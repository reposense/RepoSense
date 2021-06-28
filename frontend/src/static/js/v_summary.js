/* global Vuex getFontColor */
const dateFormatRegex = /([12]\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\d|3[01]))$/;

window.vSummary = {
  props: ['repos', 'errorMessages'],
  template: window.$('v_summary').innerHTML,
  data() {
    return {
      checkedFileTypes: [],
      fileTypes: [],
      filtered: [],
      filterSearch: '',
      filterGroupSelection: 'groupByRepos',
      sortGroupSelection: 'groupTitle', // UI for sorting groups
      sortWithinGroupSelection: 'title', // UI for sorting within groups
      sortingOption: '',
      isSortingDsc: '',
      sortingWithinOption: '',
      isSortingWithinDsc: '',
      filterTimeFrame: 'commit',
      filterBreakdown: false,
      tmpFilterSinceDate: '',
      tmpFilterUntilDate: '',
      hasModifiedSinceDate: window.isSinceDateProvided,
      hasModifiedUntilDate: window.isUntilDateProvided,
      filterHash: '',
      minDate: window.sinceDate,
      maxDate: window.untilDate,
      fileTypeColors: {},
      isSafariBrowser: /.*Version.*Safari.*/.test(navigator.userAgent),
      filterGroupSelectionWatcherFlag: false,
    };
  },
  watch: {

    filterGroupSelection() {
      // Deactivates watcher
      if (!this.filterGroupSelectionWatcherFlag) {
        return;
      }
      const { allGroupsMerged } = this;

      this.$store.commit('incrementLoadingOverlayCount', 1);
      setTimeout(() => {
        this.getFilteredRepos();
        this.updateMergedGroup(allGroupsMerged);
        this.$store.commit('incrementLoadingOverlayCount', -1);
      });
    },

    '$store.state.summaryDates': function () {
      this.hasModifiedSinceDate = true;
      this.hasModifiedUntilDate = true;
      this.tmpFilterSinceDate = this.$store.state.summaryDates.since;
      this.tmpFilterUntilDate = this.$store.state.summaryDates.until;
      window.deactivateAllOverlays();
      this.getFiltered();
    },

    mergedGroups() {
      this.getFiltered();
    },
  },
  computed: {
    checkAllFileTypes: {
      get() {
        return this.checkedFileTypes.length === this.fileTypes.length;
      },
      set(value) {
        if (value) {
          this.checkedFileTypes = this.fileTypes.slice();
        } else {
          this.checkedFileTypes = [];
        }
        this.getFiltered();
      },
    },

    avgContributionSize() {
      let totalLines = 0;
      let totalCount = 0;
      this.repos.forEach((repo) => {
        repo.users.forEach((user) => {
          if (user.checkedFileTypeContribution === undefined) {
            this.updateCheckedFileTypeContribution(user);
          }
          if (user.checkedFileTypeContribution > 0) {
            totalCount += 1;
            totalLines += user.checkedFileTypeContribution;
          }
        });
      });

      return totalLines / totalCount;
    },

    allGroupsMerged: {
      get() {
        if (this.mergedGroups.length === 0) {
          return false;
        }
        return this.mergedGroups.length === this.filtered.length;
      },
      set(value) {
        if (value) {
          const mergedGroups = [];
          this.filtered.forEach((group) => {
            mergedGroups.push(this.getGroupName(group));
          });
          this.filtered = [];
          this.$store.commit('updateMergedGroup', mergedGroups);
        } else {
          this.$store.commit('updateMergedGroup', []);
        }
      },
    },

    filterSinceDate() {
      if (this.tmpFilterSinceDate && this.tmpFilterSinceDate >= this.minDate) {
        return this.tmpFilterSinceDate;
      }
      // If user clears the since date field
      return this.minDate;
    },

    filterUntilDate() {
      if (this.tmpFilterUntilDate && this.tmpFilterUntilDate <= this.maxDate) {
        return this.tmpFilterUntilDate;
      }
      return this.maxDate;
    },

    ...Vuex.mapState(['mergedGroups']),
  },
  methods: {
    dismissTab(event) {
      event.target.parentNode.style.display = 'none';
    },

    // view functions //
    getReportIssueGitHubLink(stackTrace) {
      return `${window.BASE_URL}/reposense/RepoSense/issues/new?title=${this.getReportIssueTitle()
      }&body=${this.getReportIssueMessage(stackTrace)}`;
    },

    getReportIssueEmailAddress() {
      return 'seer@comp.nus.edu.sg';
    },

    getReportIssueEmailLink(stackTrace) {
      return `mailto:${this.getReportIssueEmailAddress()}?subject=${this.getReportIssueTitle()
      }&body=${this.getReportIssueMessage(stackTrace)}`;
    },

    getReportIssueTitle() {
      return encodeURI('Unexpected error with RepoSense version ') + window.repoSenseVersion;
    },

    getReportIssueMessage(message) {
      return encodeURI(message);
    },

    // model functions //
    resetFilterSearch() {
      this.filterSearch = '';
      this.getFiltered();
    },
    updateFilterSearch(evt) {
      this.filterSearch = evt.target.value;
      this.getFiltered();
    },

    setSummaryHash() {
      const { addHash, encodeHash } = window;

      addHash('search', this.filterSearch);
      addHash('sort', this.sortGroupSelection);
      addHash('sortWithin', this.sortWithinGroupSelection);

      if (this.hasModifiedSinceDate) {
        addHash('since', this.filterSinceDate);
      }

      if (this.hasModifiedUntilDate) {
        addHash('until', this.filterUntilDate);
      }

      addHash('timeframe', this.filterTimeFrame);

      let mergedGroupsHash = this.mergedGroups.join(window.HASH_DELIMITER);
      if (mergedGroupsHash.length === 0) {
        mergedGroupsHash = '';
      }
      addHash('mergegroup', mergedGroupsHash);

      addHash('groupSelect', this.filterGroupSelection);
      addHash('breakdown', this.filterBreakdown);

      if (this.filterBreakdown) {
        const checkedFileTypesHash = this.checkedFileTypes.length > 0
          ? this.checkedFileTypes.join(window.HASH_DELIMITER)
          : '';
        addHash('checkedFileTypes', checkedFileTypesHash);
      } else {
        window.removeHash('checkedFileTypes');
      }

      encodeHash();
    },

    renderFilterHash() {
      const convertBool = (txt) => (txt === 'true');
      const hash = window.hashParams;

      if (hash.search) { this.filterSearch = hash.search; }
      if (hash.sort) {
        this.sortGroupSelection = hash.sort;
      }
      if (hash.sortWithin) {
        this.sortWithinGroupSelection = hash.sortWithin;
      }

      if (hash.timeframe) { this.filterTimeFrame = hash.timeframe; }
      if (hash.mergegroup) {
        this.$store.commit(
            'updateMergedGroup',
            hash.mergegroup.split(window.HASH_DELIMITER),
        );
      }
      if (hash.since && dateFormatRegex.test(hash.since)) {
        this.tmpFilterSinceDate = hash.since;
      }
      if (hash.until && dateFormatRegex.test(hash.until)) {
        this.tmpFilterUntilDate = hash.until;
      }

      if (hash.groupSelect) {
        this.filterGroupSelection = hash.groupSelect;
      }
      if (hash.breakdown) {
        this.filterBreakdown = convertBool(hash.breakdown);
      }
      if (hash.checkedFileTypes) {
        const parsedFileTypes = hash.checkedFileTypes.split(window.HASH_DELIMITER);
        this.checkedFileTypes = parsedFileTypes.filter((type) => this.fileTypes.includes(type));
      }
    },

    getGroupName(group) {
      return window.getGroupName(group, this.filterGroupSelection);
    },

    isMatchSearchedUser(filterSearch, user) {
      return !filterSearch || filterSearch.toLowerCase()
          .split(' ')
          .filter(Boolean)
          .some((param) => user.searchPath.includes(param));
    },

    getFiltered() {
      this.setSummaryHash();
      window.deactivateAllOverlays();

      this.$store.commit('incrementLoadingOverlayCount', 1);
      // Use setTimeout() to force this.filtered to update only after loading screen is displayed.
      setTimeout(() => {
        this.getFilteredRepos();
        this.getMergedRepos();
        this.$store.commit('incrementLoadingOverlayCount', -1);
      });
    },

    getFilteredRepos() {
      // array of array, sorted by repo
      const full = [];

      // create deep clone of this.repos to not modify the original content of this.repos
      // when merging groups
      const groups = this.hasMergedGroups() ? JSON.parse(JSON.stringify(this.repos)) : this.repos;
      groups.forEach((repo) => {
        const res = [];

        // filtering
        repo.users.forEach((user) => {
          if (this.isMatchSearchedUser(this.filterSearch, user)) {
            this.getUserCommits(user, this.filterSinceDate, this.filterUntilDate);
            if (this.filterTimeFrame === 'week') {
              this.splitCommitsWeek(user, this.filterSinceDate, this.filterUntilDate);
            }
            this.updateCheckedFileTypeContribution(user);
            res.push(user);
          }
        });

        if (res.length) {
          full.push(res);
        }
      });
      this.filtered = full;

      this.getOptionWithOrder();

      const filterControl = {
        filterGroupSelection: this.filterGroupSelection,
        sortingOption: this.sortingOption,
        sortingWithinOption: this.sortingWithinOption,
        isSortingDsc: this.isSortingDsc,
        isSortingWithinDsc: this.isSortingWithinDsc,
      };
      this.getOptionWithOrder();
      this.filtered = window.utilsSortFiltered(this.filtered, filterControl);
    },

    updateMergedGroup(allGroupsMerged) {
      // merge group is not allowed when group by none
      // also reset merged groups
      if (this.filterGroupSelection === 'groupByNone' || !allGroupsMerged) {
        this.$store.commit('updateMergedGroup', []);
      } else {
        const mergedGroups = [];
        this.filtered.forEach((group) => {
          mergedGroups.push(this.getGroupName(group));
        });
        this.$store.commit('updateMergedGroup', mergedGroups);
      }
    },

    getMergedRepos() {
      this.filtered.forEach((group, groupIndex) => {
        if (this.mergedGroups.includes(this.getGroupName(group))) {
          this.mergeGroupByIndex(this.filtered, groupIndex);
        }
      });
    },

    mergeGroupByIndex(filtered, groupIndex) {
      const dateToIndexMap = {};
      const dailyIndexMap = {};
      const mergedCommits = [];
      const mergedDailyCommits = [];
      const mergedFileTypeContribution = {};
      let mergedVariance = 0;
      let totalMergedCheckedFileTypeCommits = 0;
      filtered[groupIndex].forEach((user) => {
        user.commits.forEach((commit) => {
          this.mergeCommits(commit, user, dateToIndexMap, mergedCommits);
        });
        user.dailyCommits.forEach((commit) => {
          this.mergeCommits(commit, user, dailyIndexMap, mergedDailyCommits);
        });

        this.mergeFileTypeContribution(user, mergedFileTypeContribution);

        totalMergedCheckedFileTypeCommits += user.checkedFileTypeContribution;
        mergedVariance += user.variance;
      });
      mergedCommits.sort(window.comparator((ele) => ele.date));
      filtered[groupIndex][0].commits = mergedCommits;
      filtered[groupIndex][0].dailyCommits = mergedDailyCommits;
      filtered[groupIndex][0].fileTypeContribution = mergedFileTypeContribution;
      filtered[groupIndex][0].variance = mergedVariance;
      filtered[groupIndex][0].checkedFileTypeContribution = totalMergedCheckedFileTypeCommits;

      // only take the merged group
      filtered[groupIndex] = filtered[groupIndex].slice(0, 1);
    },

    hasMergedGroups() {
      return this.mergedGroups.length > 0;
    },

    mergeCommits(commit, user, dateToIndexMap, merged) {
      const {
        commitResults, date, insertions, deletions,
      } = commit;

      // bind repoId to each commit
      commitResults.forEach((commitResult) => {
        commitResult.repoId = user.repoId;
      });

      if (Object.prototype.hasOwnProperty.call(dateToIndexMap, date)) {
        const commitWithSameDate = merged[dateToIndexMap[date]];

        commitResults.forEach((commitResult) => {
          commitWithSameDate.commitResults.push(commitResult);
        });

        commitWithSameDate.insertions += insertions;
        commitWithSameDate.deletions += deletions;
      } else {
        dateToIndexMap[date] = Object.keys(dateToIndexMap).length;
        merged.push(JSON.parse(JSON.stringify(commit)));
      }
    },

    mergeFileTypeContribution(user, merged) {
      Object.entries(user.fileTypeContribution).forEach((fileType) => {
        const key = fileType[0];
        const value = fileType[1];

        if (!Object.prototype.hasOwnProperty.call(merged, key)) {
          merged[key] = 0;
        }
        merged[key] += value;
      });
    },

    processFileTypes() {
      const selectedColors = ['#ffe119', '#4363d8', '#3cb44b', '#f58231', '#911eb4', '#46f0f0', '#f032e6',
          '#bcf60c', '#fabebe', '#008080', '#e6beff', '#9a6324', '#fffac8', '#800000', '#aaffc3', '#808000', '#ffd8b1',
          '#000075', '#808080'];
      const fileTypeColors = {};
      let i = 0;

      this.repos.forEach((repo) => {
        repo.users.forEach((user) => {
          Object.keys(user.fileTypeContribution).forEach((fileType) => {
            if (!Object.prototype.hasOwnProperty.call(fileTypeColors, fileType)) {
              if (i < selectedColors.length) {
                fileTypeColors[fileType] = selectedColors[i];
                i += 1;
              } else {
                fileTypeColors[fileType] = window
                    .utilsGetNonRepeatingColor(Object.values(fileTypeColors));
              }
            }
            if (!this.fileTypes.includes(fileType)) {
              this.fileTypes.push(fileType);
            }
          });
        });
        this.fileTypeColors = fileTypeColors;
      });

      this.checkedFileTypes = this.fileTypes.slice();
      this.$store.commit('updateFileTypeColors', this.fileTypeColors);
    },

    splitCommitsWeek(user, sinceDate, untilDate) {
      const { commits } = user;

      const res = [];

      const nextMondayDate = this.dateRounding(sinceDate, 0); // round up for the next monday

      const nextMondayMs = (new Date(nextMondayDate)).getTime();
      const sinceMs = new Date(sinceDate).getTime();
      const untilMs = (new Date(untilDate)).getTime();

      if (nextMondayDate <= untilDate) {
        this.pushCommitsWeek(sinceMs, nextMondayMs - 1, res, commits);
        this.pushCommitsWeek(nextMondayMs, untilMs, res, commits);
      } else {
        this.pushCommitsWeek(sinceMs, untilMs, res, commits);
      }
      user.commits = res;
    },

    pushCommitsWeek(sinceMs, untilMs, res, commits) {
      const diff = Math.round(Math.abs((untilMs - sinceMs) / window.DAY_IN_MS));
      const weekInMS = window.DAY_IN_MS * 7;

      for (let weekId = 0; weekId < diff / 7; weekId += 1) {
        const startOfWeekMs = sinceMs + (weekId * weekInMS);
        const endOfWeekMs = startOfWeekMs + weekInMS - window.DAY_IN_MS;
        const endOfWeekMsWithinUntilMs = endOfWeekMs <= untilMs ? endOfWeekMs : untilMs;

        const week = {
          insertions: 0,
          deletions: 0,
          date: window.getDateStr(startOfWeekMs),
          endDate: window.getDateStr(endOfWeekMsWithinUntilMs),
          commitResults: [],
        };

        this.addLineContributionWeek(endOfWeekMsWithinUntilMs, week, commits);
        if (week.commitResults.length > 0) {
          res.push(week);
        }
      }
    },

    addLineContributionWeek(endOfWeekMs, week, commits) {
      // commits are not contiguous, meaning there are gaps of days without
      // commits, so we are going to check each commit's date and make sure
      // it is within the duration of a week
      while (commits.length > 0
          && (new Date(commits[0].date)).getTime() <= endOfWeekMs) {
        const commit = commits.shift();
        week.insertions += commit.insertions;
        week.deletions += commit.deletions;
        commit.commitResults.forEach((commitResult) => week.commitResults.push(commitResult));
      }
    },

    getUserCommits(user, sinceDate, untilDate) {
      user.commits = [];
      const userFirst = user.dailyCommits[0];
      const userLast = user.dailyCommits[user.dailyCommits.length - 1];

      if (!userFirst) {
        return null;
      }

      if (!sinceDate || sinceDate === 'undefined') {
        sinceDate = userFirst.date;
      }

      if (!untilDate) {
        untilDate = userLast.date;
      }

      user.dailyCommits.forEach((commit) => {
        const { date } = commit;
        if (date >= sinceDate && date <= untilDate) {
          const filteredCommit = JSON.parse(JSON.stringify(commit));
          this.filterCommitByCheckedFileTypes(filteredCommit);

          if (filteredCommit.commitResults.length > 0) {
            user.commits.push(filteredCommit);
          }
        }
      });

      return null;
    },

    filterCommitByCheckedFileTypes(commit) {
      const filteredCommitResults = commit.commitResults.map((result) => {
        const filteredFileTypes = this.getFilteredFileTypes(result);
        this.updateCommitResultWithFileTypes(result, filteredFileTypes);
        return result;
      }).filter((result) => Object.values(result.fileTypesAndContributionMap).length > 0);

      commit.insertions = filteredCommitResults.reduce((acc, result) => acc + result.insertions, 0);
      commit.deletions = filteredCommitResults.reduce((acc, result) => acc + result.deletions, 0);
      commit.commitResults = filteredCommitResults;
    },

    getFilteredFileTypes(commitResult) {
      return Object.keys(commitResult.fileTypesAndContributionMap)
          .filter(this.isFileTypeChecked)
          .reduce((obj, fileType) => {
            obj[fileType] = commitResult.fileTypesAndContributionMap[fileType];
            return obj;
          }, {});
    },

    isFileTypeChecked(fileType) {
      if (this.filterBreakdown) {
        return this.checkedFileTypes.includes(fileType);
      }
      return true;
    },

    updateCommitResultWithFileTypes(commitResult, filteredFileTypes) {
      commitResult.insertions = Object.values(filteredFileTypes)
          .reduce((acc, fileType) => acc + fileType.insertions, 0);
      commitResult.deletions = Object.values(filteredFileTypes)
          .reduce((acc, fileType) => acc + fileType.deletions, 0);
      commitResult.fileTypesAndContributionMap = filteredFileTypes;
    },

    getOptionWithOrder() {
      [this.sortingOption, this.isSortingDsc] = this.sortGroupSelection.split(' ');
      [this.sortingWithinOption, this.isSortingWithinDsc] = this.sortWithinGroupSelection.split(' ');
    },

    // updating filters programically //
    resetDateRange() {
      this.hasModifiedSinceDate = false;
      this.hasModifiedUntilDate = false;
      this.tmpFilterSinceDate = '';
      this.tmpFilterUntilDate = '';
      window.removeHash('since');
      window.removeHash('until');
      this.getFiltered();
    },

    updateTmpFilterSinceDate(event) {
      const since = event.target.value;
      this.hasModifiedSinceDate = true;

      if (!this.isSafariBrowser) {
        this.tmpFilterSinceDate = since;
        event.target.value = this.filterSinceDate;
        this.getFiltered();
      } else if (dateFormatRegex.test(since) && since >= this.minDate) {
        this.tmpFilterSinceDate = since;
        event.currentTarget.style.removeProperty('border-bottom-color');
        this.getFiltered();
      } else {
        // invalid since date detected
        event.currentTarget.style.borderBottomColor = 'red';
      }
    },

    updateTmpFilterUntilDate(event) {
      const until = event.target.value;
      this.hasModifiedUntilDate = true;

      if (!this.isSafariBrowser) {
        this.tmpFilterUntilDate = until;
        event.target.value = this.filterUntilDate;
        this.getFiltered();
      } else if (dateFormatRegex.test(until) && until <= this.maxDate) {
        this.tmpFilterUntilDate = until;
        event.currentTarget.style.removeProperty('border-bottom-color');
        this.getFiltered();
      } else {
        // invalid until date detected
        event.currentTarget.style.borderBottomColor = 'red';
      }
    },

    updateCheckedFileTypeContribution(ele) {
      let validCommits = 0;
      Object.keys(ele.fileTypeContribution).forEach((fileType) => {
        if (!this.filterBreakdown) {
          validCommits += ele.fileTypeContribution[fileType];
        } else if (this.checkedFileTypes.includes(fileType)) {
          validCommits += ele.fileTypeContribution[fileType];
        }
      });
      ele.checkedFileTypeContribution = validCommits;
    },

    restoreZoomFiltered(info) {
      const {
        zSince, zUntil, zTimeFrame, zIsMerged, zFilterSearch,
      } = info;
      const filtered = [];

      const groups = JSON.parse(JSON.stringify(this.repos));

      const res = [];
      groups.forEach((repo) => {
        repo.users.forEach((user) => {
          // only filter users that match with zoom user and previous searched user
          if (this.matchZoomUser(info, user) && this.isMatchSearchedUser(zFilterSearch, user)) {
            this.getUserCommits(user, zSince, zUntil);
            if (zTimeFrame === 'week') {
              this.splitCommitsWeek(user, zSince, zUntil);
            }
            this.updateCheckedFileTypeContribution(user);
            res.push(user);
          }
        });
      });

      if (res.length) {
        filtered.push(res);
      }

      if (zIsMerged) {
        this.mergeGroupByIndex(filtered, 0);
      }
      [[info.zUser]] = filtered;
      info.zFileTypeColors = this.fileTypeColors;
      info.isRefreshing = false;
      this.$store.commit('updateTabZoomInfo', info);
    },
    matchZoomUser(info, user) {
      const {
        zIsMerged, zFilterGroup, zRepo, zAuthor,
      } = info;
      if (zIsMerged) {
        return zFilterGroup === 'groupByRepos'
          ? user.repoName === zRepo
          : user.name === zAuthor;
      }
      return user.repoName === zRepo && user.name === zAuthor;
    },

    dateRounding(datestr, roundDown) {
      // rounding up to nearest monday
      const date = new Date(datestr);
      const day = date.getUTCDay();
      let datems = date.getTime();
      if (roundDown) {
        datems -= ((day + 6) % 7) * window.DAY_IN_MS;
      } else {
        datems += ((8 - day) % 7) * window.DAY_IN_MS;
      }

      return window.getDateStr(datems);
    },

    getFontColor,
  },
  created() {
    this.processFileTypes();
    this.renderFilterHash();
    this.getFiltered();
    if (this.$store.state.tabZoomInfo.isRefreshing) {
      const zoomInfo = Object.assign({}, this.$store.state.tabZoomInfo);
      this.restoreZoomFiltered(zoomInfo);
    }
  },
  mounted() {
    // Delay execution of filterGroupSelection watcher
    // to prevent clearing of merged groups
    setTimeout(() => {
      this.filterGroupSelectionWatcherFlag = true;
    }, 0);
  },
  components: {
    vSummaryCharts: window.vSummaryCharts,
  },
};
