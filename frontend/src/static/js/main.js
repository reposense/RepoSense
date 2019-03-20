window.REPORT_ZIP = null;
window.REPOS = {};

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

window.app = new window.Vue({
  el: '#app',
  data: {
    repos: {},
    repoLength: 0,
    loadedRepo: 0,
    userUpdated: false,

    isLoading: false,
    isCollapsed: false,
    isTabActive: true, // to force tab wrapper to load

    tabType: 'empty',
    tabInfo: {},
    creationDate: '',
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
        this.isLoading = true;
        this.loadedRepo = 0;

        return Promise.all(names.map((name) => (
          window.api.loadCommits(name)
              .then(() => { this.loadedRepo += 1; })
        )));
      }).then(() => {
        this.userUpdated = true;
        this.isLoading = false;
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
      return full;
    },

    deactivateTab() {
      this.isTabActive = false;
      if (document.getElementById('tabs-wrapper')) {
        document.getElementById('tabs-wrapper').scrollTop = 0;
      }
    },

    updateTabAuthorship(obj) {
      this.deactivateTab();
      this.tabInfo.tabAuthorship = Object.assign({}, obj);

      this.isTabActive = true;
      this.isCollapsed = false;
      this.tabType = 'authorship';
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

    generateKey(dataObj) {
      return JSON.stringify(dataObj);
    },

    receiveDates(dates) {
      const [minDate, maxDate] = dates;
      this.renderAuthorShipTabHash(minDate, maxDate);
    },
  },
  components: {
    v_summary: window.vSummary,
    v_authorship: window.vAuthorship,
    CircleSpinner: window.VueLoadingSpinner.Circle,
  },
  created() {
    this.updateReportDir();
    window.decodeHash();
  },
  updated() {
    this.$nextTick(() => {
      if (window.$('tabs-wrapper')) {
        window.$('tabs-wrapper').style.flex = `0 0 ${flexWidth * 100}%`;
      }
    });
    if (!this.isTabActive) {
      window.removeHash('tabAuthor');
      window.removeHash('tabRepo');
      window.addHash('tabOpen', this.isTabActive);
      window.encodeHash();
    }
  },
});
