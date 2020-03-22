/* global Vue hljs */
Vue.directive('hljs', {
  inserted(ele, binding) {
    const element = ele;
    element.className = binding.value.split('.').pop();

    hljs.highlightBlock(element);
  },
});

window.app = new window.Vue({
  el: '#app',
  data: {
    repos: {},
    users: [],
    repoLength: 0,
    loadedRepo: 0,
    userUpdated: false,

    isLoading: false,
    isTabActive: true, // to force tab wrapper to load

    tabType: 'empty',
    tabInfo: {
      tabAuthorship: {
        author: '',
        location: '',
        maxDate: '',
        minDate: '',
        name: '',
        repo: '',
      },
      tabZoom: {
        avgCommitSize: 0,
        filterGroupSelection: '',
        filterTimeFrame: '',
        location: '',
        isMergeGroup: false,
        sinceDate: '',
        untilDate: '',
        user: null,
      },
    },
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
      if (document.getElementById('tabs-wrapper')) {
        document.getElementById('tabs-wrapper').scrollTop = 0;
      }

      this.isTabActive = true;
      this.tabType = tabName;

      window.addHash('tabOpen', this.isTabActive);
      window.addHash('tabType', this.tabType);
      window.encodeHash();
    },

    deactivateTab() {
      this.isTabActive = false;
      window.addHash('tabOpen', this.isTabActive);
      window.removeHash('tabAuthor');
      window.removeHash('tabRepo');
      window.removeHash('tabType');
      window.encodeHash();
    },

    updateTabAuthorship(obj) {
      this.tabInfo.tabAuthorship = Object.assign({}, this.tabInfo.tabAuthorship, obj);
      this.activateTab('authorship');
    },
    updateTabZoom(obj) {
      this.tabInfo.tabZoom = Object.assign({}, this.tabInfo.tabZoom, obj);
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
        minDate,
        maxDate,
      };
      const tabInfoLength = Object.values(info).filter((x) => x).length;
      if (Object.keys(info).length === tabInfoLength) {
        this.updateTabAuthorship(info);
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
          // handle zoom tab if needed
        }
      }
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
    v_zoom: window.vZoom,
    v_summary: window.vSummary,
    v_authorship: window.vAuthorship,
    CircleSpinner: window.VueLoadingSpinner.Circle,
  },
  created() {
    this.updateReportDir();
  },
  updated() {
    this.$nextTick(() => {
      if (window.$('tabs-wrapper')) {
        window.$('tabs-wrapper').style.flex = `0 0 ${window.flexWidth * 100}%`;
      }
    });
  },
});
