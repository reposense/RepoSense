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
      selectedGroups: [],
      groups: [],
      filesLinesObj: {},
      filesBlankLinesObj: {},
      filesGroupsBlankLinesObj: {},
      totalLineCount: '',
      totalBlankLineCount: '',
      containsGroups: false,
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
      const filesGroupsBlanksInfoObj = {};
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

          let fileType = file.path.split('.').pop();
          fileType = (fileType.length === 0) ? 'others' : fileType;
          this.addLineCountToFile(fileType, lineCnt, filesInfoObj);

          const segmentInfo = this.splitSegments(file.lines);
          out.segments = segmentInfo.segments;
          totalBlankLineCount += segmentInfo.blankLineCount;
          this.addLineCountToFile(fileType, segmentInfo.blankLineCount, filesBlanksInfoObj);
          this.addLineCountToFile(file.group, segmentInfo.blankLineCount, filesGroupsBlanksInfoObj);
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

      if (this.containsGroups) {
        Object.keys(this.info.filesGroupsObj).forEach((file) => {
          this.selectedGroups.push(file);
          this.groups.push(file);
        });
      }

      this.filesBlankLinesObj = filesBlanksInfoObj;
      this.filesGroupsBlankLinesObj = filesGroupsBlanksInfoObj;
      this.files = res;
      this.isLoaded = true;
    },

    setContainsGroups(file) {
      if (Object.prototype.hasOwnProperty.call(file, 'group')) {
        this.containsGroups = true;
      } else {
        this.containsGroups = false;
      }
    },

    addLineCountToFile(type, lineCount, filesInfoObj) {
      if (!filesInfoObj[type]) {
        filesInfoObj[type] = 0;
      }

      filesInfoObj[type] += lineCount;
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
        this.selectedGroups = this.groups.slice();
      } else {
        this.selectedFileTypes = [];
        this.selectedGroups = [];
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

    selectGroup(group) {
      if (this.selectedGroups.includes(group)) {
        const index = this.selectedGroups.indexOf(group);
        this.selectedGroups.splice(index, 1);
      } else {
        this.selectedGroups.push(group);
      }

      if (this.groups.length === this.selectedGroups.length) {
        this.isSelectAllChecked = true;
      } else if (this.selectedGroups.length === 0) {
        this.isSelectAllChecked = false;
      }
    },

    isSelectedFileTypes(filePath) {
      const fileExt = filePath.split('.').pop();
      return this.selectedFileTypes.includes(fileExt);
    },

    isSelectedGroups(group) {
      return this.selectedGroups.includes(group);
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

    getGroupBlankLineInfo(group) {
      return `${group}: Blank: ${
        this.filesGroupsBlankLinesObj[group]}, Non-Blank: ${
        this.info.filesGroupsObj[group] - this.filesGroupsBlankLinesObj[group]}`;
    },

    getTotalFileBlankLineInfo() {
      return `Total: Blank: ${this.totalBlankLineCount}, Non-Blank: ${
        this.totalLineCount - this.totalBlankLineCount}`;
    },
  },

  computed: {
    filesGroupsExistingLinesObj() {
      return Object.keys(this.info.filesGroupsObj)
          .filter((type) => this.info.filesGroupsObj[type] > 0)
          .reduce((acc, key) => ({
            ...acc, [key]: this.info.filesGroupsObj[key],
          }), {});
    },
    selectedFiles() {
      if (!this.containsGroups) {
        return this.files.filter((file) => this.isSelectedFileTypes(file.path));
      } else {
        return this.files.filter((file) => this.isSelectedGroups(file.group));
      }
    },
  },

  created() {
    this.initiate();
  },

  updated() {
    window.updateToggleButton();
  },
};
