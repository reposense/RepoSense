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
      selectedGroups: [],
      groups: [],
      fileFormatBlankLinesObj: {},
      groupBlankLinesObj: {},
      totalLineCount: '',
      totalBlankLineCount: '',
      containsGroups: false,
      activeFilesCount: 0,
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
      const fileFormatBlanksInfoObj = {};
      const groupBlanksInfoObj = {};
      let totalLineCount = 0;
      let totalBlankLineCount = 0;

      this.setContainsGroups(files[0]);
      files.forEach((file) => {
        const lineCnt = file.authorContributionMap[this.info.author];
        if (lineCnt) {
          totalLineCount += lineCnt;
          const out = {};
          out.path = file.path;
          out.lineCount = lineCnt;
          out.group = file.group;

          const segmentInfo = this.splitSegments(file.lines);
          out.segments = segmentInfo.segments;
          totalBlankLineCount += segmentInfo.blankLineCount;

          let fileFormat = file.path.split('.').pop();
          fileFormat = (fileFormat.length === 0) ? 'others' : fileFormat;
          this.addBlankLineCount(fileFormat, segmentInfo.blankLineCount,
              fileFormatBlanksInfoObj);
          this.addBlankLineCount(file.group, segmentInfo.blankLineCount,
              groupBlanksInfoObj);
          res.push(out);
        }
      });

      this.totalLineCount = totalLineCount;
      this.totalBlankLineCount = totalBlankLineCount;
      res.sort((a, b) => b.lineCount - a.lineCount);

      Object.keys(this.info.filesLinesObj).forEach((file) => {
        this.selectedFileFormats.push(file);
        this.fileFormats.push(file);
      });

      if (this.containsGroups) {
        Object.keys(this.info.filesGroupsObj).forEach((file) => {
          this.selectedGroups.push(file);
          this.groups.push(file);
        });
      }

      this.fileFormatBlankLinesObj = fileFormatBlanksInfoObj;
      this.groupBlankLinesObj = groupBlanksInfoObj;
      this.files = res;
      this.isLoaded = true;

      this.activeFilesCount = this.selectedFiles.length;
    },

    setContainsGroups(file) {
      if (Object.prototype.hasOwnProperty.call(file, 'group')) {
        this.containsGroups = true;
      } else {
        this.containsGroups = false;
      }
    },

    addBlankLineCount(type, lineCount, filesInfoObj) {
      if (!filesInfoObj[type]) {
        filesInfoObj[type] = 0;
      }

      filesInfoObj[type] += lineCount;
    },

    selectAll() {
      if (!this.isSelectAllChecked) {
        this.selectedFileFormats = this.fileFormats.slice();
        this.selectedGroups = this.groups.slice();
        this.activeFilesCount = this.files.length;
      } else {
        this.selectedFileFormats = [];
        this.selectedGroups = [];
        this.activeFilesCount = 0;
      }
    },

    selectFileFormat(format) {
      if (this.selectedFileFormats.includes(format)) {
        const index = this.selectedFileFormats.indexOf(format);
        this.selectedFileFormats.splice(index, 1);
      } else {
        this.selectedFileFormats.push(format);
      }
    },

    selectGroup(group) {
      if (this.selectedGroups.includes(group)) {
        const index = this.selectedGroups.indexOf(group);
        this.selectedGroups.splice(index, 1);
      } else {
        this.selectedGroups.push(group);
      }
    },

    getSelectedFiles() {
      if (this.fileFormats.length === this.selectedFileFormats.length) {
        this.isSelectAllChecked = true;
      } else if (this.selectedFileFormats.length === 0) {
        this.isSelectAllChecked = false;
      }

      setTimeout(this.updateCount, 0);
    },

    updateFilterSearch(evt) {
      this.filterSearch = (evt.target.value.length !== 0) ? evt.target.value : '*';
    },

    tickAllCheckboxes() {
      this.selectedFileFormats = this.fileFormats.slice();
      this.selectedGroups = this.groups.slice();
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

      if (!this.containsGroups) {
        checkboxes = document.getElementsByClassName('mui-checkbox--fileformat');
      } else {
        checkboxes = document.getElementsByClassName('mui-checkbox--group');
      }

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

      if (!this.containsGroups) {
        checkboxes = document.getElementsByClassName('mui-checkbox--fileformat');
      } else {
        checkboxes = document.getElementsByClassName('mui-checkbox--group');
      }

      Array.from(checkboxes).forEach((checkbox) => {
        checkbox.disabled = false;
      });
    },

    isSelectedFileTypes(filePath) {
      const fileExt = filePath.split('.').pop();
      return this.selectedFileFormats.includes(fileExt);
    },

    isSelectedGroups(group) {
      return this.selectedGroups.includes(group);
    },

    getFileLink(file, path) {
      const repo = window.REPOS[this.info.repo];

      return `http://github.com/${
        repo.location.organization}/${repo.location.repoName}/${path}/${repo.branch}/${file.path}`;
    },

    getFileFormatBlankLineInfo(fileFormat) {
      return `${fileFormat}: Blank: ${
        this.fileFormatBlankLinesObj[fileFormat]}, Non-Blank: ${
        this.info.filesLinesObj[fileFormat] - this.fileFormatBlankLinesObj[fileFormat]}`;
    },

    getGroupBlankLineInfo(group) {
      return `${group}: Blank: ${
        this.groupBlankLinesObj[group]}, Non-Blank: ${
        this.info.filesGroupsObj[group] - this.groupBlankLinesObj[group]}`;
    },

    getTotalFileBlankLineInfo() {
      return `Total: Blank: ${this.totalBlankLineCount}, Non-Blank: ${
        this.totalLineCount - this.totalBlankLineCount}`;
    },
  },

  computed: {
    selectedFiles() {
      if (!this.containsGroups) {
        return this.files.filter((file) => this.isSelectedFileTypes(file.path)
            && minimatch(file.path, this.filterSearch, { matchBase: true }));
      }
      return this.files.filter((file) => this.isSelectedGroups(file.group)
          && minimatch(file.path, this.filterSearch, { matchBase: true }));
    },
    getFileFormatExistingLinesObj() {
      return Object.keys(this.info.filesLinesObj)
          .filter((type) => this.info.filesLinesObj[type] > 0)
          .reduce((acc, key) => ({
            ...acc, [key]: this.info.filesLinesObj[key],
          }), {});
    },
    getGroupExistingLinesObj() {
      return Object.keys(this.info.filesGroupsObj)
          .filter((type) => this.info.filesGroupsObj[type] > 0)
          .reduce((acc, key) => ({
            ...acc, [key]: this.info.filesGroupsObj[key],
          }), {});
    },
  },

  created() {
    this.initiate();
  },
};
