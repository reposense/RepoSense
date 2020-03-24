window.dismissTab = function dismissTab(node) {
  const parent = node.parentNode;
  parent.style.display = 'none';
};

window.comparator = (fn) => function compare(a, b) {
  const a1 = fn(a).toLowerCase ? fn(a).toLowerCase() : fn(a);
  const b1 = fn(b).toLowerCase ? fn(b).toLowerCase() : fn(b);
  if (a1 === b1) {
    return 0;
  } if (a1 < b1) {
    return -1;
  }
  return 1;
};

// ui funcs, only allow one ramp to be highlighted //
let drags = [];

function getBaseTarget(target) {
  return (target.className === 'summary-chart__ramp')
      ? target
      : getBaseTarget(target.parentElement);
}

function deactivateAllOverlays() {
  document.querySelectorAll('.summary-chart__ramp .overlay')
      .forEach((x) => { x.className = 'overlay'; });
}

function dragViewDown(evt) {
  deactivateAllOverlays();

  const pos = evt.clientX;
  const ramp = getBaseTarget(evt.target);
  drags = [pos];

  const base = ramp.offsetWidth;
  const offset = ramp.parentElement.offsetLeft;

  const overlay = ramp.getElementsByClassName('overlay')[0];
  overlay.style.marginLeft = '0';
  overlay.style.width = `${(pos - offset) * 100 / base}%`;
  overlay.className += ' edge';
}

function dragViewUp(evt) {
  deactivateAllOverlays();
  const ramp = getBaseTarget(evt.target);

  const base = ramp.offsetWidth;
  drags.push(evt.clientX);
  drags.sort((a, b) => a - b);

  const offset = ramp.parentElement.offsetLeft;
  drags = drags.map((x) => (x - offset) * 100 / base);

  const overlay = ramp.getElementsByClassName('overlay')[0];
  overlay.style.marginLeft = `${drags[0]}%`;
  overlay.style.width = `${drags[1] - drags[0]}%`;
  overlay.className += ' show';
}

window.viewClick = function viewClick(evt) {
  const isKeyPressed = this.isMacintosh ? evt.metaKey : evt.ctrlKey;
  if (drags.length === 2) {
    drags = [];
  }

  if (isKeyPressed) {
    return drags.length === 0
        ? dragViewDown(evt)
        : dragViewUp(evt);
  }

  return null;
};

// date functions //
const DAY_IN_MS = (1000 * 60 * 60 * 24);
window.DAY_IN_MS = DAY_IN_MS;
const WEEK_IN_MS = DAY_IN_MS * 7;
const dateFormatRegex = /([12]\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\d|3[01]))$/;


function getDateStr(date) {
  return (new Date(date)).toISOString().split('T')[0];
}
function dateRounding(datestr, roundDown) {
  // rounding up to nearest monday
  const date = new Date(datestr);
  const day = date.getUTCDay();
  let datems = date.getTime();
  if (roundDown) {
    datems -= ((day + 6) % 7) * DAY_IN_MS;
  } else {
    datems += ((8 - day) % 7) * DAY_IN_MS;
  }

  return getDateStr(datems);
}

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
      isMergeGroup: false,
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
      // merge group is not allowed when group by none
      if (this.filterGroupSelection === 'groupByNone') {
        this.isMergeGroup = false;
      }

      this.getFiltered();
    },
    isMergeGroup() {
      this.getFiltered();
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
    avgCommitSize() {
      let totalCommits = 0;
      let totalCount = 0;
      this.filtered.forEach((repo) => {
        repo.forEach((user) => {
          user.commits.forEach((slice) => {
            if (slice.insertions > 0) {
              totalCount += 1;
              totalCommits += slice.insertions;
            }
          });
        });
      });
      return totalCommits / totalCount;
    },
    avgContributionSize() {
      let totalLines = 0;
      let totalCount = 0;
      this.repos.forEach((repo) => {
        repo.users.forEach((user) => {
          if (user.totalCommits > 0) {
            totalCount += 1;
            totalLines += user.totalCommits;
          }
        });
      });

      return totalLines / totalCount;
    },
    filteredRepos() {
      return this.filtered.filter((repo) => repo.length > 0);
    },
  },
  methods: {
    // view functions //
    getFileTypeContributionBars(fileTypeContribution) {
      let currentBarWidth = 0;
      const fullBarWidth = 100;
      const contributionPerFullBar = (this.avgContributionSize * 2);
      const allFileTypesContributionBars = {};

      Object.keys(fileTypeContribution)
          .filter((fileType) => this.checkedFileTypes.includes(fileType))
          .forEach((fileType) => {
            const contribution = fileTypeContribution[fileType];
            let barWidth = (contribution / contributionPerFullBar) * fullBarWidth;
            const contributionBars = [];

            // if contribution bar for file type is able to fit on the current line
            if (currentBarWidth + barWidth < fullBarWidth) {
              contributionBars.push(barWidth);
              currentBarWidth += barWidth;
            } else {
              // take up all the space left on the current line
              contributionBars.push(fullBarWidth - currentBarWidth);
              barWidth -= fullBarWidth - currentBarWidth;
              // additional bar width will start on a new line
              const numOfFullBars = Math.floor(barWidth / fullBarWidth);
              for (let i = 0; i < numOfFullBars; i += 1) {
                contributionBars.push(fullBarWidth);
              }
              const remainingBarWidth = barWidth % fullBarWidth;
              if (remainingBarWidth !== 0) {
                contributionBars.push(remainingBarWidth);
              }
              currentBarWidth = remainingBarWidth;
            }

            allFileTypesContributionBars[fileType] = contributionBars;
          });

      return allFileTypesContributionBars;
    },
    getFileTypes(repo) {
      const fileTypes = [];
      repo.forEach((user) => {
        Object.keys(user.fileTypeContribution).forEach((fileType) => {
          if (this.checkedFileTypes.includes(fileType) && !fileTypes.includes(fileType)) {
            fileTypes.push(fileType);
          }
        });
      });
      return fileTypes;
    },
    getContributionBars(totalContribution) {
      const res = [];
      const contributionLimit = (this.avgContributionSize * 2);

      const cnt = parseInt(totalContribution / contributionLimit, 10);
      for (let cntId = 0; cntId < cnt; cntId += 1) {
        res.push(100);
      }

      const last = (totalContribution % contributionLimit) / contributionLimit;
      if (last !== 0) {
        res.push(last * 100);
      }

      return res;
    },

    getAuthorProfileLink(userName) {
      return `https://github.com/${userName}`;
    },

    getRepoLink(repo) {
      const { REPOS } = window;
      const { location, branch } = REPOS[repo.repoId];

      if (Object.prototype.hasOwnProperty.call(location, 'organization')) {
        return `${window.BASE_URL}/${location.organization}/${location.repoName}/tree/${branch}`;
      }

      return repo.location;
    },

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
      addHash('mergegroup', this.isMergeGroup);

      addHash('groupSelect', this.filterGroupSelection);
      addHash('breakdown', this.filterBreakdown);

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
        this.isMergeGroup = convertBool(hash.mergegroup);
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
      window.decodeHash();
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
    getFiltered() {
      this.setSummaryHash();
      this.getDates();
      deactivateAllOverlays();

      // array of array, sorted by repo
      const full = [];

      // create deep clone of this.repos to not modify the original content of this.repos
      // when merging groups
      const groups = this.isMergeGroup ? JSON.parse(JSON.stringify(this.repos)) : this.repos;
      groups.forEach((repo) => {
        const res = [];

        // filtering
        repo.users.forEach((user) => {
          const toDisplay = this.filterSearch.toLowerCase()
              .split(' ').filter(Boolean)
              .some((param) => user.searchPath.includes(param));

          if (!this.filterSearch || toDisplay) {
            this.getUserCommits(user);
            if (this.filterTimeFrame === 'week') {
              this.splitCommitsWeek(user);
            }

            res.push(user);
          }
        });

        if (res.length) {
          full.push(res);
        }
      });
      this.filtered = full;

      this.sortFiltered();

      if (this.isMergeGroup) {
        this.mergeGroup();
      }
    },
    mergeGroup() {
      this.filtered.forEach((group, groupIndex) => {
        const dateToIndexMap = {};
        const mergedCommits = [];
        const mergedFileTypeContribution = {};
        let mergedVariance = 0;
        let totalMergedCommits = 0;

        group.forEach((user) => {
          this.mergeCommits(user, mergedCommits, dateToIndexMap);

          this.mergeFileTypeContribution(user, mergedFileTypeContribution);

          totalMergedCommits += user.totalCommits;
          mergedVariance += user.variance;
        });

        mergedCommits.sort(window.comparator((ele) => ele.date));
        group[0].commits = mergedCommits;
        group[0].fileTypeContribution = mergedFileTypeContribution;
        group[0].totalCommits = totalMergedCommits;
        group[0].variance = mergedVariance;

        // clear all users and add merged group in filtered group
        this.filtered[groupIndex] = [];
        this.filtered[groupIndex].push(group[0]);
      });
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
              fileTypeColors[fileType] = selectedColors[i];
              i = (i + 1) % selectedColors.length;
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
    getFontColor(color) {
      // to convert color from hex code to rgb format
      const result = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(color);
      const red = parseInt(result[1], 16);
      const green = parseInt(result[2], 16);
      const blue = parseInt(result[3], 16);

      const luminosity = 0.2126 * red + 0.7152 * green + 0.0722 * blue; // per ITU-R BT.709

      return luminosity < 120 ? '#ffffff' : '#000000';
    },
    splitCommitsWeek(user) {
      const { commits } = user;

      const res = [];

      const nextMondayDate = dateRounding(this.filterSinceDate, 0); // round up for the next monday
      const untilDate = this.filterUntilDate;

      const nextMondayMs = (new Date(nextMondayDate)).getTime();
      const sinceMs = new Date(this.filterSinceDate).getTime();
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
      const diff = Math.round(Math.abs((untilMs - sinceMs) / DAY_IN_MS));

      for (let weekId = 0; weekId < diff / 7; weekId += 1) {
        const startOfWeekMs = sinceMs + (weekId * WEEK_IN_MS);
        const endOfWeekMs = startOfWeekMs + WEEK_IN_MS - DAY_IN_MS;
        const endOfWeekMsWithinUntilMs = endOfWeekMs <= untilMs ? endOfWeekMs : untilMs;

        const week = {
          insertions: 0,
          deletions: 0,
          date: getDateStr(startOfWeekMs),
          endDate: getDateStr(endOfWeekMsWithinUntilMs),
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
    getUserCommits(user) {
      user.commits = [];
      const userFirst = user.dailyCommits[0];
      const userLast = user.dailyCommits[user.dailyCommits.length - 1];

      if (!userFirst) {
        return null;
      }

      let sinceDate = this.filterSinceDate;
      if (!sinceDate || sinceDate === 'undefined') {
        sinceDate = userFirst.date;
      }

      let untilDate = this.filterUntilDate;
      if (!untilDate) {
        untilDate = userLast.date;
      }

      user.dailyCommits.forEach((commit) => {
        const { date } = commit;
        if (date >= sinceDate && date <= untilDate) {
          user.commits.push(commit);
        }
      });

      return null;
    },
    getOptionWithOrder() {
      [this.sortingOption, this.isSortingDsc] = this.sortGroupSelection.split(' ');
      [this.sortingWithinOption, this.isSortingWithinDsc] = this.sortWithinGroupSelection.split(' ');
    },
    sortFiltered() {
      this.getOptionWithOrder();
      let full = [];
      if (this.filterGroupSelection === 'groupByNone') {
        // push all repos into the same group
        full[0] = this.groupByNone(this.filtered);
      } else if (this.filterGroupSelection === 'groupByAuthors') {
        full = this.groupByAuthors(this.filtered);
      } else {
        full = this.groupByRepos(this.filtered);
      }

      this.filtered = full;
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

    updateDateRange(since, until) {
      this.hasModifiedSinceDate = true;
      this.hasModifiedUntilDate = true;
      this.tmpFilterSinceDate = since;
      this.tmpFilterUntilDate = until;
      deactivateAllOverlays();
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

    // triggering opening of tabs //
    openTabAuthorship(user, repo, index) {
      const { minDate, maxDate } = this;

      this.$emit('view-authorship', {
        minDate,
        maxDate,
        author: user.name,
        repo: user.repoName,
        name: user.displayName,
        location: this.getRepoLink(repo[index]),
        totalCommits: user.totalCommits,
      });
    },
    openTabZoomSubrange(user, repo, index) {
      // skip if accidentally clicked on ramp chart
      if (drags.length === 2 && drags[1] - drags[0]) {
        const tdiff = new Date(this.filterUntilDate) - new Date(this.filterSinceDate);
        const idxs = drags.map((x) => x * tdiff / 100);
        const tsince = getDateStr(new Date(this.filterSinceDate).getTime() + idxs[0]);
        const tuntil = getDateStr(new Date(this.filterSinceDate).getTime() + idxs[1]);
        this.openTabZoom(user, tsince, tuntil, repo, index);
      }
    },

    openTabZoom(user, since, until, repo, index) {
      const { avgCommitSize } = this;
      const clonedUser = Object.assign({}, user); // so that changes in summary won't affect zoom
      this.$emit('view-zoom', {
        filterGroupSelection: this.filterGroupSelection,
        filterTimeFrame: this.filterTimeFrame,
        avgCommitSize,
        user: clonedUser,
        location: this.getRepoLink(repo[index]),
        sinceDate: since,
        untilDate: until,
        isMergeGroup: this.isMergeGroup,
      });
    },

    getFileTypeContribution(ele) {
      let validCommits = 0;
      Object.keys(ele.fileTypeContribution).forEach((fileType) => {
        if (this.checkedFileTypes.includes(fileType)) {
          validCommits += ele.fileTypeContribution[fileType];
        }
      });
      return validCommits;
    },

    groupByRepos(repos) {
      const sortedRepos = [];
      const sortingWithinOption = this.sortingWithinOption === 'title' ? 'displayName' : this.sortingWithinOption;
      const sortingOption = this.sortingOption === 'groupTitle' ? 'searchPath' : this.sortingOption;
      repos.forEach((users) => {
        if (this.filterBreakdown && this.sortingWithinOption === 'totalCommits') {
          users.sort(window.comparator((ele) => this.getFileTypeContribution(ele)));
        } else {
          users.sort(window.comparator((ele) => ele[sortingWithinOption]));
        }

        if (this.isSortingWithinDsc) {
          users.reverse();
        }
        sortedRepos.push(users);
      });
      sortedRepos.sort(window.comparator((repo) => {
        if (sortingOption === 'totalCommits' || sortingOption === 'variance') {
          return repo.reduce(this.getGroupCommitsVariance, 0);
        }
        return repo[0][sortingOption];
      }));
      if (this.isSortingDsc) {
        sortedRepos.reverse();
      }
      return sortedRepos;
    },
    groupByNone(repos) {
      const sortedRepos = [];
      const isSortingGroupTitle = this.sortingOption === 'groupTitle';
      repos.forEach((users) => {
        users.forEach((user) => {
          sortedRepos.push(user);
        });
      });
      sortedRepos.sort(window.comparator((repo) => {
        if (isSortingGroupTitle) {
          return repo.searchPath + repo.name;
        }
        if (this.filterBreakdown && this.sortingOption === 'totalCommits') {
          return this.getFileTypeContribution(repo);
        }
        return repo[this.sortingOption];
      }));
      if (this.isSortingDsc) {
        sortedRepos.reverse();
      }

      return sortedRepos;
    },
    groupByAuthors(repos) {
      const authorMap = {};
      const filtered = [];
      const sortingWithinOption = this.sortingWithinOption === 'title' ? 'searchPath' : this.sortingWithinOption;
      const sortingOption = this.sortingOption === 'groupTitle' ? 'displayName' : this.sortingOption;
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
        if (this.filterBreakdown && this.sortingWithinOption === 'totalCommits') {
          authorMap[author].sort(window.comparator((repo) => this.getFileTypeContribution(repo)));
        } else {
          authorMap[author].sort(window.comparator((repo) => repo[sortingWithinOption]));
        }
        if (this.isSortingWithinDsc) {
          authorMap[author].reverse();
        }
        filtered.push(authorMap[author]);
      });

      filtered.sort(window.comparator((author) => {
        if (sortingOption === 'totalCommits' || sortingOption === 'variance') {
          return author.reduce(this.getGroupCommitsVariance, 0);
        }
        return author[0][sortingOption];
      }));
      if (this.isSortingDsc) {
        filtered.reverse();
      }
      return filtered;
    },

    getPercentile(index) {
      if (this.filterGroupSelection === 'groupByNone') {
        return (Math.round((index + 1) * 1000 / this.filtered[0].length) / 10).toFixed(1);
      }
      return (Math.round((index + 1) * 1000 / this.filtered.length) / 10).toFixed(1);
    },

    getGroupCommitsVariance(total, group) {
      if (this.filterBreakdown && this.sortingOption === 'totalCommits') {
        return total + this.getFileTypeContribution(group);
      }
      return total + group[this.sortingOption];
    },

    getGroupTotalContribution(group) {
      return group.reduce((accContribution, user) => accContribution + user.totalCommits, 0);
    },
  },
  created() {
    this.renderFilterHash();
    this.getFiltered();
    this.processFileTypes();
  },
  components: {
    v_ramp: window.vRamp,
  },
};
