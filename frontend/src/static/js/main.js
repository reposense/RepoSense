window.REPORT_ZIP = null;
window.REPOS = {};

window.hashParams = {};
window.addHash = function addHash(newKey, newVal) {
  const { hashParams } = window;
  hashParams[newKey] = newVal;

  const hash = [];
  const enquery = (key, val) => `${key}=${encodeURIComponent(val)}`;
  Object.keys(hashParams).forEach((hashKey) => {
    hash.push(enquery(hashKey, hashParams[hashKey]));
  });

  window.location.hash = hash.join('&');
};

const DRAG_BAR_WIDTH = 13.25;
const SCROLL_BAR_WIDTH = 17;
const GUIDE_BAR_WIDTH = 3;

window.app = new window.Vue({
  el: '#app',
  data: {
    repos: {},
    repoLength: 0,
    loadedRepo: 0,
    userUpdated: false,

    isCollapsed: false,
    isTabActive: false,
    isTabAuthorship: false,
    tabInfo: {},
    tabAuthorship: {},
    creationDate: '',

    showResizeGuide: false,
    guideWidth: 0.5,
    flexWidth: 0.5,
    mouseMove: () => {},
    appWrapperUserSelect: 'auto',

    DRAG_BAR_WIDTH,
    GUIDE_BAR_WIDTH,
  },
  methods: {
    // model functions //
    updateReportZip(evt) {
      this.users = [];

      window.JSZip.loadAsync(evt.target.files[0])
        .then((zip) => {
          window.REPORT_ZIP = zip;
        })
        .then(() => this.updateReportView());
    },
    updateReportDir() {
      window.REPORT_ZIP = null;

      this.users = [];
      this.updateReportView();
    },
    updateReportView() {
      window.api.loadSummary().then((names) => {
        this.repos = window.REPOS;
        this.repoLength = Object.keys(window.REPOS).length;
        this.loadedRepo = 0;

        this.userUpdated = false;
        this.loadedRepo = 0;

        return Promise.all(names.map(name => (
          window.api.loadCommits(name)
            .then(() => { this.loadedRepo += 1; })
        )));
      }).then(() => {
        this.userUpdated = true;
      });
    },
    getUsers() {
      const full = [];
      Object.keys(this.repos).forEach((repo) => {
        if (this.repos[repo].users) {
          full.push(this.repos[repo]);
        }
      });
      return full;
    },
    deactivateTabs() {
      this.isTabAuthorship = false;
    },

    updateTabAuthorship(obj) {
      this.deactivateTabs();
      this.tabInfo.tabAuthorship = Object.assign({}, obj);

      this.isTabActive = true;
      this.isTabAuthorship = true;
      this.isCollapsed = false;
      if (document.getElementById("tabs-wrapper")) {
        document.getElementById("tabs-wrapper").scrollTop = 0;
      }
    },

    /*global expandAll*/
    expand(isActive) {
      this.isCollapsed = !isActive;
      expandAll(isActive);
    },

    registerMouseMove(event) {
      const _mouseMove = (event) => {
        this.guideWidth =
          Math.min(
            Math.max(
              window.innerWidth - event.clientX + (DRAG_BAR_WIDTH / 2),
              SCROLL_BAR_WIDTH + (DRAG_BAR_WIDTH / 2)
            ),
            window.innerWidth - (DRAG_BAR_WIDTH / 2)
          ) / window.innerWidth;
      };
      this.showResizeGuide = true;
      this.appWrapperUserSelect = 'none';
      this.mouseMove = _mouseMove;
    },

    deregisterMouseMove() {
      this.flexWidth = this.guideWidth;
      this.showResizeGuide = false;
      this.mouseMove = () => {};
      this.appWrapperUserSelect = 'auto';
    },

    generateKey(dataObj) {
      return JSON.stringify(dataObj);
    },
  },
  components: {
    v_summary: window.vSummary,
    v_authorship: window.vAuthorship,
  },
  created() {
    this.updateReportDir();
  },
});
