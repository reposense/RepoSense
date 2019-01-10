function toggleNext(ele) {
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

function expandAll(isActive) {
  const renameValue = isActive ? 'file active' : 'file';

  const files = document.getElementsByClassName('file');
  Array.from(files).forEach((file) => {
    file.className = renameValue;
  });
}

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
      selectedFiles: [],
      filesLinesObj: {},
      filesBlankLinesObj: {},
      totalLineCount: "",
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
          .then(files => this.processFiles(files));
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
        blankLineCount
      };
    },

    processFiles(files) {
      const res = [];
      let filesInfoObj = {};
      let filesBlanksInfoObj = {};
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
      for (var file in filesInfoObj) {
        if (filesInfoObj.hasOwnProperty(file)) {
          this.selectedFileTypes.push(file);
          this.fileTypes.push(file);
        }
      }
      this.filesBlankLinesObj = filesBlanksInfoObj;
      this.files = res;
      this.selectedFiles = res;
      this.isLoaded = true;
    },

    addLineCountToFileType(path, lineCount, filesInfoObj) {
      var fileType = path.split(".").pop();
      fileType = (fileType.length === 0) ? "others" : fileType;

      if (!filesInfoObj[fileType]) {
        filesInfoObj[fileType] = 0;
      }

      filesInfoObj[fileType] += lineCount;
    },

    sortFileTypeAlphabetically(unsortedFilesInfoObj) {
      return Object.keys(unsortedFilesInfoObj)
          .sort()
          .reduce((acc, key) => ({
              ...acc, [key]: unsortedFilesInfoObj[key]
          }), {});
    },

    selectAll() {
      if (!this.isSelectAllChecked) {
        this.selectedFileTypes = this.fileTypes;
        this.selectedFiles = this.files;
      } else {
        this.selectedFileTypes = [];
        this.selectedFiles = [];
      }
    },

    selectFile() {
      setTimeout(this.getSelectedFiles, 0);
    },

    getSelectedFiles() {
      if (this.fileTypes.length === this.selectedFileTypes.length) {
        this.selectedFiles = this.files;
        this.isSelectAllChecked = true;
      } else if (this.selectedFileTypes.length === 0) {
        this.selectedFiles = [];
        this.isSelectAllChecked = false;
      } else {
        this.selectedFiles = this.files.filter((file) => this.selectedFileTypes.includes(file.path.split('.').pop()));
      }
    },

    getFileBlankLineInfo(fileType) {
      return fileType + ': ' + 'Blank: ' + this.filesBlankLinesObj[fileType]
          + ', Non-Blank: ' + (this.filesLinesObj[fileType] - this.filesBlankLinesObj[fileType]);
    },

    getTotalFileBlankLineInfo() {
      return 'Total: Blank: ' + this.totalBlankLineCount + ', Non-Blank: '
          + (this.totalLineCount - this.totalBlankLineCount);
    },
  },

  created() {
    this.initiate();
  },

  updated() {
    this.$nextTick(() => {
      document.querySelectorAll('pre.hljs code').forEach((ele) => {
        window.hljs.highlightBlock(ele);
      });
    });
  },
};
