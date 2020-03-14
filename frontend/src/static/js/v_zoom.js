const commitSortDict = {
  lineOfCode: (commit) => commit.insertions,
  time: (commit) => commit.date,
};

window.vZoom = {
  props: {
    info: Object,
  },
  template: window.$('v_zoom').innerHTML,
  data() {
    return {
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
      const {
        user, sinceDate, untilDate, filterTimeFrame,
      } = this.info;
      const filteredUser = Object.assign({}, user);

      const date = filterTimeFrame === 'week' ? 'endDate' : 'date';
      filteredUser.commits = user.commits.filter(
          (commit) => commit[date] >= sinceDate && commit[date] <= untilDate,
      ).sort(this.sortingFunction);

      return filteredUser;
    },
    totalCommitMessageBodyCount() {
      let nonEmptyCommitMessageCount = 0;
      this.filteredUser.commits.forEach((commit) => {
        commit.commitResults.forEach((commitResult) => {
          if (commitResult.messageBody !== '') {
            nonEmptyCommitMessageCount += 1;
          }
        });
      });

      return nonEmptyCommitMessageCount;
    },
  },
  watch: {
    info() {
      this.updateExpandedCommitMessagesCount();
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

    scrollToCommit(tag, commit) {
      const el = this.$el.getElementsByClassName(`${commit} ${tag}`)[0];
      if (el) {
        el.focus();
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
