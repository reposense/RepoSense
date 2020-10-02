/* global Vuex */

window.vZoom = {
  props: ['info'],
  template: window.$('v_zoom').innerHTML,
  data() {
    return {
      showAllCommitMessageBody: true,
      expandedCommitMessagesCount: this.totalCommitMessageBodyCount,
      commitsSortType: 'time',
      toReverseSortedCommits: true,
      isCommitsFinalized: false,
      selectedFileTypes: [],
      fileTypes: [],
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
      this.isCommitsFinalized = true;

      return filteredUser;
    },
    selectedCommits() {
      const commits = [];
      this.filteredUser.commits.forEach((commitDay) => {
        const filteredDay = { ...commitDay };
        filteredDay.commitResults = [];
        commitDay.commitResults.forEach((slice) => {
          if (Object.keys(slice.fileTypesAndContributionMap).some(
              (fileType) => this.selectedFileTypes.indexOf(fileType) !== -1,
          )) {
            filteredDay.commitResults.push(slice);
          }
        });
        if (filteredDay.commitResults.length > 0) {
          commits.push(filteredDay);
        }
      });
      return commits;
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
    isSelectAllChecked: {
      get() {
        return this.selectedFileTypes.length === this.fileTypes.length;
      },
      set(value) {
        if (value) {
          this.selectedFileTypes = this.fileTypes.slice();
        } else {
          this.selectedFileTypes = [];
        }
      },
    },
    ...Vuex.mapState(['fileTypeColors']),
  },

  watch: {
    isCommitsFinalized() {
      if (this.isCommitsFinalized) {
        this.updateFileTypes(this.filteredUser.commits);
        this.selectedFileTypes = this.fileTypes.slice();
      }
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
      // restore selected user's commits and file type colors from v_summary
      this.$root.$emit('restoreCommits', this.info);
    },

    updateFileTypes(commits) {
      this.fileTypes = [];
      commits.forEach((day) => {
        day.commitResults.forEach((slice) => {
          Object.keys(slice.fileTypesAndContributionMap).forEach((fileType) => {
            if (this.fileTypes.indexOf(fileType) === -1) {
              this.fileTypes.push(fileType);
            }
          });
        });
      });
      this.fileTypes.sort();
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

    filterSelectedFileTypes(fileTypes) {
      return fileTypes.filter((fileType) => this.selectedFileTypes.includes(fileType));
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
