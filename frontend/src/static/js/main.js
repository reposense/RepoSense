window.BASE_URL = 'https://github.com';
window.REPORT_ZIP = null;
window.REPOS = {};
window.isMacintosh = navigator.platform.includes('Mac');

window.hashParams = {};
window.addHash = function addHash(newKey, newVal) {
  window.hashParams[newKey] = newVal;
};
window.removeHash = function removeHash(key) {
  delete window.hashParams[key];
};

window.encodeHash = function encodeHash() {
  const { hashParams } = window;

  window.location.hash = Object.keys(hashParams)
      .map((key) => `${key}=${encodeURIComponent(hashParams[key])}`)
      .join('&');
};

window.decodeHash = function decodeHash() {
  const hashParams = {};

  window.location.hash.slice(1).split('&')
      .forEach((param) => {
        const [key, val] = param.split('=');
        if (key) {
          try {
            hashParams[key] = decodeURIComponent(val);
          } catch (error) {
            this.userUpdated = false;
            this.isLoading = false;
          }
        }
      });
  window.hashParams = hashParams;
};

const DRAG_BAR_WIDTH = 13.25;
const SCROLL_BAR_WIDTH = 17;
const GUIDE_BAR_WIDTH = 2;

const throttledEvent = (delay, handler) => {
  let lastCalled = 0;
  return (...args) => {
    if (Date.now() - lastCalled > delay) {
      lastCalled = Date.now();
      handler(...args);
    }
  };
};

let guideWidth = (0.5 * window.innerWidth - (GUIDE_BAR_WIDTH / 2))
    / window.innerWidth;
let flexWidth = 0.5;

window.mouseMove = () => {};
window.registerMouseMove = () => {
  const innerMouseMove = (event) => {
    guideWidth = (
      Math.min(
          Math.max(
              window.innerWidth - event.clientX,
              SCROLL_BAR_WIDTH + DRAG_BAR_WIDTH,
          ),
          window.innerWidth - SCROLL_BAR_WIDTH,
      )
        - (GUIDE_BAR_WIDTH / 2)
    ) / window.innerWidth;
    window.$('tab-resize-guide').style.right = `${guideWidth * 100}%`;
  };
  window.$('tab-resize-guide').style.display = 'block';
  window.$('app-wrapper').style['user-select'] = 'none';
  window.mouseMove = throttledEvent(30, innerMouseMove);
};

window.deregisterMouseMove = () => {
  flexWidth = (guideWidth * window.innerWidth + (GUIDE_BAR_WIDTH / 2))
        / window.innerWidth;
  window.mouseMove = () => {};
  if (window.$('tabs-wrapper')) {
    window.$('tabs-wrapper').style.flex = `0 0 ${flexWidth * 100}%`;
  }
  window.$('tab-resize-guide').style.display = 'none';
  window.$('app-wrapper').style['user-select'] = 'auto';
};

/* global Vue hljs */
Vue.directive('hljs', {
  inserted(ele, binding) {
    const element = ele;
    element.className = binding.value.split('.').pop();

    hljs.highlightBlock(element);
  },
});

Vue.component('font-awesome-icon', window['vue-fontawesome'].FontAwesomeIcon);

window.app = new window.Vue({
  el: '#app',
  data: {
    repos: {},
    users: [],
    repoLength: 0,
    loadedRepo: 0,
    userUpdated: false,

    isLoading: false,
    isCollapsed: false,
    isTabActive: true, // to force tab wrapper to load

    tabType: 'empty',
    tabInfo: {},
    creationDate: '',

    errorMessages: {},
  },
  methods: {
    // model functions //
    updateReportZip(evt) {
      this.users = [];

      window.JSZip.loadAsync(evt.target.files[0])
          .then((zip) => {
            window.REPORT_ZIP = zip;
          }, () => {
            window.alert('Either the .zip file is corrupted, or you uploaded a .zip file that is not generated '
                + 'by RepoSense.');
          })
          .then(() => this.updateReportView().then(() => this.renderTabHash()));
    },
    updateReportDir() {
      window.REPORT_ZIP = null;

      this.users = [];
      this.updateReportView().then(() => this.renderTabHash());
    },
    async updateReportView() {
      await window.api.loadSummary().then((names) => {
        this.repos = window.REPOS;
        this.repoLength = Object.keys(window.REPOS).length;
        this.loadedRepo = 0;

        this.userUpdated = false;
        this.isLoading = true;
        this.loadedRepo = 0;

        return Promise.all(names.map((name) => (
          window.api.loadCommits(name)
              .then(() => { this.loadedRepo += 1; })
        )));
      }).then(() => {
        this.userUpdated = true;
        this.isLoading = false;
        this.getUsers();
      }).catch((error) => {
        this.userUpdated = false;
        this.isLoading = false;
        window.alert(error);
      });
    },
    getUsers() {
      const full = [];
      Object.keys(this.repos).forEach((repo) => {
        if (this.repos[repo].users) {
          full.push(this.repos[repo]);
        }
      });
      this.users = full;
    },

    // handle opening of sidebar //
    activateTab(tabName) {
      // changing isTabActive to trigger redrawing of component
      this.isTabActive = false;
      if (document.getElementById('tabs-wrapper')) {
        document.getElementById('tabs-wrapper').scrollTop = 0;
      }

      this.isTabActive = true;
      this.isCollapsed = false;
      this.tabType = tabName;

      window.addHash('tabOpen', this.isTabActive);
      window.addHash('tabType', this.tabType);
      window.encodeHash();
    },

    deactivateTab() {
      this.isTabActive = false;
      window.addHash('tabOpen', this.isTabActive);
      window.removeHash('tabType');
      this.removeZoomHashes();
      window.encodeHash();
    },

    removeZoomHashes() {
      window.removeHash('zA');
      window.removeHash('zR');
      window.removeHash('zACS');
      window.removeHash('zS');
      window.removeHash('zU');
      window.removeHash('zFGS');
      window.removeHash('zFTF');
      window.removeHash('zMG');
    },

    updateTabAuthorship(obj) {
      this.removeZoomHashes();
      this.tabInfo.tabAuthorship = Object.assign({}, obj);
      this.activateTab('authorship');
    },
    updateTabZoom(obj) {
      this.tabInfo.tabZoom = Object.assign({}, obj);
      this.activateTab('zoom');
    },

    // updating summary view
    updateSummaryDates(since, until) {
      this.$refs.summary.updateDateRange(since, until);
    },

    renderAuthorShipTabHash(minDate, maxDate) {
      const hash = window.hashParams;
      const info = {
        author: hash.tabAuthor,
        repo: hash.tabRepo,
        isMergeGroup: hash.authorshipIsMergeGroup === 'true',
        minDate,
        maxDate,
      };
      const tabInfoLength = Object.values(info).filter((x) => x !== null).length;
      if (Object.keys(info).length === tabInfoLength) {
        this.updateTabAuthorship(info);
      } else if (hash.tabOpen === 'false' || tabInfoLength > 2) {
        window.app.isTabActive = false;
      }
    },

    renderZoomTabHash() {
      const hash = window.hashParams;
      const zoomInfo = {
        zAuthor: hash.zA,
        zRepo: hash.zR,
        zAvgCommitSize: hash.zACS,
        zSince: hash.zS,
        zUntil: hash.zU,
        zFilterGroup: hash.zFGS,
        zTimeFrame: hash.zFTF,
        zIsMerge: hash.zMG === 'true',
      };
      const tabInfoLength = Object.values(zoomInfo).filter((x) => x !== null).length;
      if (Object.keys(zoomInfo).length === tabInfoLength) {
        this.updateTabZoom(zoomInfo);
      } else if (hash.tabOpen === 'false' || tabInfoLength > 2) {
        window.app.isTabActive = false;
      }
    },

    renderTabHash() {
      window.decodeHash();
      const hash = window.hashParams;
      if (!hash.tabOpen) {
        return;
      }
      this.isTabActive = hash.tabOpen === 'true';

      if (this.isTabActive) {
        if (hash.tabType === 'authorship') {
          let { since, until } = hash;

          // get since and until dates from window.app if not found in hash
          since = since || window.app.sinceDate;
          until = until || window.app.untilDate;
          this.renderAuthorShipTabHash(since, until);
        } else {
          this.renderZoomTabHash();
        }
      }
    },

    generateKey(dataObj) {
      return JSON.stringify(dataObj);
    },

    getRepoSenseHomeLink() {
      return 'http://reposense.org';
    },

    getUserGuideVersionLink() {
      const version = window.app.repoSenseVersion;
      if (!version) {
        return `${window.BASE_URL}/reposense/RepoSense`;
      }
      return `${window.BASE_URL}/reposense/RepoSense/blob/${version}/docs/UserGuide.md`;
    },

    receiveDates(dates) {
      const [minDate, maxDate] = dates;

      if (this.tabType === 'authorship') {
        this.renderAuthorShipTabHash(minDate, maxDate);
      }
    },
  },
  components: {
    vZoom: window.vZoom,
    vSummary: window.vSummary,
    vAuthorship: window.vAuthorship,
    CircleSpinner: window.VueLoadingSpinner.Circle,
  },
  created() {
    this.updateReportDir();
  },
  updated() {
    this.$nextTick(() => {
      if (window.$('tabs-wrapper')) {
        window.$('tabs-wrapper').style.flex = `0 0 ${flexWidth * 100}%`;
      }
    });
  },
});
