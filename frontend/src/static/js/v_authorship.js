window.toggleNext = function toggleNext(ele) {
  // function for toggling unopened code
  const target = ele.nextSibling;
  const child = ele.childNodes;

  let style = 'none';
  let childBackground = '#bfbdbd';
  let toolTipMessage = 'Show untouched code';

  if (target.style.display === 'none') {
    style = '';
    childBackground = '#000';
    toolTipMessage = 'Hide untouched code';
  }

  target.style.display = style;

  if (target.className === 'code') {
    child[0].style.background = childBackground;
    target.style.borderLeft = '4px solid rgba(197, 206, 197, 1)';
    child[2].innerHTML = toolTipMessage;
  }
};

const repoCache = [];
window.vAuthorship = {
  props: ['info'],
  template: window.$('v_authorship').innerHTML,
  data() {
    return {
      isLoaded: false,
      files: [],
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
        segments[lastId].lines.push(line.content);
      });

      return segments;
    },

    processFiles(files) {
      const res = [];

      files.forEach((file) => {
        const lineCnt = file.authorContributionMap[this.info.author];
        if (lineCnt) {
          const out = {};
          out.path = file.path;
          out.lineCount = lineCnt;

          const segments = this.splitSegments(file.lines);
          out.segments = segments;
          res.push(out);
        }
      });

      res.sort((a, b) => b.lineCount - a.lineCount);

      this.files = res;
      this.isLoaded = true;
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
