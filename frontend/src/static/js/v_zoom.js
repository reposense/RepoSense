window.vZoom = {
  props: ['info'],
  template: window.$('v_zoom').innerHTML,
  data() {
    return {
      filterTimeFrame: 'commit',
    };
  },
  methods: {
    openSummary() {
      this.$emit('view-summary', {});
    },

    getSliceLink(slice) {
      return `${window.getBaseLink(this.info.user.repoId)}/commit/${slice.hash}`;
    },

    getFilterTimeFrame() {
      window.decodeHash();
      const hash = window.hashParams;
      if (hash.timeframe) {
        this.filterTimeFrame = hash.timeframe;
      }
    },
  },
  components: {
    v_ramp: window.vRamp,
  },
  created() {
    this.getFilterTimeFrame();
  },
};
