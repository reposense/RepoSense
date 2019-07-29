window.toggleCommitBodyMessage = function toggleCommitBodyMessage(ele) {
  // function for expanding/collapsing a commit message body.
  const activeClass = 'commit-message--body-active';
  const inactiveClass = 'commit-message--body';

  const commitMessageBodyElemet = ele.nextElementSibling;

  commitMessageBodyElemet.className = commitMessageBodyElemet.className === activeClass
    ? inactiveClass
    : activeClass;
};

window.vZoom = {
  props: ['info'],
  template: window.$('v_zoom').innerHTML,
  data() {
    return {
      filterTimeFrame: window.hashParams.timeframe,
      showAllCommitMessageBody: true,
      numExpandedCommitMessages: this.getNumCommitMessageBody(),
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

    getNumCommitMessageBody() {
      let numNonEmptyCommitMessageBody = 0;
      this.info.user.commits.forEach((commit) => {
        commit.commitResults.forEach((commitResult) => {
          if (commitResult.messageBody !== '') {
            numNonEmptyCommitMessageBody += 1;
          }
        });
      });

      return numNonEmptyCommitMessageBody;
    },

    toggleAllCommitMessagesBody(isActive) {
      this.showAllCommitMessageBody = isActive;

      const renameValue = this.showAllCommitMessageBody
        ? 'commit-message--body-active'
        : 'commit-message--body';

      const commitMessageClasses = isActive
        ? document.getElementsByClassName('commit-message--body')
        : document.getElementsByClassName('commit-message--body-active');
      Array.from(commitMessageClasses).forEach((commitMessageClass) => {
        commitMessageClass.className = renameValue;
      });

      this.numExpandedCommitMessages = isActive ? this.getNumCommitMessageBody() : 0;
    },

    updateNumExpandedCommitMessages() {
      this.numExpandedCommitMessages = document.getElementsByClassName('commit-message--body-active').length;
    },
  },
  created() {
    this.filterCommits();
  },
  components: {
    v_ramp: window.vRamp,
  },
};
