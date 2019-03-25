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

// ui funcs, only allow one ramp to be highlighted //
let drags = [];

function getBaseTarget(target) {
  return (target.className === 'summary-chart__ramp')
    ? target : getBaseTarget(target.parentElement);
}

function deactivateAllOverlays() {
  document.querySelectorAll('.summary-chart__ramp .overlay')
      .forEach((x) => { x.className = 'overlay'; });
}

window.dragViewDown = function dragViewDown(evt) {
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
};

window.dragViewUp = function dragViewUp(evt) {
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
};

// date functions //
const DAY_IN_MS = (1000 * 60 * 60 * 24);
window.DAY_IN_MS = DAY_IN_MS;

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
          dayCommits: [],
        };

        for (let dayId = 0; dayId < 7; dayId += 1) {
          const commit = commits[(weekId * 7) + dayId] || {};
          if (commit) {
            week.insertions += commit.insertions;
            week.deletions += commit.deletions;
          }
          week.dayCommits.push(commit);
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
          users.sort(window.comparator((ele) => ele[this.filterSort]));
          full.push(users);
        } else {
          users.forEach((user) => full[0].push(user));
        }
      });

      if (!this.filterGroupRepos) {
        full[0].sort(window.comparator((ele) => {
          const field = ele[this.filterSort];
          return field.toLowerCase ? field.toLowerCase() : field;
        }));
      }

      if (this.filterSortReverse) {
        full.forEach((repo) => repo.reverse());
      }

      this.filtered = full;
    },

    // updating filters programically //
    resetDateRange() {
      this.tmpFilterSinceDate = this.minDate;
      this.tmpFilterUntilDate = this.maxDate;
    },

    updateDateRange() {
      if (drags.length > 0) {
        const since = new Date(this.filterSinceDate).getTime();
        const until = new Date(this.filterUntilDate).getTime();
        const range = until - since;

        const getStr = (time) => getDateStr(new Date(time));
        this.tmpFilterSinceDate = getStr(since + range * drags[0] / 100);
        this.tmpFilterUntilDate = getStr(since + range * drags[1] / 100);

        drags = [];
        deactivateAllOverlays();
      }
    },

    // triggering opening of tabs //
    openTabAuthorship(user, repo) {
      const { minDate, maxDate } = this;

      this.$emit('view-authorship', {
        minDate,
        maxDate,
        author: user.name,
        repo: user.repoName,
        name: user.displayName,
        location: repo[0].location,
        totalCommits: user.totalCommits,
      });
    },

    openTabZoomin(userOrig) {
      // skip if accidentally clicked on ramp chart
      if (drags[1] - drags[0]) {
        const idxs = drags.map((x) => x * userOrig.commits.length / 100);
        const rawCommits = userOrig.commits.slice(
            parseInt(idxs[0], 10),
            parseInt(idxs[1] + 1, 10),
        );

        const commits = [];
        rawCommits.forEach((commit) => {
          if (this.filterTimeFrame === 'week') {
            commit.dayCommits.forEach((dayCommit) => commits.push(dayCommit));
          } else {
            commits.push(commit);
          }
        });

        const { avgCommitSize } = this;
        const user = { ...userOrig, commits };
        this.$emit('view-zoomin', {
          user, avgCommitSize,
        });
      }
    },
  },
  created() {
    this.renderFilterHash();
    this.getFiltered();
    this.processFileFormats();
  },
  components: {
    v_ramp: window.vRamp,
  },
};
