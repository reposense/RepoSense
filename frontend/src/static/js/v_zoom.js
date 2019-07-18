window.vZoom = {
  props: ['info'],
  template: window.$('v_zoom').innerHTML,
  data: () => ({
    showAllCommitMessageBody: true,
  }),
  methods: {
    openSummary() {
      this.$emit('view-summary', {});
    },

    getSliceLink(slice) {
      return `${window.getBaseLink(this.info.user.repoId)}/commit/${slice.hash}`;
    },

    toggleAllCommitMessagesBody() {
      this.showAllCommitMessageBody = (this.showAllCommitMessageBody !== true);
    },
  },
  components: {
    v_ramp: window.vRamp,
  },
};
