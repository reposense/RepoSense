window.vZoom = {
  props: ['info'],
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
      const commitSortFunction = this.commitsSortType === 'time'
        ? (commit) => commit.date
        : (commit) => commit.insertions;

      return (a, b) => (this.toReverseSortedCommits ? -1 : 1)
        * window.comparator(commitSortFunction)(a, b);
    },
    filteredUser() {
      const {
        zUser, zSince, zUntil, zTimeFrame,
      } = this.info;
      const filteredUser = Object.assign({}, zUser);

      const date = zTimeFrame === 'week' ? 'endDate' : 'date';
      filteredUser.commits = zUser.commits.filter(
          (commit) => commit[date] >= zSince && commit[date] <= zUntil,
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
  methods: {
    initiate() {
      if (!this.info.zUser) { // restoring zoom tab from reloaded page
        this.restoreZoomTab();
      }
    },

    openSummary() {
      const info = { since: this.info.zSince, until: this.info.zUntil };
      this.$store.commit('updateSummaryDates', info);
    },

    getSliceLink(slice) {
      if (this.info.zIsMerge) {
        return `${window.getBaseLink(slice.repoId)}/commit/${slice.hash}`;
      }
      return `${window.getBaseLink(this.info.zUser.repoId)}/commit/${slice.hash}`;
    },

    scrollToCommit(tag, commit) {
      const el = this.$el.getElementsByClassName(`${commit} ${tag}`)[0];
      if (el) {
        el.focus();
      }
    },

    restoreZoomTab() {
      // restore selected user's commits from v_summary
      this.$root.$emit('restoreCommits', this.info);
    },

    setInfoHash() {
      const { addHash, encodeHash } = window;
      const {
        zAvgCommitSize, zSince, zUntil, zFilterGroup,
        zTimeFrame, zIsMerge, zAuthor, zRepo, zFromRamp, zFilterSearch,
      } = this.info;

      addHash('zA', zAuthor);
      addHash('zR', zRepo);
      addHash('zACS', zAvgCommitSize);
      addHash('zS', zSince);
      addHash('zFS', zFilterSearch);
      addHash('zU', zUntil);
      addHash('zMG', zIsMerge);
      addHash('zFTF', zTimeFrame);
      addHash('zFGS', zFilterGroup);
      addHash('zFR', zFromRamp);
      encodeHash();
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

    removeZoomHashes() {
      window.removeHash('zA');
      window.removeHash('zR');
      window.removeHash('zFS');
      window.removeHash('zACS');
      window.removeHash('zS');
      window.removeHash('zU');
      window.removeHash('zFGS');
      window.removeHash('zFTF');
      window.removeHash('zMG');
      window.encodeHash();
    },
  },
  created() {
    this.initiate();
  },
  mounted() {
    this.setInfoHash();
    this.updateExpandedCommitMessagesCount();
  },
  beforeDestroy() {
    this.removeZoomHashes();
  },
  components: {
    vRamp: window.vRamp,
  },
};
