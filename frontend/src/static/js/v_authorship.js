// eslint-disable-next-line import/no-unresolved
import minimatch from 'https://cdn.skypack.dev/minimatch@v3.0.4';

/* global Vuex getFontColor */
const filesSortDict = {
  lineOfCode: (file) => file.lineCount,
  path: (file) => file.path,
  fileName: (file) => file.path.split(/[/]+/).pop(),
  fileType: (file) => file.fileType,
};

function authorshipInitialState() {
  return {
    isLoaded: false,
    files: [],
    selectedFiles: [],
    filterType: 'checkboxes',
    selectedFileTypes: [],
    fileTypes: [],
    filesLinesObj: {},
    fileTypeBlankLinesObj: {},
    filesSortType: 'lineOfCode',
    toReverseSortFiles: true,
    isBinaryFilesChecked: false,
    searchBarValue: '',
    authorDisplayName: '',
  };
}

const repoCache = [];

window.vAuthorship = {
  template: window.$('v_authorship').innerHTML,
  data() {
    return authorshipInitialState();
  },

  watch: {
    filesSortType() {
      window.addHash('authorshipSortBy', this.filesSortType);
      window.encodeHash();
      this.updateSelectedFiles();
    },

    searchBarValue() {
      this.updateSelectedFiles();
    },

    selectedFileTypes() {
      this.updateSelectedFiles();
    },

    toReverseSortFiles() {
      window.addHash('reverseAuthorshipOrder', this.toReverseSortFiles);
      window.encodeHash();
      this.updateSelectedFiles();
    },

    info() {
      Object.assign(this.$data, authorshipInitialState());
      this.initiate();
    },
  },

  methods: {
    retrieveHashes() {
      const hash = window.hashParams;

      switch (hash.authorshipSortBy) {
      case 'path':
      case 'fileName':
      case 'fileType':
        this.filesSortType = hash.authorshipSortBy;
        break;
      default:
        // Invalid value, use the default value of 'lineOfCode'
      }

      this.toReverseSortFiles = hash.reverseAuthorshipOrder !== 'false';

      if (hash.authorshipFileTypes) {
        this.selectedFileTypes = hash.authorshipFileTypes
            .split(window.HASH_DELIMITER)
            .filter((fileType) => this.fileTypes.includes(fileType));
      } else {
        this.resetSelectedFileTypes();
      }

      if (hash.authorshipIsBinaryFileTypeChecked) {
        this.isBinaryFilesChecked = hash.authorshipIsBinaryFileTypeChecked === 'true';
      }

      if ('authorshipFilesGlob' in hash) {
        this.indicateSearchBar();
        this.searchBarValue = hash.authorshipFilesGlob;
      }
    },

    resetSelectedFileTypes() {
      this.selectedFileTypes = this.info.checkedFileTypes
        ? this.info.checkedFileTypes.filter((fileType) => this.fileTypes.includes(fileType))
        : [];
    },

    setInfoHash() {
      const { addHash } = window;
      // We only set these hashes as they are propagated from summary_charts
      addHash('tabAuthor', this.info.author);
      addHash('tabRepo', this.info.repo);
      addHash('authorshipIsMergeGroup', this.info.isMergeGroup);
      this.updateFileTypeHash();
    },

    removeAuthorshipHashes() {
      window.removeHash('authorshipFileTypes');
      window.removeHash('authorshipIsBinaryFileTypeChecked');
      window.removeHash('authorshipFilesGlob');
      window.removeHash('authorshipSortBy');
      window.removeHash('reverseAuthorshipOrder');
      window.removeHash('tabAuthor');
      window.removeHash('tabRepo');
      window.removeHash('authorshipIsMergeGroup');
      window.encodeHash();
    },

    async initiate() {
      const repo = window.REPOS[this.info.repo];

      this.getRepoProps(repo);
      if (!repo || !this.info.author) {
        this.$store.commit('updateTabState', false);
        return;
      }
      if (repoCache.length === 2) {
        const toRemove = repoCache.shift();
        if (toRemove !== this.info.repo) {
          delete window.REPOS[toRemove].files;
        }
      }
      repoCache.push(this.info.repo);

      let { files } = repo;
      if (!files) {
        files = await window.api.loadAuthorship(this.info.repo);
      }
      this.processFiles(files);

      if (this.info.isRefresh) {
        this.retrieveHashes();
      } else {
        this.resetSelectedFileTypes();
      }

      this.setInfoHash();
    },

    getRepoProps(repo) {
      if (repo) {
        if (this.info.isMergeGroup) {
          // sum of all users' file type contribution
          repo.users.forEach((author) => {
            this.updateTotalFileTypeContribution(author.fileTypeContribution);
          });
        } else {
          const author = repo.users.find((user) => user.name === this.info.author);
          if (author) {
            this.authorDisplayName = author.displayName;
            this.filesLinesObj = author.fileTypeContribution;
          }
        }
      }
    },

    updateTotalFileTypeContribution(fileTypeContribution) {
      Object.entries(fileTypeContribution).forEach(([type, cnt]) => {
        if (this.filesLinesObj[type]) {
          this.filesLinesObj[type] += cnt;
        } else {
          this.filesLinesObj[type] = cnt;
        }
      });
    },

    expandAll() {
      this.selectedFiles.forEach((file) => {
        file.active = true;
        file.wasCodeLoaded = true;
      });
    },

    collapseAll() {
      this.selectedFiles.forEach((file) => {
        file.active = false;
      });
    },

    toggleFileActiveProperty(file) {
      file.active = !file.active;
      file.wasCodeLoaded = file.wasCodeLoaded || file.active;
    },

    isUnknownAuthor(name) {
      return name === '-';
    },

    hasCommits(info) {
      const { isMergeGroup, author } = info;
      const repo = window.REPOS[info.repo];
      if (repo) {
        return isMergeGroup
            ? Object.entries(repo.commits.authorFinalContributionMap).some(([name, cnt]) => (
              !this.isUnknownAuthor(name) && cnt > 0
            ))
            : repo.commits.authorFinalContributionMap[author] > 0;
      }
      return false;
    },

    splitSegments(lines) {
      // split into segments separated by authored
      let lastState;
      let lastId = -1;
      const segments = [];
      let blankLineCount = 0;

      lines.forEach((line, lineCount) => {
        const isAuthorMatched = this.info.isMergeGroup
            ? !this.isUnknownAuthor(line.author.gitId)
            : line.author.gitId === this.info.author;
        const authored = (line.author && isAuthorMatched);

        if (authored !== lastState || lastId === -1) {
          segments.push({
            authored,
            lines: [],
            lineNumbers: [],
          });

          lastId += 1;
          lastState = authored;
        }

        const content = line.content || ' ';
        segments[lastId].lines.push(content);

        segments[lastId].lineNumbers.push(lineCount + 1);

        if (line.content === '' && authored) {
          blankLineCount += 1;
        }
      });

      return {
        segments,
        blankLineCount,
      };
    },

    processFiles(files) {
      const COLLAPSED_VIEW_LINE_COUNT_THRESHOLD = 2000;
      const res = [];
      const fileTypeBlanksInfoObj = {};

      files.filter((file) => this.isValidFile(file)).forEach((file) => {
        const contributionMap = file.authorContributionMap;

        const lineCnt = this.info.isMergeGroup
            ? this.getContributionFromAllAuthors(contributionMap)
            : contributionMap[this.info.author];

        const out = {};
        out.path = file.path;
        out.lineCount = lineCnt;
        out.active = lineCnt <= COLLAPSED_VIEW_LINE_COUNT_THRESHOLD && !file.isBinary;
        out.wasCodeLoaded = lineCnt <= COLLAPSED_VIEW_LINE_COUNT_THRESHOLD;
        out.fileType = file.fileType;

        if (file.isBinary) {
          out.isBinary = true;
        } else {
          out.isBinary = false;
        }

        if (!file.isBinary) {
          const segmentInfo = this.splitSegments(file.lines);
          out.segments = segmentInfo.segments;
          out.blankLineCount = segmentInfo.blankLineCount;

          this.addBlankLineCount(file.fileType, segmentInfo.blankLineCount,
              fileTypeBlanksInfoObj);
        }

        res.push(out);
      });

      res.sort((a, b) => b.lineCount - a.lineCount);

      Object.keys(this.filesLinesObj).forEach((file) => {
        if (this.filesLinesObj[file] !== 0) {
          this.fileTypes.push(file);
        }
      });

      this.fileTypeBlankLinesObj = fileTypeBlanksInfoObj;
      this.files = res;
      this.updateSelectedFiles(true);
    },

    isValidFile(file) {
      return this.info.isMergeGroup
          ? Object.entries(file.authorContributionMap)
              .some((authorCount) => !this.isUnknownAuthor(authorCount[0]))
          : this.info.author in file.authorContributionMap;
    },

    getContributionFromAllAuthors(contributionMap) {
      return Object.entries(contributionMap).reduce((acc, [author, cnt]) => (
        (!this.isUnknownAuthor(author) ? acc + cnt : acc)
      ), 0);
    },

    addBlankLineCount(fileType, lineCount, filesInfoObj) {
      if (!filesInfoObj[fileType]) {
        filesInfoObj[fileType] = 0;
      }

      filesInfoObj[fileType] += lineCount;
    },

    updateSearchBarValue() {
      this.searchBarValue = this.$refs.searchBar.value;

      window.addHash('authorshipFilesGlob', this.searchBarValue);
      window.removeHash('authorshipFileTypes');
      window.removeHash('authorshipIsBinaryFileTypeChecked');
      window.encodeHash();
    },

    updateFileTypeHash() {
      const fileTypeHash = this.selectedFileTypes.length > 0
          ? this.selectedFileTypes.reduce((a, b) => `${a}~${b}`)
          : '';

      window.addHash('authorshipFileTypes', fileTypeHash);
      window.addHash('authorshipIsBinaryFileTypeChecked', this.isBinaryFilesChecked);
      window.removeHash('authorshipFilesGlob');
      window.encodeHash();
    },

    updateSelectedFiles(setIsLoaded = false) {
      this.$store.commit('incrementLoadingOverlayCount', 1);
      setTimeout(() => {
        this.selectedFiles = this.files.filter(
            (file) => ((this.selectedFileTypes.includes(file.fileType) && !file.isBinary)
            || (file.isBinary && this.isBinaryFilesChecked))
            && minimatch(file.path, this.searchBarValue || '*', { matchBase: true, dot: true }),
        )
            .sort(this.sortingFunction);
        if (setIsLoaded) {
          this.isLoaded = true;
        }
        this.$store.commit('incrementLoadingOverlayCount', -1);
      });
    },

    indicateSearchBar() {
      this.selectedFileTypes = this.fileTypes.slice();
      this.isBinaryFilesChecked = true;
      this.filterType = 'search';
    },

    indicateCheckBoxes() {
      this.searchBarValue = '';
      this.filterType = 'checkboxes';
      this.updateFileTypeHash();
    },

    getFileLink(file, path) {
      const repo = window.REPOS[this.info.repo];

      return `${window.BASE_URL}/${
        repo.location.organization}/${repo.location.repoName}/${path}/${repo.branch}/${file.path}`;
    },

    getFileTypeBlankLineInfo(fileType) {
      return `${fileType}: Blank: ${
        this.fileTypeBlankLinesObj[fileType]}, Non-Blank: ${
        this.filesLinesObj[fileType] - this.fileTypeBlankLinesObj[fileType]}`;
    },

    getTotalFileBlankLineInfo() {
      return `Total: Blank: ${this.totalBlankLineCount}, Non-Blank: ${
        this.totalLineCount - this.totalBlankLineCount}`;
    },
    getFontColor,
  },

  computed: {
    sortingFunction() {
      return (a, b) => (this.toReverseSortFiles ? -1 : 1)
        * window.comparator(filesSortDict[this.filesSortType])(a, b);
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

        this.indicateCheckBoxes();
      },
    },

    isBinaryChecked: {
      get() {
        return this.isBinaryFilesChecked;
      },
      set(value) {
        if (value) {
          this.isBinaryFilesChecked = true;
        } else {
          this.isBinaryFilesChecked = false;
        }

        this.updateSelectedFiles();
        this.indicateCheckBoxes();
      },
    },

    activeFilesCount() {
      return this.selectedFiles.filter((file) => file.active).length;
    },

    totalLineCount() {
      return Object.values(this.fileTypeLinesObj).reduce((acc, val) => acc + val, 0);
    },

    totalBlankLineCount() {
      return Object.values(this.fileTypeBlankLinesObj).reduce((acc, val) => acc + val, 0);
    },

    fileTypeLinesObj() {
      const numLinesModified = {};
      Object.entries(this.filesLinesObj)
          .filter(([, value]) => value > 0)
          .forEach(([langType, value]) => {
            numLinesModified[langType] = value;
          });
      return numLinesModified;
    },

    binaryFilesCount() {
      return this.files.filter((file) => file.isBinary).length;
    },

    ...Vuex.mapState({
      fileTypeColors: 'fileTypeColors',
      info: 'tabAuthorshipInfo',
    }),
  },

  created() {
    this.initiate();
  },

  beforeDestroy() {
    this.removeAuthorshipHashes();
  },

  components: {
    vSegment: window.vSegment,
  },
};
