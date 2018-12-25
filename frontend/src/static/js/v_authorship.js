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
  props: ['info', 'path'],
  template: window.$('v_authorship').innerHTML,
  data() {
    return {
      isLoaded: false,
      files: [],
      filesLinesObj: {},
      totalLineCount: "",
      link: "",
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
      this.link = this.path + this.info.author + ".md";
    },

    splitSegments(lines) {
      // split into segments separated by authored
      let lastState;
      let lastId = -1;
      const segments = [];

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
      });

      return segments;
    },

    processFiles(files) {
      const res = [];
      let filesInfoObj = {};
      let totalLineCount = 0;

      files.forEach((file) => {
        const lineCnt = file.authorContributionMap[this.info.author];
        if (lineCnt) {
          totalLineCount += lineCnt;
          const out = {};
          out.path = file.path;
          out.lineCount = lineCnt;
          this.addLineCountToFileType(file.path, lineCnt, filesInfoObj);

          const segments = this.splitSegments(file.lines);
          out.segments = segments;
          res.push(out);
        }
      });

      this.totalLineCount = totalLineCount;
      res.sort((a, b) => b.lineCount - a.lineCount);

      this.filesLinesObj = this.sortFileTypeAlphabetically(filesInfoObj);
      this.files = res;
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
