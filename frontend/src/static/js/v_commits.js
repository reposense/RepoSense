window.vCommits = {
  props: ['info'],
  template: window.$('v_commits').innerHTML,
  data: () => ({}),
  methods: {
    getLinkToGithubCommit(commitResult) {
      return `${window.getBaseLink(this.info.user.repoId)}/commit/${commitResult.hash}`;
    },
    getCommitMessageTitle(timeOfCommit) {

    }
  },
  components: {
    v_ramp: window.vRamp,
  },
};
