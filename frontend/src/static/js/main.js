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

window.decodeHash = () => {
  const params = window.location.hash.slice(1).split('&');
  params.forEach((param) => {
    const [key, val] = param.split('=');
    if (key) {
      window.hashParams[key] = decodeURIComponent(val);
    }
  });
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
    isTabActive: true,
    isTabAuthorship: false,
    tabInfo: {},
    tabAuthorship: {},
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
    deactivateTabs() {
      this.isTabAuthorship = false;
    },

    updateTabAuthorship(obj) {
      this.deactivateTabs();
      this.tabInfo.tabAuthorship = Object.assign({}, obj);

      this.isTabActive = true;
      this.isTabAuthorship = true;
      this.isCollapsed = false;
      if (document.getElementById('tabs-wrapper')) {
        document.getElementById('tabs-wrapper').scrollTop = 0;
      }
    },

    renderAuthorShipTabHash() {
      const hash = window.hashParams;
      const info = {};
      const tabKeys = ['tabAuthor', 'tabLocation'];
      tabKeys.forEach((key) => {
        if (hash[key]) {
          const slicedName = key.charAt(3).toLowerCase() + key.slice(4);
          info[slicedName] = hash[key];
        }
      });
      if (hash.since) { info.minDate = hash.since; }
      if (hash.until) { info.maxDate = hash.until; }
      if (info.location) {
        const repoName = info.location.split('github.com/')[1].split('/');
        if (repoName.length === 2) {
          info.repo = `${repoName.join('_').slice(0, -4)}_master`;
        } else {
          repoName.splice(2, 1);
          info.repo = repoName.join('_');
        }
      }
      if (Object.keys(info).length === 5) {
        this.updateTabAuthorship(info);
      } else if (hash.tabOpen === 'false') {
        this.isTabActive = false;
      }
    },

    /* global expandAll */
    expand(isActive) {
      this.isCollapsed = !isActive;
      expandAll(isActive);
    },

    generateKey(dataObj) {
      return JSON.stringify(dataObj);
    },

    hasCommits(info) {
      return window.REPOS[info.repo].commits.authorFinalContributionMap[info.author] > 0;
    },

    removeInfoHash(hashObj) {
      const tabKeys = ['tabAuthor', 'tabLocation'];
      return hashObj.filter((value) => {
        const key = value.split('=')[0];
        return !tabKeys.includes(key);
      });
    },

    addKeyToHash(params, key, val) {
      const param = `${key}=${val}`;
      if (!params.includes(param)) {
        params.push(param);
      }
      return params;
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
    this.renderAuthorShipTabHash();
  },
  updated() {
    this.$nextTick(() => {
      if (window.$('tabs-wrapper')) {
        window.$('tabs-wrapper').style.flex = `0 0 ${flexWidth * 100}%`;
      }
    });
    if (!this.isTabActive) {
      const hashObj = window.location.hash.slice(1).split('&');
      let newHash = this.removeInfoHash(hashObj);
      newHash = this.addKeyToHash(newHash, 'tabOpen', this.isTabActive);
      window.location.hash = `#${newHash.join('&')}`;
    }
  },
});
