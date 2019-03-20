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
      selectedFileFormats: [],
      fileFormats: [],
      filesBlankLinesObj: {},
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
          this.filesLinesObj = author[0].fileFormatContribution;
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
      const filesBlanksInfoObj = {};
      let totalLineCount = 0;
      let totalBlankLineCount = 0;

      files.forEach((file) => {
        const lineCnt = file.authorContributionMap[this.info.author];
        if (lineCnt) {
          totalLineCount += lineCnt;
          const out = {};
          out.path = file.path;
          out.lineCount = lineCnt;

          const segmentInfo = this.splitSegments(file.lines);
          out.segments = segmentInfo.segments;
          totalBlankLineCount += segmentInfo.blankLineCount;
          this.addBlankLineCountToFileFormat(file.path, segmentInfo.blankLineCount,
              filesBlanksInfoObj);
          res.push(out);
        }
      });

      this.totalLineCount = totalLineCount;
      this.totalBlankLineCount = totalBlankLineCount;
      res.sort((a, b) => b.lineCount - a.lineCount);

      Object.keys(this.filesLinesObj).forEach((file) => {
        this.selectedFileFormats.push(file);
        this.fileFormats.push(file);
      });

      this.filesBlankLinesObj = filesBlanksInfoObj;
      this.files = res;
      this.isLoaded = true;

      this.activeFilesCount = this.selectedFiles.length;
    },

    addBlankLineCountToFileFormat(path, lineCount, filesInfoObj) {
      let fileFormat = path.split('.').pop();
      fileFormat = (fileFormat.length === 0) ? 'others' : fileFormat;

      if (!filesInfoObj[fileFormat]) {
        filesInfoObj[fileFormat] = 0;
      }

      filesInfoObj[fileFormat] += lineCount;
    },

    sortFiles() {
      this.sortingFunction = (a, b) => (this.toReverseSortFiles ? -1 : 1)
          * window.comparator(filesSortDict[this.filesSortType])(a, b);
    },

    selectAll() {
      if (!this.isSelectAllChecked) {
        this.selectedFileFormats = this.fileFormats.slice();
        this.activeFilesCount = this.files.length;
        this.isSelectAllChecked = true;
      } else {
        this.selectedFileFormats = [];
        this.activeFilesCount = 0;
        this.isSelectAllChecked = false;
      }
    },

    selectFileFormat(format) {
      if (this.selectedFileFormats.includes(format)) {
        const index = this.selectedFileFormats.indexOf(format);
        this.selectedFileFormats.splice(index, 1);
      } else {
        this.selectedFileFormats.push(format);
      }
      if (this.fileFormats.length === this.selectedFileFormats.length) {
        this.isSelectAllChecked = true;
      } else {
        this.isSelectAllChecked = false;
      }

      setTimeout(this.updateCount, 0);
    },

    updateFilterSearch(evt) {
      this.filterSearch = (evt.target.value.length !== 0) ? evt.target.value : '*';
    },

    tickAllCheckboxes() {
      this.selectedFileFormats = this.fileFormats.slice();
      this.isSelectAllChecked = true;
      this.filterSearch = '*';
    },

    enableSearchBar() {
      const searchBar = document.getElementById('search');
      const submitButton = document.getElementById('submit-button');
      searchBar.disabled = false;
      submitButton.disabled = false;

      this.tickAllCheckboxes();
      const checkboxes = document.getElementsByClassName('mui-checkbox--fileformat');
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
      const checkboxes = document.getElementsByClassName('mui-checkbox--fileformat');
      Array.from(checkboxes).forEach((checkbox) => {
        checkbox.disabled = false;
      });
    },

    isSelected(filePath) {
      const fileExt = filePath.split('.').pop();
      return this.selectedFileFormats.includes(fileExt);
    },

    getFileLink(file, path) {
      const repo = window.REPOS[this.info.repo];

      return `http://github.com/${
        repo.location.organization}/${repo.location.repoName}/${path}/${repo.branch}/${file.path}`;
    },

    getFileBlankLineInfo(fileFormat) {
      return `${fileFormat}: Blank: ${
        this.filesBlankLinesObj[fileFormat]}, Non-Blank: ${
        this.filesLinesObj[fileFormat] - this.filesBlankLinesObj[fileFormat]}`;
    },

    getTotalFileBlankLineInfo() {
      return `Total: Blank: ${this.totalBlankLineCount}, Non-Blank: ${
        this.totalLineCount - this.totalBlankLineCount}`;
    },
  },

  computed: {
    selectedFiles() {
      return this.files
          .filter((file) => this.isSelected(file.path)
              && minimatch(file.path, this.filterSearch, { matchBase: true }))
          .sort(this.sortingFunction);
    },
    getExistingLinesObj() {
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
