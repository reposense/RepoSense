<template lang="pug">
#authorship
  span.large-font Code Panel
  .toolbar(v-if="hasCommits(info)")
    a(v-if="hasActiveFile", v-on:click="expandAll(false)") hide all file details
    a(v-else, v-on:click="expandAll(true)") show all file details
  .title
    a.repoName.large-font(
      v-bind:href="info.location", target="_blank",
      v-bind:title="'Click to open the repo'"
    ) {{ info.repo }}
    .author.medium-font
      span &#8627; &nbsp;
      span {{ info.name }} ({{ info.author }})
    .period.medium-font
      span &#8627; &nbsp;
      span {{ info.minDate }} to {{ info.maxDate }}
    .contribution(v-if="isLoaded && files.length!=0")
      .sorting.mui-form--inline
        .mui-select
          select.medium-font(v-model="filesSortType")
            option(value="lineOfCode") LoC
            option(value="path") Path
            option(value="fileName") File Name
            option(value="fileType") File Type
          label.medium-font sort by
        .mui-select.sort-order
          select.medium-font(v-model="toReverseSortFiles")
            option(v-bind:value='true') Descending
            option(v-bind:value='false') Ascending
          label.medium-font order
      .searchbox
        input.radio-button--search(type="radio", value="search", v-model="filterType")
        form.file-picker.mui-form--inline(onsubmit="return false")
          input#search.medium-font(type="search", placeholder="Filter by glob", v-on:change="updateFilterSearch",
            v-on:click="indicateSearchBar")
          button#submit-button.medium-font(type="submit", v-on:click="indicateSearchBar") Filter
      .fileType
        input.radio-button--checkbox(type="radio", value="checkboxes", v-model="filterType")
        .checkboxes.mui-form--inline(v-if="files.length > 0")
          label
            input.mui-checkbox--fileType(type="checkbox", v-model="isSelectAllChecked")
            span(v-bind:title="getTotalFileBlankLineInfo()")
              span.select.medium-font All:&nbsp;
              span.loc.medium-font {{ totalLineCount }}&nbsp;
              span.bloc.medium-font ({{ totalBlankLineCount }})
          template(v-for="fileType in Object.keys(getFileTypeExistingLinesObj)")
            label(v-bind:key="fileType")
              input.mui-checkbox--fileType(type="checkbox", v-bind:id="fileType", v-bind:value="fileType",
                v-on:click="indicateCheckBoxes", v-model="selectedFileTypes")
              span(v-bind:title="getFileTypeBlankLineInfo(fileType)")
                span {{ fileType }}: {{ getFileTypeExistingLinesObj[fileType] }}&nbsp;
                span.bloc ({{ fileTypeBlankLinesObj[fileType] }})

  .files(v-if="isLoaded")
    .empty(v-if="files.length === 0") nothing to see here :(
    template(v-for="file in selectedFiles")
      .file.active(v-bind:key="file.path")
        .title
          span.path(onclick="toggleNext(this.parentNode)", v-on:click="updateCount") {{ file.path }}&nbsp;
          span.loc ({{ file.lineCount }} lines)
          a(
            v-bind:href="getFileLink(file, 'commits')", target="_blank",
            title="click to view the history view of file"
          )
            i.button.fas.fa-history
          a(
            v-bind:href="getFileLink(file, 'blame')", target="_blank",
            title="click to view the blame view of file"
          )
            i.button.fas.fa-user-edit
        pre.hljs
          template(v-for="segment in file.segments")
            v_segment(v-bind:segment="segment", v-bind:path="file.path")

  .empty(v-else) loading...
</template>

<script>
import segment from './segment.vue';

const filesSortDict = {
  lineOfCode: (file) => file.lineCount,
  path: (file) => file.path,
  fileName: (file) => file.path.split(/[/]+/).pop(),
  fileType: (file) => file.fileType,
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

export default {
  components: {
    v_segment: segment,
  },
  props: ['info'],
  data() {
    return {
      isLoaded: false,
      files: [],
      filterType: 'checkboxes',
      selectedFileTypes: [],
      fileTypes: [],
      fileTypeBlankLinesObj: {},
      totalLineCount: '',
      totalBlankLineCount: '',
      filesSortType: 'lineOfCode',
      toReverseSortFiles: true,
      hasActiveFile: true,
      filterSearch: '*',
    };
  },

  computed: {
    sortingFunction() {
      return (a, b) => (this.toReverseSortFiles ? -1 : 1)
              * window.comparator(filesSortDict[this.filesSortType])(a, b);
    },

    isSelectAllChecked: {
      get() {
        return this.selectedFileTypes.length === this.fileTypes.length;
      },
      set(value) {
        if (this.filterType === 'search') {
          this.indicateCheckBoxes();
        }
        if (value) {
          this.selectedFileTypes = this.fileTypes.slice();
        } else {
          this.selectedFileTypes = [];
        }
      },
    },

    selectedFiles() {
      return this.files.filter((file) => this.selectedFileTypes.includes(file.fileType)
              && minimatch(file.path, this.filterSearch, { matchBase: true }))
          .sort(this.sortingFunction);
    },

    getFileTypeExistingLinesObj() {
      const numLinesModified = {};
      Object.entries(this.filesLinesObj)
          .filter(([, value]) => value > 0)
          .forEach(([langType, value]) => {
            numLinesModified[langType] = value;
          });
      return numLinesModified;
    },
  },

  watch: {
    selectedFiles() {
      setTimeout(this.updateCount, 0);
    },

    filterType() {
      if (this.filterType === 'checkboxes') {
        const searchBar = document.getElementById('search');
        searchBar.value = '';
        this.filterSearch = '*';
      } else {
        this.selectedFileTypes = this.fileTypes.slice();
      }
    },
  },

  created() {
    this.initiate();
    this.setInfoHash();
  },

  methods: {
    initiate() {
      const repo = window.REPOS[this.info.repo];

      this.getRepoProps(repo);
      if (!repo || !this.info.author) {
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
      }
    },

    setInfoHash() {
      const { addHash, encodeHash } = window;
      addHash('tabAuthor', this.info.author);
      addHash('tabRepo', this.info.repo);
      encodeHash();
    },

    expandAll(hasActiveFile) {
      const renameValue = hasActiveFile ? 'file active' : 'file';

      const files = document.getElementsByClassName('file');
      Array.from(files).forEach((file) => {
        file.className = renameValue;
      });

      this.hasActiveFile = hasActiveFile;
    },

    updateCount() {
      this.hasActiveFile = document.getElementsByClassName('file active').length > 0;
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

      lines.forEach((line, lineCount) => {
        const authored = (line.author && line.author.gitId === this.info.author);

        if (authored !== lastState || lastId === -1) {
          segments.push({
            authored,
            lines: [],
            lineNumbers: [],
          });

          lastId += 1;
          lastState = authored;
        }

        const content = line.content || ' ';
        segments[lastId].lines.push(content);

        segments[lastId].lineNumbers.push(lineCount + 1);

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
        if (this.filesLinesObj[file] !== 0) {
          this.selectedFileTypes.push(file);
          this.fileTypes.push(file);
        }
      });

      this.selectedFileTypes = this.fileTypes.slice();
      this.fileTypeBlankLinesObj = fileTypeBlanksInfoObj;
      this.files = res;
      this.isLoaded = true;
    },

    addBlankLineCount(fileType, lineCount, filesInfoObj) {
      if (!filesInfoObj[fileType]) {
        filesInfoObj[fileType] = 0;
      }

      filesInfoObj[fileType] += lineCount;
    },

    updateFilterSearch(evt) {
      if (this.filterType === 'checkboxes') {
        this.indicateSearchBar();
      }
      this.filterSearch = (evt.target.value.length !== 0) ? evt.target.value : '*';
    },

    indicateSearchBar() {
      this.selectedFileTypes = this.fileTypes.slice();
      this.filterType = 'search';
    },

    indicateCheckBoxes() {
      if (this.filterType === 'search') {
        const searchBar = document.getElementById('search');
        searchBar.value = '';
        this.filterSearch = '*';
        this.filterType = 'checkboxes';
      }
    },

    getFileLink(file, path) {
      const repo = window.REPOS[this.info.repo];

      return `${window.BASE_URL}/${
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
};

</script>

<style lang="scss" scoped>
@import '../static/css/colors';

/* Authorship */
.title {
  .repoName {
    cursor: pointer;
  }

  .author {
    margin-left: .3rem;
  }

  .period {
    margin-left: 2rem;
  }

  .contribution {
    color: mui-color('red');

    .fileType {
      align-items: center;
      display: flex;
      margin-left: 3rem;
      margin-top: 1.0rem;
    }

    .radio-button--search {
      float: left;
      margin: 1.75rem 2.0rem 0 0;
    }

    .radio-button--checkbox {
      float: left;
      margin: 0 2.0rem 0 -3rem;
      vertical-align: middle;
    }

    #search {
      margin-top: 1.25rem;
      padding: .5rem 1.0rem .25rem 1.0rem;
      width: 30%;
    }

    #submit-button {
      background-color: mui-color('blue');
      color: mui-color('white');
      margin: 1.0rem 0 0 .25rem;
      padding: .5rem 1.0rem .25rem 1.0rem;
    }

    .select {
      font-weight: bold;
    }

    .loc {
      font-weight: bolder;
    }

    .bloc {
      color: mui-color('grey');
    }

    span.fileType {
      span.bloc::after {
        content: ', ';
      }

      span.bloc:last-child::after {
        content: '';
      }
    }

    .mui-checkbox--fileType {
      vertical-align: middle;
    }

    .checkboxes {
      label {
        align-items: center;
        display: inline-flex;
        padding-right: 10px;
      }

      span {
        margin-left: .25rem;
      }
    }
  }
}

.files {
  clear: left;

  .file {
    pre {
      display: none;
    }

    &.active {
      pre {
        display: grid;

        .hljs {
          // overwrite hljs library
          display: inherit;
          padding: 0;
        }
      }
    }
  }

  .title {
    font-size: 1.25rem;
    font-weight: bold;
    margin-top: 1rem;

    .path {
      cursor: pointer;
    }

    .loc {
      color: mui-color('grey');
    }

    .button {
      color: mui-color('grey');
      font-size: 1.15rem;
      margin-left: .5rem;
      text-decoration: none;
    }
  }
}

.empty {
  text-align: center;
}

</style>
