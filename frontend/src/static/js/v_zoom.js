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
      expandedCommitMessagesCount: this.getCommitMessageBodyCount(),
      commitsSortType: 'time',
      toReverseSortedCommits: false,
      sortingFunction: window.comparator(commitSortDict.time),
    };
  },

  watch: {
    commitsSortType() {
      this.sortCommits();
    },

    toReverseSortedCommits() {
      this.sortCommits();
    },
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
      if (this.info.isMergeGroup) {
        return `${window.getBaseLink(slice.repoId)}/commit/${slice.hash}`;
      }
      return `${window.getBaseLink(this.info.user.repoId)}/commit/${slice.hash}`;
    },

    getCommitMessageBodyCount() {
      let nonEmptyCommitMessageCount = 0;
      this.info.user.commits.forEach((commit) => {
        commit.commitResults.forEach((commitResult) => {
          if (commitResult.messageBody !== '') {
            nonEmptyCommitMessageCount += 1;
          }
        });
      });

      return nonEmptyCommitMessageCount;
    },

    sortCommits() {
      this.sortingFunction = (a, b) => (this.toReverseSortedCommits ? -1 : 1)
      * window.comparator(commitSortDict[this.commitsSortType])(a, b);
    },

    toggleAllCommitMessagesBody(isActive) {
      this.showAllCommitMessageBody = isActive;

      const toRename = this.showAllCommitMessageBody ? 'commit-message active' : 'commit-message';

      const commitMessageClasses = document.getElementsByClassName('commit-message');
      Array.from(commitMessageClasses).forEach((commitMessageClass) => {
        commitMessageClass.className = toRename;
      });

      this.expandedCommitMessagesCount = isActive ? this.getCommitMessageBodyCount() : 0;
    },

    updateExpandedCommitMessagesCount() {
      this.expandedCommitMessagesCount = document.getElementsByClassName('commit-message active')
          .length;
    },
  },
  computed: {
    selectedCommits() {
      return this.info.user.commits.sort(this.sortingFunction);
    },
  },
  created() {
    this.filterCommits();
  },
  mounted() {
    this.updateExpandedCommitMessagesCount();
  },
  components: {
    v_ramp: window.vRamp,
  },
};
