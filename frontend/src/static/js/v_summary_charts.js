/* global Vuex */
window.vSummaryCharts = {
  props: ['checkedFileTypes', 'filtered', 'avgContributionSize', 'filterBreakdown',
      'filterGroupSelection', 'filterTimeFrame', 'filterSinceDate', 'filterUntilDate', 'isMergeGroup',
      'minDate', 'maxDate', 'filterSearch'],
  template: window.$('v_summary_charts').innerHTML,
  data() {
    return {
      drags: [],
      activeRepo: null,
      activeUser: null,
      activeTabType: null,
      isTabOnMergedGroup: false,
    };
  },

  computed: {
    avgCommitSize() {
      let totalCommits = 0;
      let totalCount = 0;
      this.filteredRepos.forEach((repo) => {
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

    ...Vuex.mapState(['mergedGroups', 'fileTypeColors']),
  },
  methods: {
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

    getGroupTotalContribution(group) {
      return group.reduce((acc, ele) => acc + ele.checkedFileTypeContribution, 0);
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
      this.removeSelectedTab();
      return repo.location;
    },

    // triggering opening of tabs //
    openTabAuthorship(user, repo, index, isMerged) {
      const {
        minDate, maxDate, checkedFileTypes,
      } = this;

      const info = {
        minDate,
        maxDate,
        checkedFileTypes,
        author: user.name,
        repo: user.repoName,
        name: user.displayName,
        isMergeGroup: isMerged,
        location: this.getRepoLink(repo[index]),
        repoIndex: index,
      };
      this.addSelectedTab(user.name, user.repoName, 'authorship', isMerged);
      this.$store.commit('updateTabAuthorshipInfo', info);
    },

    openTabZoomSubrange(user, evt, isMerged) {
      const isKeyPressed = window.isMacintosh ? evt.metaKey : evt.ctrlKey;

      if (isKeyPressed) {
        if (this.drags.length === 0) {
          this.dragViewDown(evt);
        } else {
          this.dragViewUp(evt);
        }
      }

      // skip if accidentally clicked on ramp chart
      if (this.drags.length === 2 && this.drags[1] - this.drags[0]) {
        const tdiff = new Date(this.filterUntilDate) - new Date(this.filterSinceDate);
        const idxs = this.drags.map((x) => x * tdiff / 100);
        const tsince = window.getDateStr(new Date(this.filterSinceDate).getTime() + idxs[0]);
        const tuntil = window.getDateStr(new Date(this.filterSinceDate).getTime() + idxs[1]);
        this.drags = [];
        this.openTabZoom(user, tsince, tuntil, isMerged);
      }
    },

    openTabZoom(user, since, until, isMerged) {
      const {
        avgCommitSize, filterGroupSelection, filterTimeFrame, filterSearch,
      } = this;
      const clonedUser = Object.assign({}, user); // so that changes in summary won't affect zoom
      const info = {
        zRepo: user.repoName,
        zAuthor: user.name,
        zFilterGroup: filterGroupSelection,
        zTimeFrame: filterTimeFrame,
        zAvgCommitSize: avgCommitSize,
        zUser: clonedUser,
        zLocation: this.getRepoLink(user),
        zSince: since,
        zUntil: until,
        zIsMerged: isMerged,
        zFileTypeColors: this.fileTypeColors,
        zFromRamp: false,
        zFilterSearch: filterSearch,
      };
      this.addSelectedTab(user.name, user.repoName, 'zoom', isMerged);
      this.$store.commit('updateTabZoomInfo', info);
    },

    getBaseTarget(target) {
      return target.className === 'summary-chart__ramp'
          ? target
          : this.getBaseTarget(target.parentElement);
    },

    dragViewDown(evt) {
      window.deactivateAllOverlays();

      const pos = evt.clientX;
      const ramp = this.getBaseTarget(evt.target);
      this.drags = [pos];

      const base = ramp.offsetWidth;
      const offset = ramp.parentElement.offsetLeft;

      const overlay = ramp.getElementsByClassName('overlay')[0];
      overlay.style.marginLeft = '0';
      overlay.style.width = `${(pos - offset) * 100 / base}%`;
      overlay.className += ' edge';
    },

    dragViewUp(evt) {
      window.deactivateAllOverlays();
      const ramp = this.getBaseTarget(evt.target);

      const base = ramp.offsetWidth;
      this.drags.push(evt.clientX);
      this.drags.sort((a, b) => a - b);

      const offset = ramp.parentElement.offsetLeft;
      this.drags = this.drags.map((x) => (x - offset) * 100 / base);

      const overlay = ramp.getElementsByClassName('overlay')[0];
      overlay.style.marginLeft = `${this.drags[0]}%`;
      overlay.style.width = `${this.drags[1] - this.drags[0]}%`;
      overlay.className += ' show';
    },

    getPercentile(index) {
      if (this.filterGroupSelection === 'groupByNone') {
        return (Math.round((index + 1) * 1000 / this.filtered[0].length) / 10).toFixed(1);
      }
      return (Math.round((index + 1) * 1000 / this.filtered.length) / 10).toFixed(1);
    },

    getGroupName(group) {
      return window.getGroupName(group, this.filterGroupSelection);
    },

    isGroupMerged(groupName) {
      return this.mergedGroups.includes(groupName);
    },

    handleMergeGroup(groupName) {
      const info = this.mergedGroups;
      info.push(groupName);
      this.$store.commit('updateMergedGroup', info);
    },

    handleExpandGroup(groupName) {
      const info = this.mergedGroups.filter((x) => x !== groupName);
      this.$store.commit('updateMergedGroup', info);
    },

    retrieveSelectedTabHash() {
      const hash = window.hashParams;

      if (hash.tabAuthor) {
        this.activeUser = hash.tabAuthor;
      } else if (hash.zA) {
        this.activeUser = hash.zA;
      }

      if (hash.tabRepo) {
        this.activeRepo = hash.tabRepo;
      } else if (hash.zR) {
        this.activeRepo = hash.zR;
      }

      if (hash.isTabOnMergedGroup) {
        if (this.filterGroupSelection === 'groupByAuthors') {
          this.activeRepo = null;
        } else if (this.filterGroupSelection === 'groupByRepos') {
          this.activeUser = null;
        }
        this.isTabOnMergedGroup = true;
      }

      if (hash.tabType) {
        this.activeTabType = hash.tabType;
      }
    },

    addSelectedTab(userName, repo, tabType, isMerged) {
      if (!isMerged || this.filterGroupSelection === 'groupByAuthors') {
        this.activeUser = userName;
      } else {
        this.activeUser = null;
      }

      if (isMerged && this.filterGroupSelection === 'groupByAuthors') {
        this.activeRepo = null;
      } else {
        this.activeRepo = repo;
      }

      if (isMerged) {
        window.addHash('isTabOnMergedGroup', 'true');
        this.isTabOnMergedGroup = true;
      } else {
        window.removeHash('isTabOnMergedGroup');
        this.isTabOnMergedGroup = false;
      }

      this.activeTabType = tabType;
      window.encodeHash();
    },

    removeSelectedTab() {
      this.activeUser = null;
      this.activeRepo = null;
      this.activeTabType = null;

      window.removeHash('isTabOnMergedGroup');
      window.encodeHash();
    },

    isSelectedTab(userName, repo, tabType, isMerged) {
      if (!isMerged) {
        return this.activeUser === userName && this.activeRepo === repo
            && this.activeTabType === tabType;
      }

      if (this.filterGroupSelection === 'groupByAuthors') {
        return this.activeUser === userName && this.activeTabType === tabType;
      }

      return this.activeRepo === repo && this.activeTabType === tabType;
    },

    isSelectedGroup(userName, repo, isMerged) {
      return (this.isTabOnMergedGroup || isMerged)
          && ((this.filterGroupSelection === 'groupByRepos' && this.activeRepo === repo)
          || (this.filterGroupSelection === 'groupByAuthors' && this.activeUser === userName));
    },
  },
  created() {
    this.retrieveSelectedTabHash();
  },
  components: {
    vRamp: window.vRamp,
  },
};
