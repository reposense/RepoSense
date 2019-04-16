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

// date functions //
const DAY_IN_MS = (1000 * 60 * 60 * 24);
const WEEK_IN_MS = DAY_IN_MS * 7;
function getDateStr(date) {
  return (new Date(date)).toISOString().split('T')[0];
}
function dateRounding(datestr, roundDown) {
  // rounding up to nearest sunday
  const date = new Date(datestr);
  const day = date.getUTCDay();
  let datems = date.getTime();
  if (roundDown) {
    datems -= day * DAY_IN_MS;
  } else {
    datems += (7 - day) * DAY_IN_MS;
  }

  return getDateStr(datems);
}

function addDays(dateStr, numDays) {
  const date = new Date(dateStr);
  return getDateStr(date.getTime() + numDays * DAY_IN_MS);
}

window.vSummary = {
  props: ['repos'],
  template: window.$('v_summary').innerHTML,
  data() {
    return {
      filtered: [],
      filterSearch: '',
      filterSortReverse: false,
      filterGroupSelection: 'groupByRepos',
      sortGroupSelection: 'searchPath', // UI for sorting groups
      sortWithinGroupSelection: 'name', // UI for sorting within groups
      sortingOption: '',
      isSortingDsc: '',
      sortingWithinOption: '',
      isSortingWithinDsc: '',
      filterTimeFrame: 'day',
      filterBreakdown: false,
      tmpFilterSinceDate: '',
      tmpFilterUntilDate: '',
      filterSinceDate: '',
      filterUntilDate: '',
      filterHash: '',
      rampSize: 0.01,
      minDate: '',
      maxDate: '',
      contributionBarColors: {},
    };
  },
  watch: {
    repos() {
      this.getFiltered();
    },
    sortGroupSelection() {
      this.getFiltered();
    },
    sortWithinGroupSelection() {
      this.getFiltered();
    },
    filterSortReverse() {
      this.getFiltered();
    },
    filterTimeFrame() {
      this.getFiltered();
    },
    filterGroupSelection() {
      this.updateSortSelection();
      this.getFiltered();
    },
    filterBreakdown() {
      this.getFiltered();
    },
    tmpFilterSinceDate() {
      if (this.tmpFilterSinceDate && this.tmpFilterSinceDate >= this.minDate) {
        this.filterSinceDate = this.tmpFilterSinceDate;
        this.getFiltered();
      }
    },
    tmpFilterUntilDate() {
      if (this.tmpFilterUntilDate && this.tmpFilterUntilDate <= this.maxDate) {
        this.filterUntilDate = this.tmpFilterUntilDate;
        this.getFiltered();
      }
    },
  },
  computed: {
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
    getWidth(slice) {
      if (slice.insertions === 0) {
        return 0;
      }

      const newSize = 100 * (slice.insertions / this.avgCommitSize);
      return Math.max(newSize * this.rampSize, 0.5);
    },
    // position for commit granularity
    getCommitPos(i, total, sinceDate, untilDate) {
      return (total - i - 1) * DAY_IN_MS / total
          / (this.getTotalForPos(sinceDate, untilDate) + DAY_IN_MS);
    },
    // position for day granularity
    getSlicePos(date, sinceDate, untilDate) {
      const total = this.getTotalForPos(sinceDate, untilDate);
      return (new Date(untilDate) - new Date(date)) / (total + DAY_IN_MS);
    },
    // get duration in miliseconds between 2 date
    getTotalForPos(sinceDate, untilDate) {
      return new Date(untilDate) - new Date(sinceDate);
    },
    getSliceColor(date) {
      const timeMs = (new Date(date)).getTime();
      return (timeMs / DAY_IN_MS) % 5;
    },
    getSliceLink(user, slice) {
      const { REPOS } = window;
      const untilDate = this.filterTimeFrame === 'week' ? addDays(slice.date, 6) : slice.date;

      return `http://github.com/${
        REPOS[user.repoId].location.organization}/${
        REPOS[user.repoId].location.repoName}/commits/${
        REPOS[user.repoId].branch}?`
                + `author=${user.name}&`
                + `since=${slice.date}'T'00:00:00+08:00&`
                + `until=${untilDate}'T'23:59:59+08:00`;
    },
    getFileFormatContributionBars(fileFormatContribution) {
      let totalWidth = 0;
      const contributionLimit = (this.avgContributionSize * 2);
      const totalBars = {};
      const maxLength = 100;

      Object.keys(fileFormatContribution).forEach((fileFormat) => {
        const contribution = fileFormatContribution[fileFormat];
        const res = [];
        let fileFormatWidth = 0;

        // compute 100% width bars
        const cnt = parseInt(contribution / contributionLimit, 10);
        for (let cntId = 0; cntId < cnt; cntId += 1) {
          res.push(maxLength);
          fileFormatWidth += maxLength;
          totalWidth += maxLength;
        }

        // compute < 100% width bars
        const last = (contribution % contributionLimit) / contributionLimit;
        if (last !== 0) {
          res.push(last * maxLength);
          fileFormatWidth += last * maxLength;
          totalWidth += last * maxLength;
        }

        // split > 100% width bars into smaller bars
        if ((totalWidth > maxLength) && (totalWidth !== fileFormatWidth)) {
          res.unshift(maxLength - (totalWidth - fileFormatWidth));
          res[res.length - 1] = res[res.length - 1] - (maxLength - (totalWidth - fileFormatWidth));
          totalWidth = res[res.length - 1];
        }
        totalBars[fileFormat] = res;
      });

      return totalBars;
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

    // model functions //
    updateFilterSearch(evt) {
      this.filterSearch = evt.target.value;
    },
    setSummaryHash() {
      const { addHash, encodeHash } = window;

      addHash('search', this.filterSearch);
      addHash('sort', this.sortGroupSelection);
      addHash('sortWithin', this.sortWithinGroupSelection);

      addHash('since', this.filterSinceDate);
      addHash('until', this.filterUntilDate);
      addHash('timeframe', this.filterTimeFrame);

      addHash('reverse', this.filterSortReverse);
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
      if (hash.since) {
        this.tmpFilterSinceDate = hash.since;
      }
      if (hash.until) {
        this.tmpFilterUntilDate = hash.until;
      }

      if (hash.reverse) { this.filterSortReverse = convertBool(hash.reverse); }
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

      // array of array, sorted by repo
      const full = [];

      this.repos.forEach((repo) => {
        const res = [];

        // filtering
        repo.users.forEach((user) => {
          const toDisplay = this.filterSearch.toLowerCase()
              .split(' ').filter((param) => param)
              .map((param) => user.searchPath.search(param) > -1)
              .reduce((curr, bool) => curr || bool, false);

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

      this.getDates();
      this.sortFiltered();
    },
    processFileFormats() {
      const selectedColors = ['#ffe119', '#4363d8', '#3cb44b', '#f58231', '#911eb4', '#46f0f0', '#f032e6',
          '#bcf60c', '#fabebe', '#008080', '#e6beff', '#9a6324', '#fffac8', '#800000', '#aaffc3', '#808000', '#ffd8b1',
          '#000075', '#808080'];
      const colors = {};
      let i = 0;

      this.repos.forEach((repo) => {
        const user = repo.users[0];
        Object.keys(user.fileFormatContribution).forEach((fileFormat) => {
          if (!Object.prototype.hasOwnProperty.call(colors, fileFormat)) {
            colors[fileFormat] = selectedColors[i];
            i = (i + 1) % selectedColors.length;
          }
        });
        this.contributionBarColors = colors;
      });
    },
    splitCommitsWeek(user) {
      const { commits } = user;

      const res = [];

      const sinceDate = dateRounding(this.filterSinceDate, 1);
      const untilDate = this.filterUntilDate;

      const sinceMs = (new Date(sinceDate)).getTime();
      const untilMs = (new Date(untilDate)).getTime();

      const diff = Math.round(Math.abs((untilMs - sinceMs) / DAY_IN_MS));

      for (let weekId = 0; weekId < diff / 7; weekId += 1) {
        const startOfWeekMs = sinceMs + (weekId * WEEK_IN_MS);

        const week = {
          insertions: 0,
          deletions: 0,
          date: getDateStr(startOfWeekMs),
        };

        // commits are not contiguous, meaning there are gaps of days without
        // commits, so we are going to check each commit's date and make sure
        // it is within the duration of a week
        while (commits.length > 0
            && (new Date(commits[0].date)).getTime() < startOfWeekMs + WEEK_IN_MS) {
          const commit = commits.shift();
          week.insertions += commit.insertions;
          week.deletions += commit.deletions;
        }

        res.push(week);
      }

      user.commits = res;
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
    updateSortSelection() {
      this.getOptionWithOrder();
      // Update UI selection to change all illegal options
      if (this.filterGroupSelection === 'groupByAuthors') {
        if (!this.sortWithinGroupSelection || this.sortingWithinOption === 'name') {
          this.sortWithinGroupSelection = 'searchPath';
        }
        if (this.sortingOption !== 'name') {
          this.sortGroupSelection = 'name';
        }
      } else if (this.filterGroupSelection === 'groupByRepos') {
        if (!this.sortWithinGroupSelection || this.sortingWithinOption === 'searchPath') {
          this.sortWithinGroupSelection = 'name';
        }
        if (this.sortingOption !== 'searchPath') {
          this.sortGroupSelection = 'searchPath';
        }
      } else if (this.filterGroupSelection === 'groupByNone') {
        this.sortWithinGroupSelection = '';
      }
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

      if (this.filterSortReverse) {
        full.forEach((repo) => repo.reverse());
      }

      this.filtered = full;
    },

    groupByRepos(repos) {
      const sortedRepos = [];
      repos.forEach((users) => {
        users.sort(window.comparator((ele) => ele[this.sortingWithinOption]));
        if (this.isSortingWithinDsc) {
          users.reverse();
        }
        sortedRepos.push(users);
      });
      sortedRepos.sort(window.comparator((repo) => repo[0][this.sortingOption]));
      if (this.isSortingDsc) {
        sortedRepos.reverse();
      }
      return sortedRepos;
    },
    groupByNone(repos) {
      const sortedRepos = [];
      repos.forEach((users) => {
        users.forEach((user) => {
          sortedRepos.push(user);
        });
      });
      sortedRepos.sort(window.comparator((ele) => ele[this.sortingOption]));
      if (this.isSortingDsc) {
        sortedRepos.reverse();
      }

      return sortedRepos;
    },
    groupByAuthors(repos) {
      const authorMap = {};
      const filtered = [];
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
        authorMap[author].sort(window.comparator((repo) => repo[this.sortingWithinOption]));
        if (this.isSortingWithinDsc) {
          authorMap[author].reverse();
        }
        filtered.push(authorMap[author]);
      });

      filtered.sort(window.comparator((ele) => ele[0][this.sortingOption]));
      if (this.isSortingDsc) {
        filtered.reverse();
      }
      return filtered;
    },
  },
  created() {
    this.renderFilterHash();
    this.getFiltered();
    this.processFileFormats();
  },
};
