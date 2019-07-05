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
  },
  created() {
    this.filterCommits();
  },
  components: {
    v_ramp: window.vRamp,
  },
};
