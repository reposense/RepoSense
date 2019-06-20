window.vZoom = {
  props: ['info'],
  template: window.$('v_zoom').innerHTML,
  data: () => ({
    showCommitMessageBody: true,
  }),
  methods: {
    openSummary() {
      this.$emit('view-summary', {});
    },

    getSliceLink(slice) {
      return `${window.getBaseLink(this.info.user.repoId)}/commit/${slice.hash}`;
    },

    toggleShowCommitMessageBody() {
      this.showCommitMessageBody = !this.showCommitMessageBody;
    }
  },
  components: {
    v_ramp: window.vRamp,
  },
};
