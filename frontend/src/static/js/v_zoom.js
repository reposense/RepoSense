/* global Vuex */

function zoomInitialState() {
  return {
    showAllCommitMessageBody: true,
    commitsSortType: 'time',
    toReverseSortedCommits: true,
    isCommitsFinalized: false,
    selectedFileTypes: [],
    fileTypes: [],
  };
}

window.vZoom = {
  template: window.$('v_zoom').innerHTML,
  data() {
    return {
      expandedCommitMessagesCount: this.totalCommitMessageBodyCount,
      ...zoomInitialState(),
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
    selectedCommits() {
      const commits = [];
      this.filteredUser.commits.forEach((commit) => {
        const filteredCommit = { ...commit };
        filteredCommit.commitResults = [];
        commit.commitResults.forEach((slice) => {
          if (Object.keys(slice.fileTypesAndContributionMap).some(
              (fileType) => this.selectedFileTypes.indexOf(fileType) !== -1,
          )) {
            filteredCommit.commitResults.push(slice);
          }
        });
        if (filteredCommit.commitResults.length > 0) {
          commits.push(filteredCommit);
        }
      });
      return commits;
    },
    totalCommitMessageBodyCount() {
      let nonEmptyCommitMessageCount = 0;
      this.selectedCommits.forEach((commit) => {
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
        this.updateSelectedFileTypesHash();
      },
    },

    ...Vuex.mapState({
      fileTypeColors: 'fileTypeColors',
      info: 'tabZoomInfo',
    }),
  },

  watch: {
    info() {
      const newData = {
        expandedCommitMessagesCount: this.totalCommitMessageBodyCount,
        ...zoomInitialState(),
      };
      Object.assign(this.$data, newData);
      this.initiate();
      this.setInfoHash();
    },

    selectedFileTypes() {
      this.$nextTick(() => {
        this.updateExpandedCommitMessagesCount();
      });
    },
    commitsSortType() {
      window.addHash('zCST', this.commitsSortType);
      window.encodeHash();
    },
    toReverseSortedCommits() {
      window.addHash('zRSC', this.toReverseSortedCommits);
      window.encodeHash();
    },
  },

  methods: {
    initiate() {
      if (this.info.zUser) {
        // This code should always run since zUser must be defined
        this.updateFileTypes();
        this.selectedFileTypes = this.fileTypes.slice();
      }

      this.updateFileTypes();
      this.selectedFileTypes = this.fileTypes.slice();
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

    updateFileTypes() {
      const commitsFileTypes = new Set();
      this.filteredUser.commits.forEach((commit) => {
        commit.commitResults.forEach((slice) => {
          Object.keys(slice.fileTypesAndContributionMap).forEach((fileType) => {
            commitsFileTypes.add(fileType);
          });
        });
      });
      this.fileTypes = Object.keys(this.filteredUser.fileTypeContribution).filter(
          (fileType) => commitsFileTypes.has(fileType),
      );
    },

    retrieveHashes() {
      this.retrieveSortHash();
      this.retrieveSelectedFileTypesHash();
    },

    retrieveSortHash() {
      const hash = window.hashParams;
      if (hash.zCST) {
        this.commitsSortType = hash.zCST;
      }
      if (hash.zRSC) {
        this.toReverseSortedCommits = (hash.zRSC === 'true');
      }
    },

    retrieveSelectedFileTypesHash() {
      const hash = window.hashParams;

      if (hash.zFT) {
        this.selectedFileTypes = hash.zFT
            .split(window.HASH_DELIMITER)
            .filter((fileType) => this.fileTypes.includes(fileType));
      }
    },

    updateSelectedFileTypesHash() {
      const fileTypeHash = this.selectedFileTypes.length > 0
          ? this.selectedFileTypes.reduce((a, b) => `${a}~${b}`)
          : '';

      window.addHash('zFT', fileTypeHash);
      window.encodeHash();
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
      window.removeHash('zFT');
      window.removeHash('zCST');
      window.removeHash('zRSC');
      window.encodeHash();
    },

    filterSelectedFileTypes(fileTypes) {
      return fileTypes.filter((fileType) => this.selectedFileTypes.includes(fileType));
    },
  },
  created() {
    this.initiate();
    this.retrieveHashes();
    this.setInfoHash();
  },
  beforeDestroy() {
    this.removeZoomHashes();
  },
  components: {
    vRamp: window.vRamp,
  },
};
