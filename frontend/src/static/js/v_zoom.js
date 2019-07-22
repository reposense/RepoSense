window.vZoom = {
  props: ['info'],
  template: window.$('v_zoom').innerHTML,
  data() {
    return {
      filterTimeFrame: window.hashParams.timeframe,
    };
  },
  methods: {
    initiate() {
      if (!this.info.user) { // restoring zoom tab from reloaded page
        this.restoreZoomTab();
      }
      this.filterCommits();
    },

    openSummary() {
      this.$emit('view-summary', {});
    },

    filterCommits() {
      const { user } = this.info;
      const date = this.filterTimeFrame === 'week' ? 'endDate' : 'date';
      const filtered = user.commits.filter(
          (commit) => commit[date] >= this.info.sinceDate && commit[date] <= this.info.untilDate,
      );
      user.commits = filtered;
    },

    getSliceLink(slice) {
      return `${window.getBaseLink(this.info.user.repoId)}/commit/${slice.hash}`;
    },

    restoreZoomTab() {
      const { info } = this;
      const { users } = window.REPOS[info.repo];

      const selectedUser = Object.assign({}, users.filter((user) => {
        return user.name === info.author;
      })[0]);

      this.$root.$emit('restoreCommits', selectedUser); // restore selected user's commits from v_summary
      this.info.user = selectedUser;
    },

    setInfoHash() {
      const { addHash } = window;
      const { user } = this.info;
      addHash('tabAuthor', user.name);
      addHash('tabRepo', user.repoId);
      addHash('avgCommitSize', this.info.avgCommitSize);
      addHash('zoomSince', this.info.sinceDate);
      addHash('zoomUntil', this.info.untilDate);
    },

  },
  components: {
    v_ramp: window.vRamp,
  },
  created() {
    this.initiate();
    this.setInfoHash();
  },
};
