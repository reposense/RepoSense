<template lang="pug">
#authorship
  .panel-title
    span Code Panel
  .toolbar--multiline
    a(v-if="activeFilesCount < selectedFiles.length", v-on:click="expandAll()")
      |show all file details
    a(v-if="activeFilesCount > 0", v-on:click="collapseAll()") hide all file details
  .panel-heading
    a.group-name(
      v-bind:href="info.location", target="_blank",
      v-bind:title="'Click to open the repo'"
    ) {{ info.repo }}
    .author(v-if="!info.isMergeGroup")
      span &#8627; &nbsp;
      span {{ authorDisplayName }} ({{ info.author }})
    .period
      span &#8627; &nbsp;
      span {{ info.minDate }} to {{ info.maxDate }}
        |&nbsp;&nbsp; ({{ selectedFiles.length }} files changed)
  .title
    .contribution(v-if="isLoaded && files.length!=0")
      .sorting.mui-form--inline
        .mui-select.sort-by
          select(v-model="filesSortType")
            option(value="lineOfCode") LoC
            option(value="path") Path
            option(value="fileName") File Name
            option(value="fileType") File Type
          label sort by
        .mui-select.sort-order
          select(v-model="toReverseSortFiles")
            option(v-bind:value='true') Descending
            option(v-bind:value='false') Ascending
          label order
      .searchbox
        input.radio-button--search(
          type="radio",
          value="search",
          v-model="filterType",
          v-on:change="indicateSearchBar(); updateSearchBarValue();"
        )
        .mui-form--inline
          input#search(
            type="search",
            placeholder="Filter by glob",
            ref="searchBar",
            v-bind:value="searchBarValue",
            v-on:focus="indicateSearchBar",
            v-on:keyup.enter="updateSearchBarValue"
          )
          button#submit-button(
            type="button",
            v-on:click="indicateSearchBar(); updateSearchBarValue();"
          ) Filter
      .fileTypes
        input.radio-button--checkbox(
          type="radio",
          value="checkboxes",
          v-model="filterType",
          v-on:change="indicateCheckBoxes"
        )
        .checkboxes.mui-form--inline(v-if="files.length > 0")
          label(style='background-color: #000000; color: #ffffff')
            input.mui-checkbox--fileType#all(type="checkbox", v-model="isSelectAllChecked")
            span(v-bind:title="getTotalFileBlankLineInfo()")
              span All&nbsp;
              span {{ totalLineCount }}&nbsp;
              span ({{ totalLineCount - totalBlankLineCount }})&nbsp;
          template(v-for="fileType in Object.keys(fileTypeLinesObj)")
            label(
              v-bind:key="fileType",
              v-bind:style="{\
                'background-color': fileTypeColors[fileType],\
                'color': getFontColor(fileTypeColors[fileType])\
                }"
            )
              input.mui-checkbox--fileType(type="checkbox",
                v-bind:id="fileType", v-bind:value="fileType",
                v-on:change="indicateCheckBoxes", v-model="selectedFileTypes")
              span(v-bind:title="getFileTypeBlankLineInfo(fileType)")
                span {{ fileType }}&nbsp;{{ fileTypeLinesObj[fileType] }}&nbsp;
                span ({{ fileTypeLinesObj[fileType] - fileTypeBlankLinesObj[fileType] }})&nbsp;

  .files(v-if="isLoaded")
    .empty(v-if="files.length === 0") nothing to see here :(
    template(v-for="(file, i) in selectedFiles")
      .file(v-bind:key="file.path")
        .title
          span.path(v-on:click="toggleFileActiveProperty(file)")
            .tooltip
              font-awesome-icon(icon="caret-down", fixed-width, v-show="file.active")
              span.tooltip-text(v-show="file.active") Click to hide file details
              font-awesome-icon(icon="caret-right", fixed-width, v-show="!file.active")
              span.tooltip-text(v-show="!file.active") Click to show file details
            span {{ i + 1 }}. &nbsp;&nbsp; {{ file.path }} &nbsp;
          span.icons
            a(
              v-bind:href="getFileLink(file, 'commits')", target="_blank"
            )
              .tooltip
                font-awesome-icon.button(icon="history")
                span.tooltip-text Click to view the history view of file
            a(
              v-bind:href="getFileLink(file, 'blame')", target="_blank"
            )
              .tooltip
                font-awesome-icon.button(icon="user-edit")
                span.tooltip-text Click to view the blame view of file
          span.fileTypeLabel(
            v-bind:style="{\
              'background-color': fileTypeColors[file.fileType],\
              'color': getFontColor(fileTypeColors[file.fileType])\
              }"
          ) {{ file.fileType }}&nbsp;{{ file.lineCount }}
            |&nbsp;({{ file.lineCount - file.blankLineCount }})
        pre.hljs.file-content(v-if="file.wasCodeLoaded", v-show="file.active")
          template(v-for="segment in file.segments")
            v-segment(
              v-bind:key="segment.lineNumbers[0]",
              v-bind:segment="segment",
              v-bind:path="file.path"
            )
</template>

<script>
import { mapState } from 'vuex';
import vSegment from '../components/v-segment.vue';

const filesSortDict = {
  lineOfCode: (file) => file.lineCount,
  path: (file) => file.path,
  fileName: (file) => file.path.split(/[/]+/).pop(),
  fileType: (file) => file.fileType,
};

const repoCache = [];
const minimatch = require('minimatch');

export default {
  name: 'v-authorship',
  props: ['info'],
  data() {
    return {
      authorDisplayName: '',
      isLoaded: false,
      files: [],
      selectedFiles: [],
      filterType: 'checkboxes',
      selectedFileTypes: [],
      fileTypes: [],
      filesLinesObj: {},
      fileTypeBlankLinesObj: {},
      filesSortType: 'lineOfCode',
      toReverseSortFiles: true,
      searchBarValue: '',
    };
  },

  watch: {
    filesSortType() {
      window.addHash('authorshipSortBy', this.filesSortType);
      window.encodeHash();
      this.updateSelectedFiles();
    },

    searchBarValue() {
      this.updateSelectedFiles();
    },

    selectedFileTypes() {
      this.updateSelectedFiles();
    },

    toReverseSortFiles() {
      window.addHash('reverseAuthorshipOrder', this.toReverseSortFiles);
      window.encodeHash();
      this.updateSelectedFiles();
    },

    isLoaded() {
      if (this.isLoaded) {
        this.retrieveHashes();
        this.setInfoHash();
      }
    },
  },

  methods: {
    retrieveHashes() {
      const hash = window.hashParams;

      switch (hash.authorshipSortBy) {
      case 'path':
      case 'fileName':
      case 'fileType':
        this.filesSortType = hash.authorshipSortBy;
        break;
      default:
        // Invalid value, use the default value of 'lineOfCode'
      }

      this.toReverseSortFiles = hash.reverseAuthorshipOrder !== 'false';

      this.selectedFileTypes = this.info.checkedFileTypes
        ? this.info.checkedFileTypes.filter((fileType) => this.fileTypes.includes(fileType))
        : [];
      if (hash.authorshipFileTypes) {
        this.selectedFileTypes = hash.authorshipFileTypes
            .split(window.HASH_DELIMITER)
            .filter((fileType) => this.fileTypes.includes(fileType));
      }

      if ('authorshipFilesGlob' in hash) {
        this.indicateSearchBar();
        this.searchBarValue = hash.authorshipFilesGlob;
      }
    },

    setInfoHash() {
      const { addHash } = window;
      // We only set these hashes as they are propagated from summary_charts
      addHash('tabAuthor', this.info.author);
      addHash('tabRepo', this.info.repo);
      addHash('authorshipIsMergeGroup', this.info.isMergeGroup);
      this.updateFileTypeHash();
    },

    removeAuthorshipHashes() {
      window.removeHash('authorshipFileTypes');
      window.removeHash('authorshipFilesGlob');
      window.removeHash('authorshipSortBy');
      window.removeHash('reverseAuthorshipOrder');
      window.removeHash('tabAuthor');
      window.removeHash('tabRepo');
      window.removeHash('authorshipIsMergeGroup');
      window.encodeHash();
    },

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

    getFontColor(color) {
      return window.getFontColor(color);
    },

    getRepoProps(repo) {
      if (repo) {
        if (this.info.isMergeGroup) {
          // sum of all users' file type contribution
          repo.users.forEach((author) => {
            this.updateTotalFileTypeContribution(author.fileTypeContribution);
          });
        } else {
          const author = repo.users.find((user) => user.name === this.info.author);
          if (author) {
            this.authorDisplayName = author.displayName;
            this.filesLinesObj = author.fileTypeContribution;
          }
        }
      }
    },

    updateTotalFileTypeContribution(fileTypeContribution) {
      Object.entries(fileTypeContribution).forEach(([type, cnt]) => {
        if (this.filesLinesObj[type]) {
          this.filesLinesObj[type] += cnt;
        } else {
          this.filesLinesObj[type] = cnt;
        }
      });
    },

    expandAll() {
      this.selectedFiles.forEach((file) => {
        file.active = true;
        file.wasCodeLoaded = true;
      });
    },

    collapseAll() {
      this.selectedFiles.forEach((file) => {
        file.active = false;
      });
    },

    toggleFileActiveProperty(file) {
      file.active = !file.active;
      file.wasCodeLoaded = file.wasCodeLoaded || file.active;
    },

    isUnknownAuthor(name) {
      return name === '-';
    },

    hasCommits(info) {
      const { isMergeGroup, author } = info;
      const repo = window.REPOS[info.repo];
      if (repo) {
        return isMergeGroup
            ? Object.entries(repo.commits.authorFinalContributionMap).some(([name, cnt]) => (
              !this.isUnknownAuthor(name) && cnt > 0
            ))
            : repo.commits.authorFinalContributionMap[author] > 0;
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
        const isAuthorMatched = this.info.isMergeGroup
            ? !this.isUnknownAuthor(line.author.gitId)
            : line.author.gitId === this.info.author;
        const authored = (line.author && isAuthorMatched);

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
      const COLLAPSED_VIEW_LINE_COUNT_THRESHOLD = 2000;
      const res = [];
      const fileTypeBlanksInfoObj = {};

      files.forEach((file) => {
        const contributionMap = file.authorContributionMap;
        const lineCnt = this.info.isMergeGroup
            ? this.getContributionFromAllAuthors(contributionMap)
            : contributionMap[this.info.author];

        if (lineCnt) {
          const out = {};
          out.path = file.path;
          out.lineCount = lineCnt;
          out.active = lineCnt <= COLLAPSED_VIEW_LINE_COUNT_THRESHOLD;
          out.wasCodeLoaded = lineCnt <= COLLAPSED_VIEW_LINE_COUNT_THRESHOLD;
          out.fileType = file.fileType;

          const segmentInfo = this.splitSegments(file.lines);
          out.segments = segmentInfo.segments;
          out.blankLineCount = segmentInfo.blankLineCount;

          this.addBlankLineCount(file.fileType, segmentInfo.blankLineCount,
              fileTypeBlanksInfoObj);
          res.push(out);
        }
      });

      res.sort((a, b) => b.lineCount - a.lineCount);

      Object.keys(this.filesLinesObj).forEach((file) => {
        if (this.filesLinesObj[file] !== 0) {
          this.fileTypes.push(file);
        }
      });

      this.fileTypeBlankLinesObj = fileTypeBlanksInfoObj;
      this.files = res;
      this.isLoaded = true;
      this.updateSelectedFiles();
    },

    getContributionFromAllAuthors(contributionMap) {
      return Object.entries(contributionMap).reduce((acc, [author, cnt]) => (
        (!this.isUnknownAuthor(author) ? acc + cnt : acc)
      ), 0);
    },

    addBlankLineCount(fileType, lineCount, filesInfoObj) {
      if (!filesInfoObj[fileType]) {
        filesInfoObj[fileType] = 0;
      }

      filesInfoObj[fileType] += lineCount;
    },

    updateSearchBarValue() {
      this.searchBarValue = this.$refs.searchBar.value;

      window.addHash('authorshipFilesGlob', this.searchBarValue);
      window.removeHash('authorshipFileTypes');
      window.encodeHash();
    },

    updateFileTypeHash() {
      const fileTypeHash = this.selectedFileTypes.length > 0
          ? this.selectedFileTypes.reduce((a, b) => `${a}~${b}`)
          : '';

      window.addHash('authorshipFileTypes', fileTypeHash);
      window.removeHash('authorshipFilesGlob');
      window.encodeHash();
    },

    updateSelectedFiles() {
      this.$store.commit('incrementLoadingOverlayCount', 1);
      setTimeout(() => {
        this.selectedFiles = this.files.filter(
            (file) => this.selectedFileTypes.includes(file.fileType)
            && minimatch(file.path, this.searchBarValue || '*', { matchBase: true, dot: true }),
        )
            .sort(this.sortingFunction);
        this.$store.commit('incrementLoadingOverlayCount', -1);
      });
    },

    indicateSearchBar() {
      this.selectedFileTypes = this.fileTypes.slice();
      this.filterType = 'search';
    },

    indicateCheckBoxes() {
      this.searchBarValue = '';
      this.filterType = 'checkboxes';
      this.updateFileTypeHash();
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
        if (value) {
          this.selectedFileTypes = this.fileTypes.slice();
        } else {
          this.selectedFileTypes = [];
        }

        this.indicateCheckBoxes();
      },
    },

    activeFilesCount() {
      return this.selectedFiles.filter((file) => file.active).length;
    },

    totalLineCount() {
      return Object.values(this.fileTypeLinesObj).reduce((acc, val) => acc + val, 0);
    },

    totalBlankLineCount() {
      return Object.values(this.fileTypeBlankLinesObj).reduce((acc, val) => acc + val, 0);
    },

    fileTypeLinesObj() {
      const numLinesModified = {};
      Object.entries(this.filesLinesObj)
          .filter(([, value]) => value > 0)
          .forEach(([langType, value]) => {
            numLinesModified[langType] = value;
          });
      return numLinesModified;
    },

    ...mapState(['fileTypeColors']),
  },

  created() {
    this.initiate();
  },

  beforeUnmount() {
    this.removeAuthorshipHashes();
  },

  components: {
    vSegment,
  },
};
</script>

<style lang="scss">
@import '../styles/_colors.scss';

/* Authorship */
#tab-authorship {

  .file-content {
    background-color: mui-color('github', 'title-background');
    border: solid mui-color('github', 'border');
    border-radius: 0 0 4px 4px;
    border-width: 0 1px 1px 1px;
  }

  .title {
    .contribution {
      .radio-button--search {
        float: left;
        margin: 1.75rem 2.0rem 0 0;
      }

      .radio-button--checkbox {
        float: left;
        margin: 0 2.0rem 0 0;
        vertical-align: middle;
      }

      #search {
        @include medium-font;
        margin-top: 1.25rem;
        padding: .5rem 1.0rem .25rem 1.0rem;
        width: 30%;
      }

      #submit-button {
        @include medium-font;
        background-color: mui-color('blue');
        color: mui-color('white');
        margin: 1.0rem 0 0 .25rem;
        padding: .5rem 1.0rem .25rem 1.0rem;
      }

      .searchbox {
        margin-bottom: 1em;
      }

      .select {
        font-weight: bold;
      }

      .loc {
        font-weight: bolder;
      }

      .mui-checkbox--fileType {
        vertical-align: middle;
      }
    }
  }

  .files {
    clear: left;

    .file {
      pre {
        display: grid;
        // GitHub's font family and font size
        font-family: SFMono-Regular, Consolas, Liberation Mono, Menlo, monospace;
        font-size: 12px;
        margin-top: 0;

        .hljs {
          // overwrite hljs library
          display: inherit;
          padding: 0;
          white-space: pre-wrap;
        }
      }
    }

    .title {
      background-color: mui-color('github', 'title-background');
      border: 1px solid mui-color('github', 'border');
      border-radius: 4px 4px 0 0;
      font-size: medium;
      font-weight: bold;
      margin-top: 1rem;
      padding: .3em .5em;

      .path {
        cursor: pointer;
        overflow-wrap: break-word;
      }

      .loc {
        color: mui-color('grey');
      }

      .button {
        color: mui-color('grey');
        margin-left: .5rem;
        text-decoration: none;
      }

      .icons {
        margin-right: 8px;
        vertical-align: middle;
      }
    }
  }

  .empty {
    text-align: center;
  }
}
</style>
