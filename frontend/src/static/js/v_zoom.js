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

    getSliceLink(slice) {
      return `${window.getBaseLink(this.info.user.repoId)}/commit/${slice.hash}`;
    },

    updateUser(repos) {
      const userOrig = repos[this.info.repoIndex][this.info.userIndex];
      const { commits } = userOrig;
      const commitsWithinDuration = commits.filter(
          (commit) => commit.date >= this.info.sinceDate && commit.date <= this.info.untilDate,
      );
      const user = { ...userOrig, commits: commitsWithinDuration };
      this.info.user = user;
    },
  },
  mounted() {
    this.$root.$on('updateFilterTimeFrame', (repos, timeFrame) => {
      this.updateUser(repos);
      this.filterTimeFrame = timeFrame;
    });
  },
  components: {
    v_ramp: window.vRamp,
  },
};
