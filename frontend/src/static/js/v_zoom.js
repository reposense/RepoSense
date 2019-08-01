window.toggleCommitBodyMessage = function toggleCommitBodyMessage(ele) {
  // function for expanding/collapsing a commit message body.
  const activeClass = 'body active';
  const inactiveClass = 'body';

  const commitMessageBodyElement = ele.nextElementSibling;

  commitMessageBodyElement.className = commitMessageBodyElement.className === activeClass
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

      const renameValue = this.showAllCommitMessageBody ? 'body active' : 'body';

      const commitMessageClasses = document.getElementsByClassName('body');
      Array.from(commitMessageClasses).forEach((commitMessageClass) => {
        commitMessageClass.className = renameValue;
      });

      this.expandedCommitMessagesNum = isActive ? this.getCommitMessageBodyNum() : 0;
    },

    updateExpandedCommitMessagesNum() {
      this.expandedCommitMessagesNum = document.getElementsByClassName('body active').length;
    },
  },
  created() {
    this.filterCommits();
  },
  components: {
    v_ramp: window.vRamp,
  },
};
