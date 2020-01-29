window.vZoom = {
  props: ['info'],
  template: window.$('v_zoom').innerHTML,
  data() {
    return {
      filterTimeFrame: window.hashParams.timeframe,
      showAllCommitMessageBody: true,
      expandedCommitMessagesCount: this.totalCommitMessageBodyCount,
    };
  },
  computed: {
    filteredUser() {
      const { user } = this.info;
      const filteredUser = Object.assign({}, user);

      const date = this.filterTimeFrame === 'week' ? 'endDate' : 'date';
      filteredUser.commits = user.commits.filter(
          (commit) => commit[date] >= this.info.sinceDate && commit[date] <= this.info.untilDate,
      );

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

    restoreZoomTab() {
      const { info } = this;
      const { users } = window.REPOS[info.tabRepo];

      const selectedUser = Object.assign({},
          users.filter((user) => user.name === info.tabAuthor)[0]);

      this.$root.$emit('restoreCommits', selectedUser); // restore selected user's commits from v_summary
      this.info.user = selectedUser;
    },

    setInfoHash() {
      const { addHash, encodeHash } = window;
      const {
        user, avgCommitSize, sinceDate, untilDate,
      } = this.info;

      addHash('tabAuthor', user.name);
      addHash('tabRepo', user.repoId);
      addHash('avgCommitSize', avgCommitSize);
      addHash('zoomSince', sinceDate);
      addHash('zoomUntil', untilDate);
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
