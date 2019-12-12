window.vZoom = {
  props: ['info'],
  template: window.$('v_zoom').innerHTML,
  data() {
    return {
      filterTimeFrame: window.hashParams.timeframe,
      showAllCommitMessageBody: true,
      expandedCommitMessagesCount: this.getCommitMessageBodyCount(),
    };
  },
  methods: {
    initiate() {
      if (!this.info.user) { // restoring zoom tab from reloaded page
        this.restoreZoomTab();
      }
      this.filterCommits();
    },

    openSummary() {
      this.$emit('view-summary', this.info.zoomSince, this.info.zoomUntil);
    },

    filterCommits() {
      const { user } = this.info;
      const date = this.filterTimeFrame === 'week' ? 'endDate' : 'date';
      const filtered = user.commits.filter(
          (commit) => commit[date] >= this.info.zoomSince && commit[date] <= this.info.zoomUntil,
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
        user, avgCommitSize, zoomSince, zoomUntil,
      } = this.info;

      addHash('tabAuthor', user.name);
      addHash('tabRepo', user.repoId);
      addHash('avgCommitSize', avgCommitSize);
      addHash('zoomSince', zoomSince);
      addHash('zoomUntil', zoomUntil);
      encodeHash();
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
  created() {
    this.initiate();
    this.setInfoHash();
  },
  mounted() {
    this.updateExpandedCommitMessagesCount();
  },
  components: {
    v_ramp: window.vRamp,
  },
};
