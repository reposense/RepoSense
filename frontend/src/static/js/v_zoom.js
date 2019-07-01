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

    setInfoHash() {
      const { addHash } = window;
      const { user } = this.info;
      addHash('tabAuthor', user.name);
      addHash('tabRepo', user.repoId);
    },

  },
  components: {
    v_ramp: window.vRamp,
  },

  created() {
    this.setInfoHash();
  },
};
