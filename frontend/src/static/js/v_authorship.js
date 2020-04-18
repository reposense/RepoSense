const filesSortDict = {
  lineOfCode: (file) => file.lineCount,
  path: (file) => file.path,
  fileName: (file) => file.path.split(/[/]+/).pop(),
  fileType: (file) => file.fileType,
};

window.toggleNext = function toggleNext(ele) {
  // function for toggling unopened code
  const targetClass = 'active';

  const parent = ele.parentNode;
  const classes = parent.className.split(' ');
  const idx = classes.indexOf(targetClass);

  if (idx === -1) {
    classes.push(targetClass);
  } else {
    classes.splice(idx, 1);
  }

  parent.className = classes.join(' ');
};

const repoCache = [];
const minimatch = require('minimatch');

window.vAuthorship = {
  props: ['info'],
  template: window.$('v_authorship').innerHTML,
  data() {
    return {
      isLoaded: false,
      files: [],
      filterType: 'checkboxes',
      selectedFileTypes: [],
      fileTypes: [],
      filesLinesObj: {},
      fileTypeBlankLinesObj: {},
      totalLineCount: '',
      totalBlankLineCount: '',
      filesSortType: 'lineOfCode',
      toReverseSortFiles: true,
      filterSearch: '*',
    };
  },

  watch: {
    filterType() {
      if (this.filterType === 'checkboxes') {
        const searchBar = document.getElementById('search');
        searchBar.value = '';
        this.filterSearch = '*';
      } else {
        this.selectedFileTypes = this.fileTypes.slice();
      }
    },
  },

  methods: {
    initiate() {
      const repo = window.REPOS[this.info.repo];

      this.getRepoProps(repo);
      if (!repo || !this.info.author) {
        window.app.isTabActive = false;
        return;
      }
      if (repoCache.length === 2) {
        const toRemove = repoCache.shift();
        if (toRemove !== this.info.repo) {
          delete window.REPOS[toRemove].files;
        }
      }
      repoCache.push(this.info.repo);

      if (repo.files) {
        this.processFiles(repo.files);
      } else {
        window.api.loadAuthorship(this.info.repo)
            .then((files) => this.processFiles(files));
      }
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
            this.info.name = author.displayName;
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

    setInfoHash() {
      const { addHash, encodeHash } = window;
      addHash('tabAuthor', this.info.author);
      addHash('tabRepo', this.info.repo);
      addHash('authorshipIsMergeGroup', this.info.isMergeGroup);
      encodeHash();
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

    hasCommits(info) {
      const { isMergeGroup, author } = info;
      const repo = window.REPOS[info.repo];
      if (repo) {
        return isMergeGroup
            ? Object.entries(repo.commits.authorFinalContributionMap).some(([name, cnt]) => name !== '-' && cnt > 0)
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
            ? line.author.gitId !== '-'
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
      let totalLineCount = 0;
      let totalBlankLineCount = 0;

      files.forEach((file) => {
        const contributionMap = file.authorContributionMap;
        const lineCnt = this.info.isMergeGroup
            ? this.getContributionFromAllAuthors(contributionMap)
            : contributionMap[this.info.author];

        if (lineCnt) {
          totalLineCount += lineCnt;
          const out = {};
          out.path = file.path;
          out.lineCount = lineCnt;
          out.active = lineCnt <= COLLAPSED_VIEW_LINE_COUNT_THRESHOLD;
          out.wasCodeLoaded = lineCnt <= COLLAPSED_VIEW_LINE_COUNT_THRESHOLD;
          out.fileType = file.fileType;

          const segmentInfo = this.splitSegments(file.lines);
          out.segments = segmentInfo.segments;
          totalBlankLineCount += segmentInfo.blankLineCount;

          this.addBlankLineCount(file.fileType, segmentInfo.blankLineCount,
              fileTypeBlanksInfoObj);
          res.push(out);
        }
      });

      this.totalLineCount = totalLineCount;
      this.totalBlankLineCount = totalBlankLineCount;
      res.sort((a, b) => b.lineCount - a.lineCount);

      Object.keys(this.filesLinesObj).forEach((file) => {
        if (this.filesLinesObj[file] !== 0) {
          this.selectedFileTypes.push(file);
          this.fileTypes.push(file);
        }
      });

      this.selectedFileTypes = this.fileTypes.slice();
      this.fileTypeBlankLinesObj = fileTypeBlanksInfoObj;
      this.files = res;
      this.isLoaded = true;
    },

    getContributionFromAllAuthors(contributionMap) {
      return Object.entries(contributionMap).reduce((acc, [author, cnt]) => (author !== '-' ? acc + cnt : acc), 0);
    },

    addBlankLineCount(fileType, lineCount, filesInfoObj) {
      if (!filesInfoObj[fileType]) {
        filesInfoObj[fileType] = 0;
      }

      filesInfoObj[fileType] += lineCount;
    },

    updateFilterSearch(evt) {
      if (this.filterType === 'checkboxes') {
        this.indicateSearchBar();
      }
      this.filterSearch = (evt.target.value.length !== 0) ? evt.target.value : '*';
    },

    indicateSearchBar() {
      this.selectedFileTypes = this.fileTypes.slice();
      this.filterType = 'search';
    },

    indicateCheckBoxes() {
      if (this.filterType === 'search') {
        const searchBar = document.getElementById('search');
        searchBar.value = '';
        this.filterSearch = '*';
        this.filterType = 'checkboxes';
      }
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
        if (this.filterType === 'search') {
          this.indicateCheckBoxes();
        }
        if (value) {
          this.selectedFileTypes = this.fileTypes.slice();
        } else {
          this.selectedFileTypes = [];
        }
      },
    },

    selectedFiles() {
      return this.files.filter((file) => this.selectedFileTypes.includes(file.fileType)
          && minimatch(file.path, this.filterSearch, { matchBase: true, dot: true }))
          .sort(this.sortingFunction);
    },

    activeFilesCount() {
      return this.selectedFiles.filter((file) => file.active).length;
    },

    getFileTypeExistingLinesObj() {
      const numLinesModified = {};
      Object.entries(this.filesLinesObj)
          .filter(([, value]) => value > 0)
          .forEach(([langType, value]) => {
            numLinesModified[langType] = value;
          });
      return numLinesModified;
    },
  },

  created() {
    this.initiate();
    this.setInfoHash();
  },

  components: {
    vSegment: window.vSegment,
  },
};
