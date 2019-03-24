const filesSortDict = {
  lineOfCode: (file) => file.lineCount,
  path: (file) => file.path,
  fileName: (file) => file.path.split(/[/]+/).pop(),
  fileType: (file) => file.path.split(/[/]+/).pop().split(/[.]+/).pop(),
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
      isSelectAllChecked: true,
      selectedFileTypes: [],
      fileTypes: [],
      fileTypeBlankLinesObj: {},
      totalLineCount: '',
      totalBlankLineCount: '',
      filesSortType: 'lineOfCode',
      toReverseSortFiles: false,
      activeFilesCount: 0,
      filterSearch: '*',
      sortingFunction: window.comparator(filesSortDict.lineOfCode),
    };
  },

  watch: {
    filesSortType() {
      this.sortFiles();
    },
    toReverseSortFiles() {
      this.sortFiles();
    },
  },

  methods: {
    initiate() {
      const repo = window.REPOS[this.info.repo];

      this.getRepoProps(repo);
      if (!repo || !this.info.name) {
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
        this.info.location = repo.location.location;
      }
    },

    setInfoHash() {
      const { addHash, removeHash } = window;
      addHash('tabAuthor', this.info.author);
      addHash('tabRepo', this.info.repo);
      removeHash('tabOpen');
    },

    expandAll(isActive) {
      const renameValue = isActive ? 'file active' : 'file';

      const files = document.getElementsByClassName('file');
      Array.from(files).forEach((file) => {
        file.className = renameValue;
      });

      this.activeFilesCount = isActive ? this.selectedFiles.length : 0;
    },

    updateCount() {
      this.activeFilesCount = document.getElementsByClassName('file active').length;
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

      lines.forEach((line) => {
        const authored = (line.author && line.author.gitId === this.info.author);

        if (authored !== lastState || lastId === -1) {
          segments.push({
            authored,
            lines: [],
          });

          lastId += 1;
          lastState = authored;
        }

        const content = line.content || ' ';
        segments[lastId].lines.push(content);

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
        this.selectedFileTypes.push(file);
        this.fileTypes.push(file);
      });

      this.fileTypeBlankLinesObj = fileTypeBlanksInfoObj;
      this.files = res;
      this.isLoaded = true;

      this.activeFilesCount = this.selectedFiles.length;
    },

    addBlankLineCount(type, lineCount, filesInfoObj) {
      if (!filesInfoObj[type]) {
        filesInfoObj[type] = 0;
      }

      filesInfoObj[type] += lineCount;
    },

    sortFiles() {
      this.sortingFunction = (a, b) => (this.toReverseSortFiles ? -1 : 1)
          * window.comparator(filesSortDict[this.filesSortType])(a, b);
    },

    selectAll() {
      if (!this.isSelectAllChecked) {
        this.selectedFileTypes = this.fileTypes.slice();
        this.activeFilesCount = this.files.length;
      } else {
        this.selectedFileTypes = [];
        this.activeFilesCount = 0;
      }
    },

    selectFileType(fileType) {
      if (this.selectedFileTypes.includes(fileType)) {
        const index = this.selectedFileTypes.indexOf(fileType);
        this.selectedFileTypes.splice(index, 1);
      } else {
        this.selectedFileTypes.push(fileType);
      }
    },

    getSelectedFiles() {
      if (this.fileTypes.length === this.selectedFileTypes.length) {
        this.isSelectAllChecked = true;
      } else if (this.selectedFileTypes.length === 0) {
        this.isSelectAllChecked = false;
      }

      setTimeout(this.updateCount, 0);
    },

    updateFilterSearch(evt) {
      this.filterSearch = (evt.target.value.length !== 0) ? evt.target.value : '*';
    },

    tickAllCheckboxes() {
      this.selectedFileTypes = this.fileTypes.slice();
      this.isSelectAllChecked = true;
      this.filterSearch = '*';
    },

    enableSearchBar() {
      const searchBar = document.getElementById('search');
      const submitButton = document.getElementById('submit-button');
      searchBar.disabled = false;
      submitButton.disabled = false;

      this.tickAllCheckboxes();
      document.getElementsByClassName('mui-checkbox--all')[0].disabled = true;
      let checkboxes = [];

      checkboxes = document.getElementsByClassName('mui-checkbox--fileType');
      Array.from(checkboxes).forEach((checkbox) => {
        checkbox.disabled = true;
      });
    },

    enableCheckBoxes() {
      const searchBar = document.getElementById('search');
      const submitButton = document.getElementById('submit-button');
      searchBar.value = '';
      searchBar.disabled = true;
      submitButton.disabled = true;

      this.tickAllCheckboxes();
      document.getElementsByClassName('mui-checkbox--all')[0].disabled = false;
      let checkboxes = [];

      checkboxes = document.getElementsByClassName('mui-checkbox--fileType');
      Array.from(checkboxes).forEach((checkbox) => {
        checkbox.disabled = false;
      });
    },

    isSelectedFileTypes(fileType) {
      return this.selectedFileTypes.includes(fileType);
    },

    getFileLink(file, path) {
      const repo = window.REPOS[this.info.repo];

      return `http://github.com/${
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
    selectedFiles() {
      return this.files.filter((file) => this.isSelectedFileTypes(file.fileType)
          && minimatch(file.path, this.filterSearch, { matchBase: true }))
          .sort(this.sortingFunction);
    },
    getFileTypeExistingLinesObj() {
      return Object.keys(this.filesLinesObj)
          .filter((type) => this.filesLinesObj[type] > 0)
          .reduce((acc, key) => ({
            ...acc, [key]: this.filesLinesObj[key],
          }), {});
    },
  },

  created() {
    this.initiate();
    this.setInfoHash();
  },
};
