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

  // Update expand/collapse all button
  window.updateToggleButton();
};

window.updateToggleButton = function updateToggleButton() {
  if (document.getElementsByClassName('file active').length === document.getElementsByClassName('file').length) {
    window.app.isCollapsed = false;
  } else if (document.getElementsByClassName('file active').length === 0) {
    window.app.isCollapsed = true;
  }
};

window.expandAll = function expandAll(isActive) {
  const renameValue = isActive ? 'file active' : 'file';

  const files = document.getElementsByClassName('file');
  Array.from(files).forEach((file) => {
    file.className = renameValue;
  });
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
      filesLinesObj: {},
      filesBlankLinesObj: {},
      totalLineCount: '',
      totalBlankLineCount: '',
      filterSearch: '*',
    };
  },

  methods: {
    initiate() {
      const repo = window.REPOS[this.info.repo];

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
      const filesInfoObj = {};
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
          this.addLineCountToFileType(file.path, lineCnt, filesInfoObj);

          const segmentInfo = this.splitSegments(file.lines);
          out.segments = segmentInfo.segments;
          totalBlankLineCount += segmentInfo.blankLineCount;
          this.addLineCountToFileType(file.path, segmentInfo.blankLineCount, filesBlanksInfoObj);
          res.push(out);
        }
      });

      this.totalLineCount = totalLineCount;
      this.totalBlankLineCount = totalBlankLineCount;
      res.sort((a, b) => b.lineCount - a.lineCount);

      this.filesLinesObj = this.sortFileTypeAlphabetically(filesInfoObj);
      Object.keys(filesInfoObj).forEach((file) => {
        this.selectedFileTypes.push(file);
        this.fileTypes.push(file);
      });

      this.filesBlankLinesObj = filesBlanksInfoObj;
      this.files = res;
      this.isLoaded = true;
    },

    addLineCountToFileType(path, lineCount, filesInfoObj) {
      let fileType = path.split('.').pop();
      fileType = (fileType.length === 0) ? 'others' : fileType;

      if (!filesInfoObj[fileType]) {
        filesInfoObj[fileType] = 0;
      }

      filesInfoObj[fileType] += lineCount;
    },

    sortFileTypeAlphabetically(unsortedFilesInfoObj) {
      return Object.keys(unsortedFilesInfoObj)
          .sort()
          .reduce((acc, key) => ({
            ...acc, [key]: unsortedFilesInfoObj[key],
          }), {});
    },

    selectAll() {
      if (!this.isSelectAllChecked) {
        this.selectedFileTypes = this.fileTypes.slice();
      } else {
        this.selectedFileTypes = [];
      }
    },

    selectFileType(type) {
      if (this.selectedFileTypes.includes(type)) {
        const index = this.selectedFileTypes.indexOf(type);
        this.selectedFileTypes.splice(index, 1);
      } else {
        this.selectedFileTypes.push(type);
      }

      if (this.fileTypes.length === this.selectedFileTypes.length) {
        this.isSelectAllChecked = true;
      } else if (this.selectedFileTypes.length === 0) {
        this.isSelectAllChecked = false;
      }
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
      const checkboxes = document.getElementsByClassName('mui-checkbox--filetype');
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
      const checkboxes = document.getElementsByClassName('mui-checkbox--filetype');
      Array.from(checkboxes).forEach((checkbox) => {
        checkbox.disabled = false;
      });
    },

    isSelected(filePath) {
      const fileExt = filePath.split('.').pop();
      return this.selectedFileTypes.includes(fileExt);
    },

    getFileLink(file, path) {
      const repo = window.REPOS[this.info.repo];

      return `http://github.com/${
        repo.location.organization}/${repo.location.repoName}/${path}/${repo.branch}/${file.path}`;
    },

    getFileBlankLineInfo(fileType) {
      return `${fileType}: Blank: ${
        this.filesBlankLinesObj[fileType]}, Non-Blank: ${
        this.filesLinesObj[fileType] - this.filesBlankLinesObj[fileType]}`;
    },

    getTotalFileBlankLineInfo() {
      return `Total: Blank: ${this.totalBlankLineCount}, Non-Blank: ${
        this.totalLineCount - this.totalBlankLineCount}`;
    },
  },

  computed: {
    selectedFiles() {
      return this.files.filter((file) => this.isSelected(file.path)
          && minimatch(file.path, this.filterSearch, { matchBase: true }));
    },
  },

  created() {
    this.initiate();
  },

  updated() {
    window.updateToggleButton();
  },
};
