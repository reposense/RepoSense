const commitSortDict = {
  lineOfCode: (commit) => commit.insertions,
  time: (commit) => commit.date,
};

window.vZoom = {
  props: ['info'],
  template: window.$('v_zoom').innerHTML,
  data() {
    return {
      filterTimeFrame: window.hashParams.timeframe,
      showAllCommitMessageBody: true,
      expandedCommitMessagesCount: this.totalCommitMessageBodyCount,
      commitsSortType: 'time',
      toReverseSortedCommits: true,
    };
  },

  computed: {
    sortingFunction() {
      return (a, b) => (this.toReverseSortedCommits ? -1 : 1)
      * window.comparator(commitSortDict[this.commitsSortType])(a, b);
    },
    filteredUser() {
      const { user } = this.info;
      const filteredUser = Object.assign({}, user);

      const date = this.filterTimeFrame === 'week' ? 'endDate' : 'date';
      filteredUser.commits = user.commits.filter(
          (commit) => commit[date] >= this.info.sinceDate && commit[date] <= this.info.untilDate,
      ).sort(this.sortingFunction);

      return filteredUser;
    },
    totalCommitMessageBodyCount() {
      let nonEmptyCommitMessageCount = 0;
      this.filteredUser.commits.forEach((commit) => {
        commit.commitResults.forEach((commitResult) => {
          if (commitResult.messageBody !== '' && commitResult.insertions > 0) {
            nonEmptyCommitMessageCount += 1;
          }
        });
      });

      return nonEmptyCommitMessageCount;
    },
  },
  methods: {
    openSummary() {
      this.$emit('view-summary', this.info.sinceDate, this.info.untilDate);
    },

    getSliceLink(slice) {
      if (this.info.isMergeGroup) {
        return `${window.getBaseLink(slice.repoId)}/commit/${slice.hash}`;
      }
      return `${window.getBaseLink(this.info.user.repoId)}/commit/${slice.hash}`;
    },

    scrollToCommit(commit) {
      const el = this.$el.getElementsByClassName(commit)[0];
      if (el) {
        el.scrollIntoView();
      }
    },

    toggleAllCommitMessagesBody(isActive) {
      this.showAllCommitMessageBody = isActive;

      const toRename = this.showAllCommitMessageBody ? 'commit-message message-body active' : 'commit-message message-body';

      const commitMessageClasses = document.getElementsByClassName('commit-message message-body');
      Array.from(commitMessageClasses).forEach((commitMessageClass) => {
        commitMessageClass.className = toRename;
      });

      this.expandedCommitMessagesCount = isActive ? this.totalCommitMessageBodyCount : 0;
    },

    updateExpandedCommitMessagesCount() {
      this.expandedCommitMessagesCount = document.getElementsByClassName('commit-message message-body active')
          .length;
    },
  },
  mounted() {
    this.updateExpandedCommitMessagesCount();
  },
  components: {
    v_ramp: window.vRamp,
  },
};
