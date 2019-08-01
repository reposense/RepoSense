window.vZoom = {
  props: ['info'],
  template: window.$('v_zoom').innerHTML,
  data() {
    return {
      filterTimeFrame: window.hashParams.timeframe,
      showAllCommitMessageBody: true,
      expandedCommitMessagesNum: this.getCommitMessageBodyNum(),
    };
  },
  methods: {
    openSummary() {
      this.$emit('view-summary', this.info.sinceDate, this.info.untilDate);
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

    getCommitMessageBodyNum() {
      let nonEmptyCommitMessageNum = 0;
      this.info.user.commits.forEach((commit) => {
        commit.commitResults.forEach((commitResult) => {
          if (commitResult.messageBody !== '') {
            nonEmptyCommitMessageNum += 1;
          }
        });
      });

      return nonEmptyCommitMessageNum;
    },

    toggleAllCommitMessagesBody(isActive) {
      this.showAllCommitMessageBody = isActive;

      const toRename = this.showAllCommitMessageBody ? 'commit-message active' : 'commit-message';

      const commitMessageClasses = document.getElementsByClassName('commit-message');
      Array.from(commitMessageClasses).forEach((commitMessageClass) => {
        commitMessageClass.className = toRename;
      });

      this.expandedCommitMessagesNum = isActive ? this.getCommitMessageBodyNum() : 0;
    },

    updateExpandedCommitMessagesNum() {
      this.expandedCommitMessagesNum = document.getElementsByClassName('commit-message active')
          .length;
    },
  },
  created() {
    this.filterCommits();
  },
  mounted() {
    this.updateExpandedCommitMessagesNum();
  },
  components: {
    v_ramp: window.vRamp,
  },
};
