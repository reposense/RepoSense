/* global Vuex */
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
      hasModifiedSinceDate: window.app.isSinceDateProvided,
      hasModifiedUntilDate: window.app.isUntilDateProvided,
      filterSinceDate: '',
      filterUntilDate: '',
      filterHash: '',
      minDate: '',
      maxDate: '',
      fileTypeColors: {},
      isSafariBrowser: /.*Version.*Safari.*/.test(navigator.userAgent),
      // eslint-disable-next-line new-cap
      randomGenerator: new Math.seedrandom('Seeded Random Generator'),
    };
  },
  watch: {
    checkedFileTypes() {
      this.getFiltered();
    },
    filterBreakdown() {
      this.getFiltered();
    },
    sortGroupSelection() {
      this.getFiltered();
    },

    sortWithinGroupSelection() {
      this.getFiltered();
    },

    filterTimeFrame() {
      this.getFiltered();
    },

    filterGroupSelection() {
      const { allGroupsMerged } = this;
      this.getFilteredRepos();

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

    tmpFilterSinceDate() {
      if (this.tmpFilterSinceDate && this.tmpFilterSinceDate >= this.minDate) {
        this.filterSinceDate = this.tmpFilterSinceDate;
      } else if (!this.tmpFilterSinceDate) { // If user clears the since date field
        this.filterSinceDate = this.minDate;
      }
      this.getFiltered();
    },

    tmpFilterUntilDate() {
      if (this.tmpFilterUntilDate && this.tmpFilterUntilDate <= this.maxDate) {
        this.filterUntilDate = this.tmpFilterUntilDate;
      } else if (!this.tmpFilterUntilDate) { // If user clears the until date field
        this.filterUntilDate = this.maxDate;
      }
      this.getFiltered();
    },

    '$store.state.summaryDates': function () {
      this.hasModifiedSinceDate = true;
      this.hasModifiedUntilDate = true;
      this.tmpFilterSinceDate = this.$store.state.summaryDates.since;
      this.tmpFilterUntilDate = this.$store.state.summaryDates.until;
      window.deactivateAllOverlays();
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
      },
    },

    avgContributionSize() {
      let totalLines = 0;
      let totalCount = 0;
      this.repos.forEach((repo) => {
        repo.users.forEach((user) => {
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
          this.$store.commit('updateMergedGroup', mergedGroups);
        } else {
          this.$store.commit('updateMergedGroup', []);
        }
      },
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
      return encodeURI('Unexpected error with RepoSense version ') + window.app.repoSenseVersion;
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
      window.decodeHash();

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
        // make a copy to prevent custom merged groups from overwritten
        this.customMergedGroups = hash.mergegroup;
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
      window.decodeHash();
    },

    restoreMergedGroups() {
      if (this.customMergedGroups) {
        this.$store.commit(
            'updateMergedGroup',
            this.customMergedGroups.split(window.HASH_DELIMITER),
        );
      }
    },

    getDates() {
      if (this.minDate && this.maxDate) {
        return;
      }

      const minDate = window.app.sinceDate;
      const maxDate = window.app.untilDate;

      if (!this.filterSinceDate) {
        if (!this.tmpFilterSinceDate || this.tmpFilterSinceDate < minDate) {
          this.tmpFilterSinceDate = minDate;
        }

        this.filterSinceDate = minDate;
        this.minDate = minDate;
      }

      if (!this.filterUntilDate) {
        if (!this.tmpFilterUntilDate || this.tmpFilterUntilDate > maxDate) {
          this.tmpFilterUntilDate = maxDate;
        }

        this.filterUntilDate = maxDate;
        this.maxDate = maxDate;
      }
      this.$emit('get-dates', [this.minDate, this.maxDate]);
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
      this.getDates();
      window.deactivateAllOverlays();

      this.getFilteredRepos();
      this.getMergedRepos();
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
      this.filtered = this.sortFiltered(this.filtered, filterControl);
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
      const mergedCommits = [];
      const mergedFileTypeContribution = {};
      let mergedVariance = 0;
      let totalMergedCheckedFileTypeCommits = 0;

      filtered[groupIndex].forEach((user) => {
        this.mergeCommits(user, mergedCommits, dateToIndexMap);

        this.mergeFileTypeContribution(user, mergedFileTypeContribution);

        totalMergedCheckedFileTypeCommits += user.checkedFileTypeContribution;
        mergedVariance += user.variance;
      });

      mergedCommits.sort(window.comparator((ele) => ele.date));
      filtered[groupIndex][0].commits = mergedCommits;
      filtered[groupIndex][0].fileTypeContribution = mergedFileTypeContribution;
      filtered[groupIndex][0].variance = mergedVariance;
      filtered[groupIndex][0].checkedFileTypeContribution = totalMergedCheckedFileTypeCommits;

      // only take the merged group
      filtered[groupIndex] = filtered[groupIndex].slice(0, 1);
    },

    hasMergedGroups() {
      return this.mergedGroups.length > 0;
    },

    mergeCommits(user, merged, dateToIndexMap) {
      // merge commits with the same date
      user.commits.forEach((commit) => {
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
          merged.push(commit);
        }
      });
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

    getRandomHex() {
      const maxHexColorValue = 16777214;
      // excludes #000000 and #FFFFFF as they are reserved
      return `#${Math.round(this.randomGenerator() * maxHexColorValue + 1).toString(16).padStart(6, '0')}`;
    },

    getNonRepeatingColor(existingColors) {
      let generatedHex = this.getRandomHex();
      while (this.hasSimilarExistingColors(existingColors, generatedHex)) {
        generatedHex = this.getRandomHex();
      }
      return generatedHex;
    },

    hasSimilarExistingColors(existingColors, newHex) {
      const deltaEThreshold = 11; // the lower limit of delta E to be similar, more info at http://zschuessler.github.io/DeltaE/learn/
      return existingColors.some((existingHex) => {
        const existingRGB = window.getHexToRGB(existingHex);
        const newRGB = window.getHexToRGB(newHex);
        return this.deltaE(existingRGB, newRGB) < deltaEThreshold;
      });
    },

    // this delta E (perceptual color distance) implementation taken from @antimatter15 from
    // github: https://github.com/antimatter15/rgb-lab
    deltaE(rgbA, rgbB) {
      const labA = this.rgb2lab(rgbA);
      const labB = this.rgb2lab(rgbB);
      const deltaL = labA[0] - labB[0];
      const deltaA = labA[1] - labB[1];
      const deltaB = labA[2] - labB[2];
      const c1 = Math.sqrt(labA[1] * labA[1] + labA[2] * labA[2]);
      const c2 = Math.sqrt(labB[1] * labB[1] + labB[2] * labB[2]);
      const deltaC = c1 - c2;
      let deltaH = deltaA * deltaA + deltaB * deltaB - deltaC * deltaC;
      deltaH = deltaH < 0 ? 0 : Math.sqrt(deltaH);
      const sc = 1.0 + 0.045 * c1;
      const sh = 1.0 + 0.015 * c1;
      const deltaLKLSL = deltaL / (1.0);
      const deltaCKCSC = deltaC / (sc);
      const deltaHKSHS = deltaH / (sh);
      const distance = deltaLKLSL * deltaLKLSL + deltaCKCSC * deltaCKCSC + deltaHKSHS * deltaHKSHS;
      return distance < 0 ? 0 : Math.sqrt(distance);
    },

    rgb2lab(rgb) {
      let r = rgb[0] / 255;
      let g = rgb[1] / 255;
      let b = rgb[2] / 255;
      let x;
      let y;
      let z;
      r = (r > 0.04045) ? ((r + 0.055) / 1.055) ** 2.4 : r / 12.92;
      g = (g > 0.04045) ? ((g + 0.055) / 1.055) ** 2.4 : g / 12.92;
      b = (b > 0.04045) ? ((b + 0.055) / 1.055) ** 2.4 : b / 12.92;
      x = (r * 0.4124 + g * 0.3576 + b * 0.1805) / 0.95047;
      y = (r * 0.2126 + g * 0.7152 + b * 0.0722) / 1.00000;
      z = (r * 0.0193 + g * 0.1192 + b * 0.9505) / 1.08883;
      x = (x > 0.008856) ? (x ** (1 / 3)) : (7.787 * x) + 16 / 116;
      y = (y > 0.008856) ? (y ** (1 / 3)) : (7.787 * y) + 16 / 116;
      z = (z > 0.008856) ? (z ** (1 / 3)) : (7.787 * z) + 16 / 116;
      return [(116 * y) - 16, 500 * (x - y), 200 * (y - z)];
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
                fileTypeColors[fileType] = this.getNonRepeatingColor(Object.values(fileTypeColors));
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

    sortFiltered(filtered, filterControl) {
      const { filterGroupSelection } = filterControl;
      this.getOptionWithOrder();
      let full = [];

      if (filterGroupSelection === 'groupByNone') {
        // push all repos into the same group
        full[0] = this.groupByNone(filtered, filterControl);
      } else if (filterGroupSelection === 'groupByAuthors') {
        full = this.groupByAuthors(filtered, filterControl);
      } else {
        full = this.groupByRepos(filtered, filterControl);
      }

      return full;
    },

    // updating filters programically //
    resetDateRange() {
      this.hasModifiedSinceDate = false;
      this.hasModifiedUntilDate = false;
      this.tmpFilterSinceDate = '';
      this.tmpFilterUntilDate = '';
      window.removeHash('since');
      window.removeHash('until');
    },

    updateTmpFilterSinceDate(event) {
      const since = event.target.value;
      this.hasModifiedSinceDate = true;

      if (!this.isSafariBrowser) {
        this.tmpFilterSinceDate = since;
        event.target.value = this.filterSinceDate;
        return;
      }

      if (dateFormatRegex.test(since) && since >= this.minDate) {
        this.tmpFilterSinceDate = since;
        event.currentTarget.style.removeProperty('border-bottom-color');
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
        return;
      }

      if (dateFormatRegex.test(until) && until <= this.maxDate) {
        this.tmpFilterUntilDate = until;
        event.currentTarget.style.removeProperty('border-bottom-color');
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

    groupByRepos(repos, sortingControl) {
      const sortedRepos = [];
      const {
        sortingWithinOption, sortingOption, isSortingDsc, isSortingWithinDsc,
      } = sortingControl;
      const sortWithinOption = sortingWithinOption === 'title' ? 'displayName' : sortingWithinOption;
      const sortOption = sortingOption === 'groupTitle' ? 'searchPath' : sortingOption;
      repos.forEach((users) => {
        if (sortWithinOption === 'totalCommits') {
          users.sort(window.comparator((ele) => ele.checkedFileTypeContribution));
        } else {
          users.sort(window.comparator((ele) => ele[sortWithinOption]));
        }

        if (isSortingWithinDsc) {
          users.reverse();
        }
        sortedRepos.push(users);
      });
      sortedRepos.sort(window.comparator(this.sortingHelper, sortOption));
      if (isSortingDsc) {
        sortedRepos.reverse();
      }
      return sortedRepos;
    },

    groupByNone(repos, sortingControl) {
      const sortedRepos = [];
      const { sortingOption, isSortingDsc } = sortingControl;
      const isSortingGroupTitle = sortingOption === 'groupTitle';
      repos.forEach((users) => {
        users.forEach((user) => {
          sortedRepos.push(user);
        });
      });
      sortedRepos.sort(window.comparator((repo) => {
        if (isSortingGroupTitle) {
          return repo.searchPath + repo.name;
        }
        if (sortingOption === 'totalCommits') {
          return repo.checkedFileTypeContribution;
        }
        return repo[sortingOption];
      }));
      if (isSortingDsc) {
        sortedRepos.reverse();
      }

      return sortedRepos;
    },

    groupByAuthors(repos, sortingControl) {
      const authorMap = {};
      const filtered = [];
      const {
        sortingWithinOption, sortingOption, isSortingDsc, isSortingWithinDsc,
      } = sortingControl;
      const sortWithinOption = sortingWithinOption === 'title' ? 'searchPath' : sortingWithinOption;
      const sortOption = sortingOption === 'groupTitle' ? 'displayName' : sortingOption;
      repos.forEach((users) => {
        users.forEach((user) => {
          if (Object.keys(authorMap).includes(user.name)) {
            authorMap[user.name].push(user);
          } else {
            authorMap[user.name] = [user];
          }
        });
      });
      Object.keys(authorMap).forEach((author) => {
        if (sortWithinOption === 'totalCommits') {
          authorMap[author].sort(window.comparator((repo) => repo.checkedFileTypeContribution));
        } else {
          authorMap[author].sort(window.comparator((repo) => repo[sortWithinOption]));
        }
        if (isSortingWithinDsc) {
          authorMap[author].reverse();
        }
        filtered.push(authorMap[author]);
      });

      filtered.sort(window.comparator(this.sortingHelper, sortOption));
      if (isSortingDsc) {
        filtered.reverse();
      }
      return filtered;
    },

    getGroupCommitsVariance(total, group) {
      if (this.sortingOption === 'totalCommits') {
        return total + group.checkedFileTypeContribution;
      }
      return total + group[this.sortingOption];
    },

    sortingHelper(element, sortingOption) {
      return sortingOption === 'totalCommits' || sortingOption === 'variance'
          ? element.reduce(this.getGroupCommitsVariance, 0)
          : element[0][sortingOption];
    },

    restoreZoomFiltered(info) {
      const {
        zSince, zUntil, zTimeFrame, zIsMerge, zFilterSearch,
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

      if (zIsMerge) {
        this.mergeGroupByIndex(filtered, 0);
      }
      return filtered[0][0];
    },
    matchZoomUser(info, user) {
      const {
        zIsMerge, zFilterGroup, zRepo, zAuthor,
      } = info;
      if (zIsMerge) {
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
  },
  created() {
    this.processFileTypes();
    this.renderFilterHash();
    this.getFiltered();
  },
  beforeMount() {
    this.$root.$on('restoreCommits', (info) => {
      const zoomFilteredUser = this.restoreZoomFiltered(info);
      info.zUser = zoomFilteredUser;
    });

    this.$root.$on('restoreFileTypeColors', (info) => {
      info.fileTypeColors = this.fileTypeColors;
    });
  },
  mounted() {
    this.$store.commit('updateMergedGroup', []);

    // restoring custom merged groups after watchers finish their job
    setTimeout(() => {
      this.restoreMergedGroups();
    }, 0);
  },
  components: {
    vSummaryCharts: window.vSummaryCharts,
  },
};
