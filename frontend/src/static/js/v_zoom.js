const commitSortDict = {
  lineOfCode: (commit) => commit.insertions,
  time: (commit) => commit.date,
};

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
      return (a, b) => (this.toReverseSortedCommits ? -1 : 1)
      * window.comparator(commitSortDict[this.commitsSortType])(a, b);
    },
    filteredUser() {
      const { user } = this.info;
      const filteredUser = Object.assign({}, user);

      const date = this.info.filterTimeFrame === 'week' ? 'endDate' : 'date';
      filteredUser.commits = user.commits.filter(
          (commit) => commit[date] >= this.info.zoomSince && commit[date] <= this.info.zoomUntil,
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
    initiate() {
      if (!this.info.user) { // restoring zoom tab from reloaded page
        this.restoreZoomTab();
      }
      this.setInfoHash();
    },
    openSummary() {
      this.$emit('view-summary', this.info.zoomSince, this.info.zoomUntil);
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

    restoreZoomTab() {
      // restore selected user's commits from v_summary
      this.$root.$emit('restoreCommits', this.info);
    },

    setInfoHash() {
      const { addHash, encodeHash } = window;
      const {
        avgCommitSize, zoomSince, zoomUntil, filterGroupSelection, filterTimeFrame,
        isMergeGroup, sortingOption, sortingWithinOption, isSortingDsc,
        isSortingWithinDsc, zoomAuthor, zoomRepo,
      } = this.info;

      addHash('zA', zoomAuthor);
      addHash('zR', zoomRepo);
      addHash('zACS', avgCommitSize);
      addHash('zS', zoomSince);
      addHash('zU', zoomUntil);
      addHash('zMG', isMergeGroup);
      addHash('zFTF', filterTimeFrame);
      addHash('zFGS', filterGroupSelection);
      addHash('zSO', sortingOption);
      addHash('zSWO', sortingWithinOption);
      addHash('zSD', isSortingDsc);
      addHash('zSWD', isSortingWithinDsc);
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
  },
  created() {
    this.initiate();
  },
  mounted() {
    this.updateExpandedCommitMessagesCount();
  },
  components: {
    v_ramp: window.vRamp,
  },
};
