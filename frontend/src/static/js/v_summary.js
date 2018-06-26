function comparator(fn) {
  return function compare(a, b) {
    const a1 = fn(a);
    const b1 = fn(b);
    if (a1 === b1) {
      return 0;
    } else if (a1 < b1) {
      return -1;
    }
    return 1;
  };
}

/* dates funcs */
const DAY_IN_MS = (1000 * 60 * 60 * 24);
function getIntervalDay(a, b) {
  const diff = Date.parse(a) - Date.parse(b);
  return diff / DAY_IN_MS;
}
function getDateStr(date) {
  return (new Date(date)).toISOString().split("T")[0];
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
  props: ["repos"],
  template: window.$("v_summary").innerHTML,
  data() {
    return {
      filtered: [],
      rampScale: 0.1,
      filterSearch: "",
      filterSort: "totalCommits",
      filterSortReverse: false,
      filterGroupRepos: true,
      filterGroupWeek: false,
      filterSinceDate: "",
      filterUntilDate: "",
      filterHash: "",
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
    filterGroupWeek() {
      this.getFiltered();
    },
    filterSinceDate() {
      this.getFiltered();
    },
    filterUntilDate() {
      this.getFiltered();
    },
  },
  computed: {
    sliceCount() {
      return this.filtered[0][0].commits.length;
    },
    sliceWidth() {
      return 100 / this.sliceCount;
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
      this.filtered.forEach((repo) => {
        repo.forEach((user) => {
          if (user.totalCommits > 0) {
            totalCount += 1;
            totalLines += user.totalCommits;
          }
        });
      });
      return totalLines / totalCount;
    },
  },
  methods: {
    // view funcs
    getWidth(slice) {
      if (slice.insertions === 0) {
        return 0;
      }

      let size = this.sliceWidth;
      size *= slice.insertions / this.avgCommitSize;
      return Math.max(size * this.rampScale, 0.5);
    },
    getSliceTitle(slice) {
      return `contribution on ${slice.fromDate
      }: ${slice.insertions} lines`;
    },
    getSliceLink(user, slice) {
      const {REPOS} = window;

      return `http://github.com/${
        REPOS[user.repoId].organization}/${
        REPOS[user.repoId].repoName}/commits/${
        REPOS[user.repoId].branch}?` +
                `author=${user.name}&` +
                `since=${slice.fromDate}&` +
                `until=${slice.toDate}`;
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
    // model funcs
    getFilterHash() {
      const {enquery} = window;

      this.filterSearch = this.filterSearch.toLowerCase();
      this.filterHash = [
        enquery("search", this.filterSearch),
        enquery("sort", this.filterSort),
        enquery("reverse", this.filterSortReverse),
        enquery("repoSort", this.filterGroupRepos),
        enquery("since", this.filterSinceDate),
        enquery("until", this.filterUntilDate),
      ].join("&");

      window.location.hash = this.filterHash;
    },
    getDates() {
      if (this.filterSinceDate && this.filterUntilDate) {
        return;
      }

      let minDate = "";
      let maxDate = "";
      this.filtered.forEach(function (repo) {
        repo.forEach(function (user) {
          const {commits} = user;
          const date1 = commits[0].fromDate;
          const date2 = commits[commits.length - 1].fromDate;
          if (!minDate || minDate > date1) {
            minDate = date1;
          }
          if (!maxDate || maxDate < date2) {
            maxDate = date2;
          }
        });
      });

      if (!this.filterSinceDate) {
        this.filterSinceDate = minDate;
      }

      if (!this.filterUntilDate) {
        this.filterUntilDate = maxDate;
      }
    },
    getFiltered() {
      this.getFilterHash();

      // array of array, sorted by repo
      const full = [];

      this.repos.forEach((repo) => {
        const res = [];

        // filtering
        repo.users.forEach((user) => {
          if (user.searchPath.search(this.filterSearch) > -1) {
            this.getUserCommits(user);
            if (this.filterGroupWeek) {
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
      this.getDates();
    },
    splitCommitsWeek(user) {
      const {commits} = user;
      const leng = commits.length;

      const res = [];
      for (let weekId = 0; weekId < (leng - 1) / 7; weekId += 1) {
        const week = {
          insertions: 0,
          deletions: 0,
          fromDate: commits[weekId * 7].fromDate,
          toDate: "",
        };

        for (let dayId = 0; dayId < 7; dayId += 1) {
          const commit = commits[(weekId * 7) + dayId];
          week.insertions += commit.insertions;
          week.deletions += commit.deletions;
          week.toDate = commit.toDate;
        }

        res.push(week);
      }

      user.commits = res;
    },
    getUserCommits(user) {
      user.commits = [];
      const userFirst = user.dailyCommits[0];
      const userLast = user.dailyCommits[user.dailyCommits.length - 1];

      let sinceDate = this.filterSinceDate;
      if (!sinceDate) {
        sinceDate = userFirst.fromDate;
      }

      let untilDate = this.filterUntilDate;
      if (!untilDate) {
        untilDate = userLast.fromDate;
      }

      if (this.filterGroupWeek) {
        sinceDate = dateRounding(sinceDate, 1);
      }
      let diff = getIntervalDay(userFirst.fromDate, sinceDate);

      const startMs = (new Date(sinceDate)).getTime();
      for (let dayId = 0; dayId < diff; dayId += 1) {
        user.commits.push({
          insertions: 0,
          deletions: 0,
          fromDate: getDateStr(startMs + (dayId * DAY_IN_MS)),
          toDate: getDateStr(startMs + ((dayId + 1) * DAY_IN_MS)),
        });
      }

      user.dailyCommits.forEach((commit) => {
        const date = commit.fromDate;
        if (date > sinceDate && date < untilDate) {
          user.commits.push(commit);
        }
      });

      if (this.filterGroupWeek) {
        untilDate = dateRounding(untilDate);
      }
      diff = getIntervalDay(untilDate, userLast.fromDate);

      const endMs = (new Date(userLast.fromDate)).getTime();
      for (let paddingId = 0; paddingId < diff; paddingId += 1) {
        user.commits.push({
          insertions: 0,
          deletions: 0,
          fromDate: getDateStr(endMs + (paddingId * DAY_IN_MS)),
          endDate: getDateStr(endMs + ((paddingId + 1) * DAY_IN_MS)),
        });
      }
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
        full[0].sort(comparator((ele) => ele[this.filterSort]));
      }

      if (this.filterSortReverse) {
        full.forEach((repo) => repo.reverse());
      }

      this.filtered = full;
    },
  },
  created() {
    this.getFiltered();
  },
};
