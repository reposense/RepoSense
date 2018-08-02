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

    mergeSegments(segments) {
      let lastAuthored;
      const mergedSegments = [];

      segments.forEach((segment) => {
        if (lastAuthored !== segment.authored || mergedSegments.length === 0) {
          mergedSegments.push(segment);
          lastAuthored = segment.authored;
        } else {
          const last = mergedSegments[mergedSegments.length - 1];
          last.lines = last.lines.concat(segment.lines);
        }
      });

      return mergedSegments;
    },

    removeSmallUnauthored(segments) {
      const MIN_LINES = 5;
      const res = [];

      segments.forEach((segment) => {
        if (segment.lines.length < MIN_LINES && !segment.authored) {
          if (res.length === 0) {
            const { lines } = segments[1];
            segments[1].lines = segment.lines.concat(lines);
          } else {
            const last = res[res.length - 1];
            last.lines = segment.lines.concat(last.lines);
          }
        } else {
          res.push(segment);
        }
      });

      return res;
    },

    removeEmptySegments(segments) {
      const res = [];
      segments.forEach((segment) => {
        if (segment.lines.join('') !== '') {
          res.push(segment);
        }
      });

      return res;
    },

    processFiles(files) {
      const res = [];

      files.forEach((file) => {
        if (file.authorContributionMap[this.info.author]) {
          const out = {};
          out.path = file.path;

          const segments = this.splitSegments(file.lines);
          const bigSegments = this.removeSmallUnauthored(segments);
          const validSegments = this.removeEmptySegments(bigSegments);
          const mergedSegments = this.mergeSegments(validSegments);

          out.segments = mergedSegments;
          res.push(out);
        }
      });

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
