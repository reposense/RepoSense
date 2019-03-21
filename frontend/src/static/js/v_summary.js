window.comparator = (fn) => function compare(a, b) {
  const a1 = fn(a);
  const b1 = fn(b);
  if (a1 === b1) {
    return 0;
  } if (a1 < b1) {
    return -1;
  }
  return 1;
};

// date functions //
const DAY_IN_MS = (1000 * 60 * 60 * 24);
function getIntervalDay(a, b) {
  const diff = Date.parse(a) - Date.parse(b);
  return diff / DAY_IN_MS;
}
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
      filterSort: 'displayName',
      filterSortReverse: false,
      filterGroupSelection: 'groupByRepos',
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
    filterSort() {
      this.getFiltered();
    },
    filterSortReverse() {
      this.getFiltered();
    },
    filterTimeFrame() {
      this.getFiltered();
    },
    filterGroupSelection() {
      this.getFiltered();
      this.updateSortSelection();
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
    getSlicePos(i, total) {
      return (total - i - 1) / total;
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
      addHash('sort', this.filterSort);

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
      if (hash.sort) { this.filterSort = hash.sort; }

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

      let minDate = '';
      let maxDate = '';
      this.filtered.forEach((repo) => {
        repo.forEach((user) => {
          const { commits } = user;
          if (commits.length) {
            const date1 = commits[0].date;
            const date2 = commits[commits.length - 1].date;
            if (!minDate || minDate > date1) {
              minDate = date1;
            }
            if (!maxDate || maxDate < date2) {
              maxDate = date2;
            }
          }
        });
      });

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
      const leng = commits.length;

      const res = [];
      for (let weekId = 0; weekId < (leng - 1) / 7; weekId += 1) {
        const week = {
          insertions: 0,
          deletions: 0,
          date: commits[weekId * 7].date,
        };

        for (let dayId = 0; dayId < 7; dayId += 1) {
          const commit = commits[(weekId * 7) + dayId];
          if (commit) {
            week.insertions += commit.insertions;
            week.deletions += commit.deletions;
          }
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

      if (this.filterTimeFrame === 'week') {
        sinceDate = dateRounding(sinceDate, 1);
      }
      let diff = getIntervalDay(userFirst.date, sinceDate);

      const startMs = (new Date(sinceDate)).getTime();
      for (let dayId = 0; dayId < diff; dayId += 1) {
        user.commits.push({
          insertions: 0,
          deletions: 0,
          commitResults: [],
          date: getDateStr(startMs + (dayId * DAY_IN_MS)),
        });
      }

      user.dailyCommits.forEach((commit) => {
        const { date } = commit;
        if (date >= sinceDate && date <= untilDate) {
          user.commits.push(commit);
        }
      });

      if (this.filterTimeFrame === 'week') {
        untilDate = dateRounding(untilDate);
      }
      diff = getIntervalDay(untilDate, userLast.date);

      const endMs = (new Date(userLast.date)).getTime();
      for (let paddingId = 1; paddingId < diff; paddingId += 1) {
        user.commits.push({
          insertions: 0,
          deletions: 0,
          commitResults: [],
          date: getDateStr(endMs + (paddingId * DAY_IN_MS)),
        });
      }

      return null;
    },
    updateSortSelection() {
      if (this.filterGroupSelection === 'groupByAuthors' && this.filterSort === 'displayName') {
        this.filterSort = 'searchPath';
      } else if (this.filterGroupSelection === 'groupByRepos' && this.filterSort === 'searchPath') {
        this.filterSort = 'displayName';
      }
    },
    sortFiltered() {
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
        users.sort(window.comparator((ele) => ele[this.filterSort]));
        sortedRepos.push(users);
      });
      return sortedRepos;
    },
    groupByNone(repos) {
      const sortedRepos = [];
      repos.forEach((users) => {
        users.forEach((user) => {
          sortedRepos.push(user);
        });
      });
      sortedRepos.sort(window.comparator((ele) => {
        const field = ele[this.filterSort];
        return field.toLowerCase ? field.toLowerCase() : field;
      }));
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

      Object.keys(authorMap).forEach((author) => filtered.push(authorMap[author]));
      filtered.sort(window.comparator((ele) => {
        const field = ele[0].displayName;
        return field.toLowerCase();
      }));
      return filtered;
    },
  },
  created() {
    this.renderFilterHash();
    this.getFiltered();
    this.processFileFormats();
  },
};
