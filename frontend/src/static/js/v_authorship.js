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
      fileTypeBlankLinesObj: {},
      totalLineCount: '',
      totalBlankLineCount: '',
      filesSortType: 'lineOfCode',
      toReverseSortFiles: true,
      hasActiveFile: true,
      filterSearch: '*',
    };
  },

  watch: {
    selectedFiles() {
      setTimeout(this.updateCount, 0);
    },

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
        const author = repo.users.filter((user) => user.name === this.info.author);
        if (author.length > 0) {
          this.info.name = author[0].displayName;
          this.filesLinesObj = author[0].fileTypeContribution;
        }
      }
    },

    setInfoHash() {
      const { addHash, encodeHash } = window;
      addHash('tabAuthor', this.info.author);
      addHash('tabRepo', this.info.repo);
      encodeHash();
    },

    expandAll(hasActiveFile) {
      const renameValue = hasActiveFile ? 'file active' : 'file';

      const files = document.getElementsByClassName('file');
      Array.from(files).forEach((file) => {
        file.className = renameValue;
      });

      this.hasActiveFile = hasActiveFile;
    },

    updateCount() {
      this.hasActiveFile = document.getElementsByClassName('file active').length > 0;
    },

    hasCommits(info) {
      if (window.REPOS[info.repo]) {
        return window.REPOS[info.repo].commits.authorFinalContributionMap[info.author] > 0;
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
        const authored = (line.author && line.author.gitId === this.info.author);

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
      const res = [];
      const fileTypeBlanksInfoObj = {};
      let totalLineCount = 0;
      let totalBlankLineCount = 0;

      files.forEach((file) => {
        const lineCnt = file.authorContributionMap[this.info.author];
        if (lineCnt) {
          totalLineCount += lineCnt;
          const out = {};
          out.path = file.path;
          out.lineCount = lineCnt;
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
