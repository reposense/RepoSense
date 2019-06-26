window.vZoom = {
  props: ['info'],
  template: window.$('v_zoom').innerHTML,
  data() {
    return {
      filterTimeFrame: window.hashParams.timeframe,
    };
  },
  methods: {
    openSummary() {
      this.$emit('view-summary', {});
    },

    filterCommits() {
      let { user } = this.info;
      const filtered = user.commits.filter(
          (commit) => commit.date >= this.info.sinceDate && commit.date <= this.info.untilDate,
      );
      user.commits = filtered;
    },

    getSliceLink(slice) {
      return `${window.getBaseLink(this.info.user.repoId)}/commit/${slice.hash}`;
    },

    updateUser(repos) {
      const userOrig = repos[this.info.repoIndex][this.info.userIndex];
      this.info.user = Object.assign({}, userOrig);
    },
  },
  created() {
    this.filterCommits();
  },
  mounted() {
    this.$root.$on('updateFilterTimeFrame', (repos, timeFrame) => {
      this.filterTimeFrame = timeFrame;
      this.updateUser(repos);
      this.filterCommits();
    });
  },
  components: {
    v_ramp: window.vRamp,
  },
};
