window.vZoom = {
  props: ['info'],
  template: window.$('v_zoom').innerHTML,
  data: () => ({}),
  methods: {
    openSummary() {
      this.$emit('view-summary', {});
    },

    getSliceLink(slice) {
      return `${window.getBaseLink(this.info.user.repoId)}/commit/${slice.hash}`;
    },
  },
  components: {
    v_ramp: window.vRamp,
  },
};
