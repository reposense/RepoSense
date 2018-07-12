window.REPORT_ZIP = null;
window.REPOS = {};

window.app = new window.Vue({
  el: '#app',
  data: {
    repos: {},
    repoLength: 0,
    loadedRepo: 0,
    userUpdated: false,

    isTabActive: false,
    isTabAuthorship: false,
    isTabIssues: false,

    tabRepo: '',
    tabAuthor: '',
    tabAuthorName: '',
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
      window.api.loadSummary((names) => {
        this.repos = window.REPOS;
        this.repoLength = Object.keys(window.REPOS).length;
        this.loadedRepo = 0;

        names.forEach((name) => {
          window.api.loadCommits(name, () => this.addUsers());
        });
      });
    },
    addUsers() {
      this.userUpdated = false;
      this.loadedRepo += 1;
      this.userUpdated = true;
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
      this.isTabIssues = false;
    },

    updateTabAuthorship(obj) {
      this.deactivateTabs();
      this.tabRepo = obj.repo;
      this.tabAuthor = obj.author;
      this.tabAuthorName = obj.name;

      this.isTabActive = true;
      this.isTabAuthorship = true;
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
