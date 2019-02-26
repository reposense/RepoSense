window.vZoomin = {
  props: ['info'],
  template: window.$('v_zoomin').innerHTML,
  data: () => ({}),
  computed: {
    avgSize() {
      const commits = this.info.user.commits;
      const totalLines = commits.reduce((curr, commit) => curr + commit.insertions, 0);
      const totalCommits = commits.reduce((curr, commit) => curr + commit.commitResults.length, 0);

      return totalLines/totalCommits;
    }
  },
  components: {
    v_ramp: window.vRamp,
  },
};
