function comparator(fn) {
  return function compare(a, b) {
    const a1 = fn(a);
    const b1 = fn(b);
    if (a1 === b1) {
      return 0;
    } if (a1 < b1) {
      return -1;
    }
    return 1;
  };
}

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
      filterGroupRepos: true,
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
      contributionBarFileFormatColors: {},
      contributionBarGroupColors: {},
      repoContainsGroups: {},
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
    filterGroupRepos() {
      this.getFiltered();
    },
    filterTimeFrame() {
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

    getBreakdownContributionBars(breakdownContribution) {
      let totalWidth = 0;
      const contributionLimit = (this.avgContributionSize * 2);
      const totalBars = {};
      const maxLength = 100;
      let keys = [];

      Object.keys(breakdownContribution).forEach((type) => {
        const contribution = breakdownContribution[type];
        const res = [];
        let typeWidth = 0;

        // compute 100% width bars
        const cnt = parseInt(contribution / contributionLimit, 10);
        for (let cntId = 0; cntId < cnt; cntId += 1) {
          res.push(maxLength);
          typeWidth += maxLength;
          totalWidth += maxLength;
        }

        // compute < 100% width bars
        const last = (contribution % contributionLimit) / contributionLimit;
        if (last !== 0) {
          res.push(last * maxLength);
          typeWidth += last * maxLength;
          totalWidth += last * maxLength;
        }

        // split > 100% width bars into smaller bars
        if ((totalWidth > maxLength) && (totalWidth !== typeWidth)) {
          res.unshift(maxLength - (totalWidth - typeWidth));
          res[res.length - 1] = res[res.length - 1] - (maxLength - (totalWidth - typeWidth));
          totalWidth = res[res.length - 1];
        }
        totalBars[type] = res;
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
      addHash('repoSort', this.filterGroupRepos);
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
      if (hash.repoSort) { this.filterGroupRepos = convertBool(hash.repoSort); }
      if (hash.breakdown) {
        this.filterBreakdown = convertBool(hash.breakdown);
      }
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
    processBreakdown() {
      const selectedColors = ['#ffe119', '#4363d8', '#3cb44b', '#f58231', '#911eb4', '#46f0f0', '#f032e6',
          '#bcf60c', '#fabebe', '#008080', '#e6beff', '#9a6324', '#fffac8', '#800000', '#aaffc3', '#808000', '#ffd8b1',
          '#000075', '#808080'];
      const groupColors = {};
      const fileFormatColors = {};
      const containsGroup = {};
      let i = 0;
      let j = 0;

      this.repos.forEach((repo) => {
        const user = repo.users[0];
        if (Object.keys(user.groupContribution).length !== 0) {
          containsGroup[user.repoPath] = true;
          Object.keys(user.groupContribution).forEach((group) => {
            if (!Object.prototype.hasOwnProperty.call(groupColors, group)) {
              groupColors[group] = selectedColors[i];
              i = (i + 1) % selectedColors.length;
            }
          });
        } else {
          containsGroup[user.repoPath] = false;
          Object.keys(user.fileFormatContribution).forEach((fileFormat) => {
            if (!Object.prototype.hasOwnProperty.call(fileFormatColors, fileFormat)) {
              fileFormatColors[fileFormat] = selectedColors[j];
              j = (j + 1) % selectedColors.length;
            }
          });
        }
      });

      this.contributionBarGroupColors = groupColors;
      this.contributionBarFileFormatColors = fileFormatColors;
      this.repoContainsGroups = containsGroup;
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
    sortFiltered() {
      const full = [];
      if (!this.filterGroupRepos) {
        full.push([]);
      }

      this.filtered.forEach((users) => {
        if (this.filterGroupRepos) {
          users.sort(comparator((ele) => ele[this.filterSort]));
          full.push(users);
        } else {
          users.forEach((user) => full[0].push(user));
        }
      });

      if (!this.filterGroupRepos) {
        full[0].sort(comparator((ele) => {
          const field = ele[this.filterSort];
          return field.toLowerCase ? field.toLowerCase() : field;
        }));
      }

      if (this.filterSortReverse) {
        full.forEach((repo) => repo.reverse());
      }

      this.filtered = full;
    },
  },
  created() {
    this.renderFilterHash();
    this.getFiltered();
    this.processBreakdown();
  },
};
