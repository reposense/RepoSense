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
            option(value="linesOfCode") LoC
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
      .file(v-bind:ref="file.path")
        .title(
          v-bind:class="{'sticky':\ file.active}",
          v-bind:ref="`${file.path}-title`"
          )
          span.caret(v-on:click="toggleFileActiveProperty(file)")
            .tooltip(
              v-show="file.active",
              v-on:mouseover="onTitleTooltipHover(`${file.path}-hide-file-tooltip`, `${file.path}-title`)",
              v-on:mouseout="resetTitleTooltip(`${file.path}-hide-file-tooltip`, `${file.path}-title`)"
            )
              font-awesome-icon(icon="caret-down", fixed-width)
              span.tooltip-text(v-bind:ref="`${file.path}-hide-file-tooltip`") Click to hide file details
            .tooltip(
              v-show="!file.active",
              v-on:mouseover="onTitleTooltipHover(`${file.path}-show-file-tooltip`, `${file.path}-title`)",
              v-on:mouseout="resetTitleTooltip(`${file.path}-show-file-tooltip`, `${file.path}-title`)"
            )
              font-awesome-icon(icon="caret-right", fixed-width)
              span.tooltip-text(v-bind:ref="`${file.path}-show-file-tooltip`") Click to show file details
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
                this.filesSortType === 'linesOfCode' || this.filesSortType === 'fileType'}"
          )
            span(
              v-bind:class="{'selected-parameter':\
                  this.filesSortType === 'linesOfCode' || this.filesSortType === 'fileType'}"
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
              .tooltip(
                v-on:mouseover="onTitleTooltipHover(`${file.path}-view-history-tooltip`, `${file.path}-title`)",
                v-on:mouseout="resetTitleTooltip(`${file.path}-view-history-tooltip`, `${file.path}-title`)"
              )
                font-awesome-icon.button(icon="history")
                span.tooltip-text(
                  v-bind:ref="`${file.path}-view-history-tooltip`"
                ) {{getLinkMessage(getHistoryLink(file), 'Click to view the history view of file')}}
            a(
              v-if='!file.isBinary',
              v-bind:class="!isBrokenLink(getBlameLink(file)) ? '' : 'broken-link'",
              v-bind:href="getBlameLink(file)", target="_blank",
              title="click to view the blame view of file"
            )
              .tooltip(
                v-on:mouseover="onTitleTooltipHover(`${file.path}-view-blame-tooltip`, `${file.path}-title`)",
                v-on:mouseout="resetTitleTooltip(`${file.path}-view-blame-tooltip`, `${file.path}-title`)"
              )
                font-awesome-icon.button(icon="user-edit")
                span.tooltip-text(
                  v-bind:ref="`${file.path}-view-blame-tooltip`"
                ) {{getLinkMessage(getBlameLink(file), 'Click to view the blame view of file')}}
          .author-breakdown(v-if="info.isMergeGroup")
            .author-breakdown__legend(
              v-for="author in getAuthors(file)",
              v-bind:key="author"
            )
              font-awesome-icon(
                icon="circle",
                v-bind:style="{ 'color': authorColors[author] }"
              )
              span &nbsp; {{ author }} &nbsp;
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

<script lang="ts">
import { defineComponent } from 'vue';
import { mapState } from 'vuex';
import minimatch from 'minimatch';
import brokenLinkDisabler from '../mixin/brokenLinkMixin';
import tooltipPositioner from '../mixin/dynamicTooltipMixin';
import cSegmentCollection from '../components/c-segment-collection.vue';
import getNonRepeatingColor from '../utils/random-color-generator';
import { StoreState } from '../types/vuex.d';
import { FileResult, Line } from '../types/zod/authorship-type';
import { AuthorshipFile, AuthorshipFileSegment } from '../types/types';
import { FilesSortType, FilterType } from '../types/authorship';

const filesSortDict = {
  linesOfCode: (file: AuthorshipFile) => file.lineCount,
  path: (file: AuthorshipFile) => file.path,
  fileName: (file: AuthorshipFile) => file.path.split(/[/]+/).pop() || '',
  fileType: (file: AuthorshipFile) => file.fileType,
};

function authorshipInitialState() {
  return {
    isLoaded: false,
    selectedFiles: [] as AuthorshipFile[],
    filterType: FilterType.Checkboxes,
    selectedFileTypes: [] as string[],
    fileTypes: [] as string[],
    filesLinesObj: {} as { [key: string]: number },
    fileTypeBlankLinesObj: {} as { [key: string]: number },
    filesSortType: FilesSortType.LinesOfCode,
    toReverseSortFiles: true,
    isBinaryFilesChecked: false,
    isIgnoredFilesChecked: false,
    searchBarValue: '',
    authorDisplayName: '',
    authors: new Set<string>(),
    selectedColors: ['#1e90ff', '#f08080', '#00ff7f', '#ffd700', '#ba55d3', '#adff2f', '#808000', '#800000',
      '#ff8c00', '#c71585'],
  };
}

const repoCache: string[] = [];

export default defineComponent({
  name: 'c-authorship',
  components: {
    cSegmentCollection,
  },
  mixins: [brokenLinkDisabler, tooltipPositioner],
  emits: [
    'deactivate-tab',
  ],
  data() {
    return authorshipInitialState();
  },

  computed: {
    sortingFunction() {
      return (a: AuthorshipFile, b: AuthorshipFile) => (this.toReverseSortFiles ? -1 : 1)
        * window.comparator(filesSortDict[this.filesSortType])(a, b);
    },

    isSelectAllChecked: {
      get(): boolean {
        return this.selectedFileTypes.length === this.fileTypes.length;
      },
      set(value: boolean) {
        if (value) {
          this.selectedFileTypes = this.fileTypes.slice();
        } else {
          this.selectedFileTypes = [];
        }

        this.indicateCheckBoxes();
      },
    },

    isBinaryChecked: {
      get(): boolean {
        return this.isBinaryFilesChecked;
      },
      set(value: boolean) {
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
      get(): boolean {
        return this.isIgnoredFilesChecked;
      },
      set(value: boolean) {
        if (value) {
          this.isIgnoredFilesChecked = true;
        } else {
          this.isIgnoredFilesChecked = false;
        }

        this.updateSelectedFiles();
        this.indicateCheckBoxes();
      },
    },

    activeFilesCount(): number {
      return this.selectedFiles.filter((file) => file.active).length;
    },

    totalLineCount(): number {
      return Object.values(this.fileTypeLinesObj).reduce((acc, val) => acc + val, 0);
    },

    totalBlankLineCount(): number {
      return Object.values(this.fileTypeBlankLinesObj).reduce((acc, val) => acc + val, 0);
    },

    fileTypeLinesObj(): { [key: string]: number } {
      const numLinesModified: { [key: string]: number } = {};
      Object.entries(this.filesLinesObj)
        .filter(([, value]) => value > 0)
        .forEach(([langType, value]) => {
          numLinesModified[langType] = value;
        });
      return numLinesModified;
    },

    binaryFilesCount(): number {
      return this.info.files.filter((file) => file.isBinary).length;
    },

    ignoredFilesCount(): number {
      return this.info.files.filter((file) => file.isIgnored).length;
    },

    ...mapState({
      fileTypeColors: (state: unknown) => (state as StoreState).fileTypeColors,
      info: (state: unknown) => (state as StoreState).tabAuthorshipInfo,
      authorColors: (state: unknown) => (state as StoreState).tabAuthorColors,
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
    retrieveHashes(): void {
      const hash = window.hashParams;

      switch (hash.authorshipSortBy) {
      case FilesSortType.Path:
      case FilesSortType.FileName:
      case FilesSortType.FileType:
        this.filesSortType = hash.authorshipSortBy;
        break;
      default:
        // Invalid value, use the default value of 'linesOfCode'
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

    resetSelectedFileTypes(): void {
      this.selectedFileTypes = this.info.checkedFileTypes
        ? this.info.checkedFileTypes.filter((fileType) => this.fileTypes.includes(fileType))
        : [];
    },

    setInfoHash(): void {
      const { addHash } = window;
      // We only set these hashes as they are propagated from summary_charts
      addHash('tabAuthor', this.info.author);
      addHash('tabRepo', this.info.repo);
      addHash('authorshipIsMergeGroup', this.info.isMergeGroup);
      this.updateFileTypeHash();
    },

    removeAuthorshipHashes(): void {
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

    async initiate(): Promise<void> {
      const repo = window.REPOS[this.info.repo];

      if (!repo || !this.info.author) {
        this.$store.commit('updateTabState', false);
        return;
      }
      if (repoCache.length === 2) {
        const toRemove = repoCache.shift();
        if (toRemove && toRemove !== this.info.repo) {
          delete window.REPOS[toRemove].files;
        }
      }
      repoCache.push(this.info.repo);

      let { files } = repo;
      if (!files) {
        files = await window.api.loadAuthorship(this.info.repo);
      }

      const author = repo.users?.find((user) => user.name === this.info.author);
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

    getAuthors(file: AuthorshipFile): (string | null)[] {
      return Array.from(new Set(file.segments?.map((segment) => segment.knownAuthor)
        .filter(Boolean))).sort().slice(0, 50);
    },

    toggleAllFileActiveProperty(isActive: boolean): void {
      this.$store.commit('setAllAuthorshipFileActiveProperty', { isActive, files: this.selectedFiles });
    },

    toggleFileActiveProperty(file: AuthorshipFile): void {
      this.scrollFileIntoView(file);
      this.$store.commit('toggleAuthorshipFileActiveProperty', file);
    },

    scrollFileIntoView(file: AuthorshipFile): void {
      const fileElement = (this.$refs[file.path] as HTMLElement[])[0];
      if (this.isElementAboveViewport(fileElement)) {
        fileElement.scrollIntoView(true);
      }
    },

    onTitleTooltipHover(tooltipTextElement: string, titleTextElement: string): void {
      this.onTooltipHover(tooltipTextElement);
      const titleElement = (this.$refs[titleTextElement] as HTMLElement[])[0];
      titleElement.classList.add('max-zIndex');
    },

    resetTitleTooltip(tooltipTextElement: string, titleTextElement: string): void {
      this.resetTooltip(tooltipTextElement);
      const titleElement = (this.$refs[titleTextElement] as HTMLElement[])[0];
      titleElement.classList.remove('max-zIndex');
    },

    isUnknownAuthor(name: string): boolean {
      return name === '-';
    },

    splitSegments(lines: Line[]): { segments: AuthorshipFileSegment[]; blankLineCount: number; } {
      // split into segments separated by knownAuthor
      let lastState: string | null;
      let lastId = -1;
      const segments: AuthorshipFileSegment[] = [];
      let blankLineCount = 0;

      lines.forEach((line, lineCount) => {
        const isAuthorMatched = this.info.isMergeGroup
            ? !this.isUnknownAuthor(line.author.gitId)
            : line.author.gitId === this.info.author;
        const knownAuthor = (line.author && isAuthorMatched) ? line.author.gitId : null;

        if (knownAuthor !== lastState || lastId === -1) {
          segments.push({
            knownAuthor,
            lineNumbers: [],
            lines: [],
          });

          lastId += 1;
          lastState = knownAuthor;
        }

        const content = line.content || ' ';
        segments[lastId].lines.push(content);

        segments[lastId].lineNumbers.push(lineCount + 1);

        if (line.content === '' && knownAuthor) {
          blankLineCount += 1;
        }

        if (knownAuthor) {
          this.authors.add(knownAuthor);
        }
      });

      return {
        segments,
        blankLineCount,
      };
    },

    assignAuthorColors(): void {
      let authorColorIndex = 0;
      const authorColors: { [key: string]: string } = {};
      if (this.info.isMergeGroup) {
        this.authors.forEach((author) => {
          if (authorColorIndex < this.selectedColors.length) {
            authorColors[author] = this.selectedColors[authorColorIndex];
            authorColorIndex += 1;
          } else {
            authorColors[author] = getNonRepeatingColor(Object.values(authorColors));
          }
        });
      }
      this.$store.commit('updateAuthorColors', authorColors);
    },

    processFiles(files: FileResult[]): void {
      const SINGLE_FILE_LINE_COUNT_THRESHOLD = 2000;
      const SINGLE_FILE_CHAR_COUNT_THRESHOLD = 1000000;
      const res: AuthorshipFile[] = [];
      const fileTypeBlanksInfoObj: { [key: string]: number } = {};

      files.filter((file) => this.isValidFile(file)).forEach((file) => {
        const contributionMap = file.authorContributionMap;

        const lineCnt = this.info.isMergeGroup
            ? this.getContributionFromAllAuthors(contributionMap)
            : contributionMap[this.info.author];

        const out: AuthorshipFile = {
          path: file.path,
          lineCount: lineCnt,
          active: lineCnt <= SINGLE_FILE_LINE_COUNT_THRESHOLD && !file.isBinary,
          wasCodeLoaded: lineCnt <= SINGLE_FILE_LINE_COUNT_THRESHOLD,
          fileType: file.fileType,
          isIgnored: !!file.isIgnored,
          isBinary: !!file.isBinary,
        };

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
        if (!file.isIgnored && !file.isBinary && file.active && file.charCount !== undefined) {
          file.active = file.charCount <= SINGLE_FILE_CHAR_COUNT_THRESHOLD;
          file.wasCodeLoaded = file.active;
        }
      });

      Object.keys(this.filesLinesObj).forEach((file) => {
        if (this.filesLinesObj[file] !== 0) {
          this.fileTypes.push(file);
        }
      });

      this.assignAuthorColors();
      this.fileTypeBlankLinesObj = fileTypeBlanksInfoObj;
      this.$store.commit('updateTabAuthorshipFiles', res);
      this.updateSelectedFiles(true);
    },

    isValidFile(file: FileResult): boolean {
      return this.info.isMergeGroup
          ? Object.entries(file.authorContributionMap)
            .some((authorCount) => !this.isUnknownAuthor(authorCount[0]))
          : this.info.author in file.authorContributionMap;
    },

    getContributionFromAllAuthors(contributionMap: Record<string, number>): number {
      return Object.entries(contributionMap).reduce((acc, [author, cnt]) => (
        (!this.isUnknownAuthor(author) ? acc + cnt : acc)
      ), 0);
    },

    addBlankLineCount(fileType: string, lineCount: number, filesInfoObj: { [key: string]: number }): void {
      if (!filesInfoObj[fileType]) {
        filesInfoObj[fileType] = 0;
      }

      filesInfoObj[fileType] += lineCount;
    },

    updateSearchBarValue(): void {
      this.searchBarValue = (this.$refs.searchBar as HTMLInputElement).value;

      window.addHash('authorshipFilesGlob', this.searchBarValue);
      window.removeHash('authorshipFileTypes');
      window.removeHash('authorshipIsBinaryFileTypeChecked');
      window.removeHash('authorshipIsIgnoredFilesChecked');
      window.encodeHash();
    },

    updateFileTypeHash(): void {
      const fileTypeHash = this.selectedFileTypes.length > 0
          ? this.selectedFileTypes.reduce((a, b) => `${a}~${b}`)
          : '';

      window.addHash('authorshipFileTypes', fileTypeHash);
      window.addHash('authorshipIsBinaryFileTypeChecked', this.isBinaryFilesChecked);
      window.addHash('authorshipIsIgnoredFilesChecked', this.isIgnoredFilesChecked);
      window.removeHash('authorshipFilesGlob');
      window.encodeHash();
    },

    async updateSelectedFiles(setIsLoaded = false): Promise<void> {
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

    indicateSearchBar(): void {
      this.selectedFileTypes = this.fileTypes.slice();
      this.isBinaryFilesChecked = true;
      this.isIgnoredFilesChecked = true;
      this.filterType = FilterType.Search;
    },

    indicateCheckBoxes(): void {
      this.searchBarValue = '';
      this.filterType = FilterType.Checkboxes;
      this.updateFileTypeHash();
    },

    getHistoryLink(file: AuthorshipFile): string | undefined {
      const repo = window.REPOS[this.info.repo];
      return window.getHistoryLink(this.info.repo, repo.branch, file.path);
    },

    getBlameLink(file: AuthorshipFile): string | undefined {
      const repo = window.REPOS[this.info.repo];
      return window.getBlameLink(this.info.repo, repo.branch, file.path);
    },

    getFileTypeBlankLineInfo(fileType: string): string {
      return `${fileType}: Blank: ${
        this.fileTypeBlankLinesObj[fileType]}, Non-Blank: ${
        this.filesLinesObj[fileType] - this.fileTypeBlankLinesObj[fileType]}`;
    },

    getTotalFileBlankLineInfo(): string {
      return `Total: Blank: ${this.totalBlankLineCount}, Non-Blank: ${
        this.totalLineCount - this.totalBlankLineCount}`;
    },

    getFirstPartOfPath(file: AuthorshipFile): string {
      const fileSplitIndex = file.path.lastIndexOf('/');
      const fileNameOnly = file.path.slice(fileSplitIndex + 1);

      if (this.filesSortType === FilesSortType.FileName) {
        return `${fileNameOnly}`;
      }
      return file.path;
    },

    getSecondPartOfPath(file: AuthorshipFile): string {
      const fileSplitIndex = file.path.lastIndexOf('/');
      const filePathOnly = file.path.slice(0, fileSplitIndex + 1);

      if (!filePathOnly) {
        return '/';
      }
      return filePathOnly;
    },

    getFirstPartOfLabel(file: AuthorshipFile): string {
      if (this.filesSortType === FilesSortType.LinesOfCode) {
        return `${file.lineCount} (${file.lineCount - (file.blankLineCount ?? 0)})`;
      }
      return `${file.fileType}`;
    },

    getSecondPartOfLabel(file: AuthorshipFile): string {
      if (this.filesSortType === FilesSortType.LinesOfCode) {
        return `${file.fileType}`;
      }
      return `${file.lineCount} (${file.lineCount - (file.blankLineCount ?? 0)})`;
    },

    getFontColor(color: string) {
      return window.getFontColor(color);
    },
  },
});
</script>

<style lang="scss">
@import '../styles/_colors.scss';
@import '../styles/z-indices.scss';

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
        @include small-font;
        align-items: flex-end;
        display: flex;
        flex-wrap: wrap;

        #search {
          @include small-font;
          margin-top: 1.25rem;
          min-width: 130px;
          padding: .5rem 1.0rem .25rem 1.0rem;
          width: 70%;
        }

        #submit-button {
          @include small-font;
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
        @include mono-font;
        display: grid;
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
      @include medium-font;
      background-color: mui-color('github', 'title-background');
      border: 1px solid mui-color('github', 'border');
      border-radius: 4px 4px 0 0;
      display: flex;
      flex-wrap: wrap;
      margin-top: 1rem;
      padding: .3em .5em;
      position: unset;
      top: 0;
      white-space: pre-wrap;
      word-break: break-all;
      z-index: z-index('file-title');

      &.sticky {
        position: sticky;
      }

      &.max-zIndex {
        z-index: z-index('max-value');
      }

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
        @include small-font;
        order: -1;
      }

      .author-breakdown {
        overflow-y: hidden;

        &__legend {
          @include small-font;
          display: inline;
          float: left;
          padding-right: 8px;
        }
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
        // overwrite all hljs colors
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
