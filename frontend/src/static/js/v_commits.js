window.vCommits = {
  props: ['info'],
  template: window.$('v_commits').innerHTML,
  data: () => ({
    showCommitMessageBody: true,
  }),
  methods: {
    getLinkToGithubCommit(commitResult) {
      return `${window.getBaseLink(this.info.user.repoId)}/commit/${commitResult.hash}`;
    },
    toggleShowCommitMessageBody(isActive) {
      this.showCommitMessageBody = isActive;
    },
  },
  components: {
    v_ramp: window.vRamp,
  },
};
