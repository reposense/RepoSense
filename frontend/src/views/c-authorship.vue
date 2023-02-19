<template lang="pug">
#authorship
  .panel-title
    span Code Panel
  .toolbar--multiline
    a(
      v-if="activeFilesCount < selectedFiles.length",
      v-on:click="toggleAllFileActiveProperty(true)"
    ) show all file details
    a(v-if="activeFilesCount > 0", v-on:click="toggleAllFileActiveProperty(false)") hide all file details
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
    .contribution(v-if="isLoaded && info.files.length!=0")
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
          v-on:change="indicateSearchBar"
        )
        .mui-form--inline
          input#search(
            type="search",
            placeholder="Filter by glob",
            ref="searchBar",
            v-bind:value="searchBarValue",
            v-on:keyup.enter="indicateSearchBar(); updateSearchBarValue();"
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
        .checkboxes.mui-form--inline(v-if="info.files.length > 0")
          label(style='background-color: #000000; color: #ffffff')
            input.mui-checkbox--fileType#all(type="checkbox", v-model="isSelectAllChecked")
            span(v-bind:title="getTotalFileBlankLineInfo()")
              span All&nbsp;
              span {{ totalLineCount }}&nbsp;
              span ({{ totalLineCount - totalBlankLineCount }})&nbsp;
          template(v-for="fileType in Object.keys(fileTypeLinesObj)", v-bind:key="fileType")
            label(
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
          br
          label.binary-fileType(v-if="binaryFilesCount > 0")
            input.mui-checkbox--fileType(type="checkbox", v-model="isBinaryChecked")
            span(
              v-bind:title="`${binaryFilesCount} \
              binary files (not included in total line count)`"
            )
              span {{ binaryFilesCount }} binary file(s)
          label.ignored-fileType(v-if="ignoredFilesCount > 0")
            input.mui-checkbox--fileType(type="checkbox", v-model="isIgnoredChecked")
            span(
              v-bind:title="`${ignoredFilesCount} \
              ignored files (included in total line count)`"
            )
              span {{ ignoredFilesCount }} ignored file(s)

  .files(v-if="isLoaded")
    .empty(v-if="info.files.length === 0") nothing to see here :(
    template(v-for="(file, i) in selectedFiles", v-bind:key="file.path")
      .file
        .title
          span.caret(v-on:click="toggleFileActiveProperty(file)")
            .tooltip(v-show="file.active")
              font-awesome-icon(icon="caret-down", fixed-width)
              span.tooltip-text Click to hide file details
            .tooltip(v-show="!file.active")
              font-awesome-icon(icon="caret-right", fixed-width)
              span.tooltip-text Click to show file details
          span.index {{ i + 1 }}. &nbsp;
          span.path
            span(
              v-bind:class="{'selected-parameter':\
                  this.filesSortType === 'path' || this.filesSortType === 'fileName'}"
            ) {{ getFirstPartOfPath(file) }}&nbsp;
            span.in(v-if="this.filesSortType === 'fileName'") in&nbsp;
            span(v-if="this.filesSortType === 'fileName'") {{ getSecondPartOfPath(file) }}&nbsp;
          span.fileTypeLabel(
            v-if="!file.isBinary && !file.isIgnored",
            v-bind:style="{\
              'background-color': fileTypeColors[file.fileType],\
              'color': getFontColor(fileTypeColors[file.fileType])\
              }",
            v-bind:class="{'selected-label':\
                this.filesSortType === 'lineOfCode' || this.filesSortType === 'fileType'}"
          )
            span(
              v-bind:class="{'selected-parameter':\
                  this.filesSortType === 'lineOfCode' || this.filesSortType === 'fileType'}"
            ) {{ getFirstPartOfLabel(file) }}&nbsp;
            span {{ getSecondPartOfLabel(file) }}
          span.fileTypeLabel.binary(v-if='file.isBinary') binary &nbsp;
          span.ignored-tag.fileTypeLabel(
            v-if='file.isIgnored'
          ) ignored ({{ file.lineCount }}) &nbsp;
          span.icons
            a(
              v-bind:class="!isBrokenLink(getHistoryLink(file)) ? '' : 'broken-link'",
              v-bind:href="getHistoryLink(file)", target="_blank"
            )
              .tooltip
                font-awesome-icon.button(icon="history")
                span.tooltip-text {{getLinkMessage(getHistoryLink(file), 'Click to view the history view of file')}}
            a(
              v-if='!file.isBinary',
              v-bind:class="!isBrokenLink(getBlameLink(file)) ? '' : 'broken-link'",
              v-bind:href="getBlameLink(file)", target="_blank",
              title="click to view the blame view of file"
            )
              .tooltip
                font-awesome-icon.button(icon="user-edit")
                span.tooltip-text {{getLinkMessage(getBlameLink(file), 'Click to view the blame view of file')}}
        pre.file-content(v-if="file.isBinary", v-show="file.active")
          .binary-segment
            .indicator BIN
            .bin-text Binary file not shown.
        pre.file-content(v-else-if="file.isIgnored", v-show="file.active")
          .ignored-segment
            .ignore-text File is ignored.
        pre.hljs.file-content(v-else-if="file.wasCodeLoaded", v-show="file.active")
          c-segment-collection(v-bind:segments="file.segments", v-bind:path="file.path")
</template>

<script>
import { mapState } from 'vuex';
import minimatch from 'minimatch';
import brokenLinkDisabler from '../mixin/brokenLinkMixin';
import cSegmentCollection from '../components/c-segment-collection.vue';
import Segment from '../utils/segment';

const getFontColor = window.getFontColor;

const filesSortDict = {
  lineOfCode: (file) => file.lineCount,
  path: (file) => file.path,
  fileName: (file) => file.path.split(/[/]+/).pop(),
  fileType: (file) => file.fileType,
};

function authorshipInitialState() {
  return {
    isLoaded: false,
    selectedFiles: [],
    filterType: 'checkboxes',
    selectedFileTypes: [],
    fileTypes: [],
    filesLinesObj: {},
    fileTypeBlankLinesObj: {},
    filesSortType: 'lineOfCode',
    toReverseSortFiles: true,
    isBinaryFilesChecked: false,
    isIgnoredFilesChecked: false,
    searchBarValue: '',
    authorDisplayName: '',
  };
}

const repoCache = [];

export default {
  name: 'c-authorship',
  components: {
    cSegmentCollection,
  },
  mixins: [brokenLinkDisabler],
  emits: [
    'deactivate-tab',
  ],
  data() {
    return authorshipInitialState();
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

    isBinaryChecked: {
      get() {
        return this.isBinaryFilesChecked;
      },
      set(value) {
        if (value) {
          this.isBinaryFilesChecked = true;
        } else {
          this.isBinaryFilesChecked = false;
        }

        this.updateSelectedFiles();
        this.indicateCheckBoxes();
      },
    },

    isIgnoredChecked: {
      get() {
        return this.isIgnoredFilesChecked;
      },
      set(value) {
        if (value) {
          this.isIgnoredFilesChecked = true;
        } else {
          this.isIgnoredFilesChecked = false;
        }

        this.updateSelectedFiles();
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

    binaryFilesCount() {
      return this.info.files.filter((file) => file.isBinary).length;
    },

    ignoredFilesCount() {
      return this.info.files.filter((file) => file.isIgnored).length;
    },

    ...mapState({
      fileTypeColors: 'fileTypeColors',
      info: 'tabAuthorshipInfo',
    }),
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

    selectedFileTypes: {
      deep: true,
      handler() {
        this.updateSelectedFiles();
      },
    },

    toReverseSortFiles() {
      window.addHash('reverseAuthorshipOrder', this.toReverseSortFiles);
      window.encodeHash();
      this.updateSelectedFiles();
    },

    info() {
      Object.assign(this.$data, authorshipInitialState());
      this.initiate();
    },
  },

  created() {
    this.initiate();
  },

  beforeUnmount() {
    this.removeAuthorshipHashes();
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

      if (hash.authorshipFileTypes) {
        this.selectedFileTypes = hash.authorshipFileTypes
          .split(window.HASH_DELIMITER)
          .filter((fileType) => this.fileTypes.includes(fileType));
      } else {
        this.resetSelectedFileTypes();
      }

      if (hash.authorshipIsBinaryFileTypeChecked) {
        this.isBinaryFilesChecked = hash.authorshipIsBinaryFileTypeChecked === 'true';
      }

      if (hash.authorshipIsIgnoredFilesChecked) {
        this.isIgnoredFilesChecked = hash.authorshipIsIgnoredFilesChecked === 'true';
      }

      if ('authorshipFilesGlob' in hash) {
        this.indicateSearchBar();
        this.searchBarValue = hash.authorshipFilesGlob;
      }
    },

    resetSelectedFileTypes() {
      this.selectedFileTypes = this.info.checkedFileTypes
        ? this.info.checkedFileTypes.filter((fileType) => this.fileTypes.includes(fileType))
        : [];
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
      window.removeHash('authorshipIsBinaryFileTypeChecked');
      window.removeHash('authorshipIsIgnoredFilesChecked');
      window.removeHash('authorshipFilesGlob');
      window.removeHash('authorshipSortBy');
      window.removeHash('reverseAuthorshipOrder');
      window.removeHash('tabAuthor');
      window.removeHash('tabRepo');
      window.removeHash('authorshipIsMergeGroup');
      window.encodeHash();
    },

    async initiate() {
      const repo = window.REPOS[this.info.repo];

      if (!repo || !this.info.author) {
        this.$store.commit('updateTabState', false);
        return;
      }
      if (repoCache.length === 2) {
        const toRemove = repoCache.shift();
        if (toRemove !== this.info.repo) {
          delete window.REPOS[toRemove].files;
        }
      }
      repoCache.push(this.info.repo);

      let { files } = repo;
      if (!files) {
        files = await window.api.loadAuthorship(this.info.repo);
      }

      const author = repo.users.find((user) => user.name === this.info.author);
      if (author) {
        this.authorDisplayName = author.displayName;
      }

      this.processFiles(files);

      if (this.info.isRefresh) {
        this.retrieveHashes();
      } else {
        this.resetSelectedFileTypes();
      }

      this.setInfoHash();
    },

    toggleAllFileActiveProperty(isActive) {
      this.$store.commit('setAllAuthorshipFileActiveProperty', { isActive, files: this.selectedFiles });
    },

    toggleFileActiveProperty(file) {
      this.$store.commit('toggleAuthorshipFileActiveProperty', file);
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
          segments.push(new Segment(
            authored,
            [],
            [],
          ));

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
      const SINGLE_FILE_LINE_COUNT_THRESHOLD = 2000;
      const SINGLE_FILE_CHAR_COUNT_THRESHOLD = 1000000;
      const res = [];
      const fileTypeBlanksInfoObj = {};

      files.filter((file) => this.isValidFile(file)).forEach((file) => {
        const contributionMap = file.authorContributionMap;

        const lineCnt = this.info.isMergeGroup
            ? this.getContributionFromAllAuthors(contributionMap)
            : contributionMap[this.info.author];

        const out = {};
        out.path = file.path;
        out.lineCount = lineCnt;
        out.active = lineCnt <= SINGLE_FILE_LINE_COUNT_THRESHOLD && !file.isBinary;
        out.wasCodeLoaded = lineCnt <= SINGLE_FILE_LINE_COUNT_THRESHOLD;
        out.fileType = file.fileType;
        out.fileSize = file.fileSize;
        out.isIgnored = !!file.isIgnored;
        out.isBinary = !!file.isBinary;

        if (this.filesLinesObj[out.fileType]) {
          this.filesLinesObj[out.fileType] += lineCnt;
        } else {
          this.filesLinesObj[out.fileType] = lineCnt;
        }

        if (!out.isBinary && !out.isIgnored) {
          out.charCount = file.lines.reduce(
            (count, line) => count + (line ? line.content.length : 0),
            0,
          );
        }

        if (!file.isBinary) {
          const segmentInfo = this.splitSegments(file.lines);
          out.segments = segmentInfo.segments;
          out.blankLineCount = segmentInfo.blankLineCount;

          this.addBlankLineCount(file.fileType, segmentInfo.blankLineCount, fileTypeBlanksInfoObj);
        }

        res.push(out);
      });

      res.sort((a, b) => b.lineCount - a.lineCount).forEach((file) => {
        // hide files over total char count limit
        if (!file.isIgnored && !file.isBinary && file.active) {
          file.active = file.charCount <= SINGLE_FILE_CHAR_COUNT_THRESHOLD;
          file.wasCodeLoaded = file.active;
        }
      });

      Object.keys(this.filesLinesObj).forEach((file) => {
        if (this.filesLinesObj[file] !== 0) {
          this.fileTypes.push(file);
        }
      });

      this.fileTypeBlankLinesObj = fileTypeBlanksInfoObj;
      this.$store.commit('updateTabAuthorshipFiles', res);
      this.updateSelectedFiles(true);
    },

    isValidFile(file) {
      return this.info.isMergeGroup
          ? Object.entries(file.authorContributionMap)
            .some((authorCount) => !this.isUnknownAuthor(authorCount[0]))
          : this.info.author in file.authorContributionMap;
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
      window.removeHash('authorshipIsBinaryFileTypeChecked');
      window.removeHash('authorshipIsIgnoredFilesChecked');
      window.encodeHash();
    },

    updateFileTypeHash() {
      const fileTypeHash = this.selectedFileTypes.length > 0
          ? this.selectedFileTypes.reduce((a, b) => `${a}~${b}`)
          : '';

      window.addHash('authorshipFileTypes', fileTypeHash);
      window.addHash('authorshipIsBinaryFileTypeChecked', this.isBinaryFilesChecked);
      window.addHash('authorshipIsIgnoredFilesChecked', this.isIgnoredFilesChecked);
      window.removeHash('authorshipFilesGlob');
      window.encodeHash();
    },

    async updateSelectedFiles(setIsLoaded = false) {
      await this.$store.dispatch('incrementLoadingOverlayCountForceReload', 1);
      this.selectedFiles = this.info.files.filter(
        (file) => ((this.selectedFileTypes.includes(file.fileType) && !file.isBinary && !file.isIgnored)
          || (file.isBinary && this.isBinaryFilesChecked) || (file.isIgnored && this.isIgnoredFilesChecked))
          && minimatch(file.path, this.searchBarValue || '*', { matchBase: true, dot: true }),
      )
        .sort(this.sortingFunction);
      if (setIsLoaded) {
        this.isLoaded = true;
      }
      this.$store.commit('incrementLoadingOverlayCount', -1);
    },

    indicateSearchBar() {
      this.selectedFileTypes = this.fileTypes.slice();
      this.isBinaryFilesChecked = true;
      this.isIgnoredFilesChecked = true;
      this.filterType = 'search';
    },

    indicateCheckBoxes() {
      this.searchBarValue = '';
      this.filterType = 'checkboxes';
      this.updateFileTypeHash();
    },

    getHistoryLink(file) {
      const repo = window.REPOS[this.info.repo];
      return window.getHistoryLink(this.info.repo, repo.branch, file.path);
    },

    getBlameLink(file) {
      const repo = window.REPOS[this.info.repo];
      return window.getBlameLink(this.info.repo, repo.branch, file.path);
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

    getFirstPartOfPath(file) {
      const fileSplitIndex = file.path.lastIndexOf('/');
      const fileNameOnly = file.path.slice(fileSplitIndex + 1);

      if (this.filesSortType === 'fileName') {
        return `${fileNameOnly}`;
      }
      return file.path;
    },

    getSecondPartOfPath(file) {
      const fileSplitIndex = file.path.lastIndexOf('/');
      const filePathOnly = file.path.slice(0, fileSplitIndex + 1);

      if (!filePathOnly) {
        return '/';
      }
      return filePathOnly;
    },

    getFirstPartOfLabel(file) {
      if (this.filesSortType === 'lineOfCode') {
        return `${file.lineCount} (${file.lineCount - file.blankLineCount})`;
      }
      return `${file.fileType}`;
    },

    getSecondPartOfLabel(file) {
      if (this.filesSortType === 'lineOfCode') {
        return `${file.fileType}`;
      }
      return `${file.lineCount} (${file.lineCount - file.blankLineCount})`;
    },

    getFontColor,
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

      .mui-form--inline {
        align-items: flex-end;
        display: flex;
        flex-wrap: wrap;

        #search {
          @include medium-font;
          margin-top: 1.25rem;
          min-width: 130px;
          padding: .5rem 1.0rem .25rem 1.0rem;
          width: 70%;
        }

        #submit-button {
          @include medium-font;
          background-color: mui-color('blue');
          color: mui-color('white');
          margin: 1.0rem 0 0 .25rem;
          padding: .5rem 1.0rem .25rem 1.0rem;
        }
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

      .binary-fileType {
        background-color: mui-color('white');
        color: mui-color('grey', '800');
      }

      .ignored-fileType {
        background-color: mui-color('white');
        color: mui-color('grey', '800');
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

    .ignored-tag {
      background-color: mui-color('black');
      color: mui-color('white');
    }

    .title {
      background-color: mui-color('github', 'title-background');
      border: 1px solid mui-color('github', 'border');
      border-radius: 4px 4px 0 0;
      display: flex;
      flex-wrap: wrap;
      font-size: medium;
      margin-top: 1rem;
      padding: .3em .5em;
      white-space: pre-wrap;
      word-break: break-all;

      .caret {
        cursor: pointer;
        order: -2;
        overflow-wrap: break-word;
      }

      .index {
        order: -2;
      }

      .path {
        .in {
          color: mui-color('grey', '600');
        }
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

      .selected-parameter {
        font-weight: bold;
      }

      .selected-label {
        order: -1;
      }
    }

    .binary-segment {
      background-color: mui-color('white');

      .indicator {
        float: left;
        font-weight: bold;
        padding-left: 1rem;
      }

      .bin-text {
        color: mui-color('grey', '800');
        padding-left: 4rem;
      }
    }

    .ignored-segment {
      background-color: mui-color('white');

      .ignore-text {
        color: mui-color('grey', '800');
        padding-left: 4rem;
      }
    }

    .segment {
      border-left: .25rem solid mui-color('green');
      .code {
        background-color: mui-color('github', 'authored-code-background');
        padding-left: 1rem;
      }
      .line-number {
        color: mui-color('grey');
        float: left;
        // Not allowing user to select text
        -webkit-touch-callout: none; /* iOS Safari */
        -webkit-user-select: none; /* Safari */
        -khtml-user-select: none; /* Konqueror HTML */
        -moz-user-select: none; /* Firefox */
        -ms-user-select: none; /* Internet Explorer/Edge */
        user-select: none; /* Non-prefixed version, currently supported by Chrome and Opera */
        width: 2rem;
        // overwrite all hljs colours
        [class^='hljs'] {
          color: mui-color('grey');
        }
      }
      .line-content {
        padding-left: 2rem;
        word-break: break-word;
      }
      &.untouched {
        $grey: mui-color('grey', '400');
        border-left: .25rem solid $grey;
        height: 20px; /* height of a single line of code */
        position: relative;
        &.active {
          height: auto;
          .code {
            background-color: mui-color('white');
          }
        }
        .closer {
          cursor: pointer;
          // custom margin for position of toggle icon
          margin: .2rem 0 0 -.45rem;
          position: absolute;
          &.bottom {
            //custom margin for position of toggle icon at the bottom of segment
            margin: -1.05rem 0 0 -.45rem;
          }
          .icon {
            background-color: mui-color('white');
            color: mui-color('grey');
            width: .75em;
          }
        }
      }
    }
  }

  .empty {
    text-align: center;
  }
}
</style>
