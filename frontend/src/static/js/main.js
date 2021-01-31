/* global Vuex */
// eslint-disable-next-line import/extensions
import store from './store.js';

/* global Vue hljs */
Vue.directive('hljs', {
  inserted(ele, binding) {
    const element = ele;
    element.className = binding.value.split('.').pop();

    hljs.highlightBlock(element);
  },
});

Vue.component('font-awesome-icon', window['vue-fontawesome'].FontAwesomeIcon);
Vue.component('loading-overlay', window.VueLoading);

const loadingResourcesMessage = 'Loading resources...';

window.app = new window.Vue({
  el: '#app',
  store,
  data: {
    repos: {},
    users: [],
    userUpdated: false,

    isLoadingOverlayEnabled: false,
    loadingOverlayOpacity: 1,

    isCollapsed: false,
    isTabActive: true, // to force tab wrapper to load

    tabType: 'empty',
    tabInfo: {},
    creationDate: '',

    errorMessages: {},
  },
  watch: {
    '$store.state.tabZoomInfo': function () {
      this.tabInfo.tabZoom = Object.assign({}, this.$store.state.tabZoomInfo);
      this.activateTab('zoom');
    },
    '$store.state.tabAuthorshipInfo': function () {
      this.tabInfo.tabAuthorship = Object.assign({}, this.$store.state.tabAuthorshipInfo);
      this.activateTab('authorship');
    },
    '$store.state.loadingOverlayCount': function () {
      this.isLoadingOverlayEnabled = this.$store.state.loadingOverlayCount > 0;
    },
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
          .then(() => this.updateReportView());
    },

    updateReportDir() {
      window.REPORT_ZIP = null;

      this.users = [];
      this.updateReportView();
    },

    async updateReportView() {
      this.$store.commit('incrementLoadingOverlayCount', 1);
      this.$store.commit('updateLoadingOverlayMessage', loadingResourcesMessage);
      this.userUpdated = false;
      try {
        const names = await window.api.loadSummary();
        if (names === null) {
          return;
        }
        this.repos = window.REPOS;
        await Promise.all(names.map((name) => (
          window.api.loadCommits(name)
        )));
        this.userUpdated = true;
        this.loadingOverlayOpacity = 0.5;
        this.getUsers();
        this.renderTabHash();
      } catch (error) {
        window.alert(error);
      } finally {
        this.$store.commit('incrementLoadingOverlayCount', -1);
      }
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
      if (this.$refs.tabWrapper) {
        this.$refs.tabWrapper.scrollTop = 0;
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
      window.encodeHash();
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
        this.$store.commit('updateTabAuthorshipInfo', info);
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
        zFilterSearch: hash.zFS,
        zTimeFrame: hash.zFTF,
        zIsMerge: hash.zMG === 'true',
        zFromRamp: hash.zFR === 'true',
      };
      const tabInfoLength = Object.values(zoomInfo).filter((x) => x !== null).length;
      if (Object.keys(zoomInfo).length === tabInfoLength) {
        this.$store.commit('updateTabZoomInfo', zoomInfo);
      } else if (hash.tabOpen === 'false' || tabInfoLength > 2) {
        window.app.isTabActive = false;
      }
    },

    renderTabHash() {
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

    generateKey(dataObj, keysToUse) {
      const picked = keysToUse.map((key) => dataObj[key]);
      return JSON.stringify(picked);
    },

    getRepoSenseHomeLink() {
      const version = window.app.repoSenseVersion;
      if (!version) {
        return `${window.HOME_PAGE_URL}/RepoSense/`;
      }
      return `${window.HOME_PAGE_URL}`;
    },

    getSpecificCommitLink() {
      const version = window.app.repoSenseVersion;
      if (!version) {
        return `${window.BASE_URL}/reposense/RepoSense`;
      }
      if (version.startsWith('v')) {
        return `${window.BASE_URL}/reposense/RepoSense/releases/tag/${version}`;
      }
      return `${window.BASE_URL}/reposense/RepoSense/commit/${version}`;
    },

    getUserGuideLink() {
      const version = window.app.repoSenseVersion;
      if (!version) {
        return `${window.HOME_PAGE_URL}/RepoSense/ug/index.html`;
      }
      return `${window.HOME_PAGE_URL}/ug/index.html`;
    },

    getUsingReportsUserGuideLink() {
      const version = window.app.repoSenseVersion;
      if (!version) {
        return `${window.HOME_PAGE_URL}/RepoSense/ug/usingReports.html`;
      }
      return `${window.HOME_PAGE_URL}/ug/usingReports.html`;
    },

    receiveDates(dates) {
      const [minDate, maxDate] = dates;

      if (this.tabType === 'authorship') {
        this.renderAuthorShipTabHash(minDate, maxDate);
      }
    },
  },

  computed: {
    ...Vuex.mapState(['loadingOverlayMessage']),
  },

  components: {
    vResizer: window.vResizer,
    vZoom: window.vZoom,
    vSummary: window.vSummary,
    vAuthorship: window.vAuthorship,
  },
  created() {
    window.decodeHash();
    this.updateReportDir();
  },
});
