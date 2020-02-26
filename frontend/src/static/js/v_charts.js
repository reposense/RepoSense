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

function dragViewDown(evt) {
  window.deactivateAllOverlays();

  const pos = evt.clientX;
  const ramp = getBaseTarget(evt.target);
  drags = [pos];

  const base = ramp.offsetWidth;
  const offset = ramp.parentElement.parentElement.offsetLeft;

  const overlay = ramp.getElementsByClassName('overlay')[0];
  overlay.style.marginLeft = '0';
  overlay.style.width = `${(pos - offset) * 100 / base}%`;
  overlay.className += ' edge';
}

function dragViewUp(evt) {
  window.deactivateAllOverlays();
  const ramp = getBaseTarget(evt.target);

  const base = ramp.offsetWidth;
  drags.push(evt.clientX);
  drags.sort((a, b) => a - b);

  const offset = ramp.parentElement.parentElement.offsetLeft;
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

window.vCharts = {
  props: ['filtered', 'contributionBarFileTypeColors', 'avgContributionSize', 'filterBreakdown', 'filterGroupSelection',
      'filterTimeFrame', 'filterSinceDate', 'filterUntilDate', 'isMergeGroup', 'minDate', 'maxDate'],
  template: window.$('v_charts').innerHTML,
  data() {
    return {
    };
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

    filteredRepos() {
      return this.filtered.filter((repo) => repo.length > 0);
    },
  },
  methods: {
    getFileTypeContributionBars(fileTypeContribution) {
      let currentBarWidth = 0;
      const fullBarWidth = 100;
      const contributionPerFullBar = (this.avgContributionSize * 2);
      const allFileTypesContributionBars = {};

      Object.keys(fileTypeContribution).forEach((fileType) => {
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
          if (!fileTypes.includes(fileType)) {
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
        const tsince = window.getDateStr(new Date(this.filterSinceDate).getTime() + idxs[0]);
        const tuntil = window.getDateStr(new Date(this.filterSinceDate).getTime() + idxs[1]);
        this.openTabZoom(user, tsince, tuntil, repo, index);
      }
    },

    openTabZoom(user, since, until, repo, index) {
      const { avgCommitSize } = this;

      this.$emit('view-zoom', {
        filterGroupSelection: this.filterGroupSelection,
        avgCommitSize,
        user,
        location: this.getRepoLink(repo[index]),
        sinceDate: since,
        untilDate: until,
        isMergeGroup: this.isMergeGroup,
      });
    },

    getPercentile(index) {
      if (this.filterGroupSelection === 'groupByNone') {
        return (Math.round((index + 1) * 1000 / this.filtered[0].length) / 10).toFixed(1);
      }
      return (Math.round((index + 1) * 1000 / this.filtered.length) / 10).toFixed(1);
    },

    getGroupTotalContribution(group) {
      return group.reduce((accContribution, user) => accContribution + user.totalCommits, 0);
    },
  },
  components: {
    v_ramp: window.vRamp,
  },
};
