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
      selectedFileFormats: [],
      fileFormats: [],
      filesBlankLinesObj: {},
      totalLineCount: '',
      totalBlankLineCount: '',
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

      Object.keys(this.info.filesLinesObj).forEach((file) => {
        this.selectedFileFormats.push(file);
        this.fileFormats.push(file);
      });

      this.filesBlankLinesObj = filesBlanksInfoObj;
      this.files = res;
      this.isLoaded = true;
    },

    addBlankLineCountToFileFormat(path, lineCount, filesInfoObj) {
      let fileFormat = path.split('.').pop();
      fileFormat = (fileFormat.length === 0) ? 'others' : fileFormat;

      if (!filesInfoObj[fileFormat]) {
        filesInfoObj[fileFormat] = 0;
      }

      filesInfoObj[fileFormat] += lineCount;
    },

    selectAll() {
      if (!this.isSelectAllChecked) {
        this.selectedFileFormats = this.fileFormats.slice();
      } else {
        this.selectedFileFormats = [];
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

    getSelectedFiles() {
      if (this.fileFormats.length === this.selectedFileFormats.length) {
        this.isSelectAllChecked = true;
      } else if (this.selectedFileFormats.length === 0) {
        this.isSelectAllChecked = false;
      }
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
        this.info.filesLinesObj[fileFormat] - this.filesBlankLinesObj[fileFormat]}`;
    },

    getTotalFileBlankLineInfo() {
      return `Total: Blank: ${this.totalBlankLineCount}, Non-Blank: ${
        this.totalLineCount - this.totalBlankLineCount}`;
    },
  },

  computed: {
    selectedFiles() {
      return this.files.filter((file) => this.isSelected(file.path));
    },
    filesExistingLinesObj() {
      return Object.keys(this.info.filesLinesObj)
          .filter((type) => this.info.filesLinesObj[type] > 0)
          .reduce((acc, key) => ({
            ...acc, [key]: this.info.filesLinesObj[key],
          }), {});
    },
  },

  created() {
    this.initiate();
  },

  updated() {
    window.updateToggleButton();
  },
};
