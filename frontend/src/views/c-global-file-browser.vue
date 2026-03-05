<template lang="pug">
#global-file-browser
  .header
    h3 Global File Browser
    .search-container
      input.search-input(
        type="search",
        v-model="searchPattern",
        placeholder="Filter by glob pattern (e.g., *.vue, src/**/*.ts)"
      )
      .filter-row
        .view-toggle
          button.toggle-btn(:class="{ active: viewMode === 'path' }",
            @click="viewMode = 'path'; collapseFiles()") Sort By Path
          button.toggle-btn(:class="{ active: viewMode === 'repo' }",
            @click="viewMode = 'repo'; collapseFiles()") Group By Repo
        .file-count {{ filteredFiles.length }} file(s)

  template(v-if="filteredFiles.length > 0")
    .file-list(v-if="viewMode === 'path'")
      .file-item(
        v-for="(file, index) in filteredFiles",
        :key="`${file.repoName}-${file.path}`",
        :class="{ 'is-expanded': file.active }"
      )
        .file-header(@click="toggleFile(file)")
          span.caret(v-if="!file.isBinary && !file.isIgnored")
            font-awesome-icon(
              :icon="file.active ? 'caret-down' : 'caret-right'",
              fixed-width
            )
          span.file-path(:style="{ paddingLeft: '0px' }") {{ file.path }}
          span.file-info
            span.repo-badge {{ file.repoName }}
            span.line-count(v-if="!file.isBinary && !file.isIgnored") {{ file.lineCount }} lines
            span.binary-badge(v-if="file.isBinary") binary
            span.ignored-badge(v-if="file.isIgnored") ignored

        .file-content(v-if="file.active")
          .loading(v-if="!file.segments") Loading file content...
          c-segment-collection(
            v-else,
            :segments="file.segments",
            :path="file.path"
          )

    .file-list(v-if="viewMode === 'repo'")
      .repo-group(v-for="group in groupedByRepo", :key="group.repoName")
        .repo-group-header(@click="toggleAllFiles(group.repoName)")
          span.repo-group-name {{ group.repoName }}
          span.repo-group-count {{ group.files.length }} file(s)
          .repo-group-toggle-badge {{ getRepoDisplayText(group.repoName) }}
        .file-item(
          v-for="file in getVisibleFiles(group)",
          :key="`${file.repoName}-${file.path}`",
          :class="{ 'is-expanded': file.active }"
        )
          .file-header(@click="toggleFile(file)")
            span.caret(v-if="!file.isBinary && !file.isIgnored")
              font-awesome-icon(
                :icon="file.active ? 'caret-down' : 'caret-right'",
                fixed-width
              )
            span.file-path(:style="{ paddingLeft: '0px' }") {{ file.path }}
            span.file-info
              span.line-count(v-if="!file.isBinary && !file.isIgnored") {{ file.lineCount }} lines
              span.binary-badge(v-if="file.isBinary") binary
              span.ignored-badge(v-if="file.isIgnored") ignored

          .file-content(v-if="file.active")
            .loading(v-if="!file.segments") Loading file content...
            c-segment-collection(
              v-else,
              :segments="file.segments",
              :path="file.path"
            )
        .more-files(
          v-if="!isRepoExpanded(group.repoName) && group.files.length > 3",
          @click="toggleRepoGroup(group.repoName)"
        ) and {{ group.files.length - 3 }} more file(s)...

  .empty-state(v-else)
    p No files match the current filter
</template>

<script lang='ts'>
import { defineComponent, PropType } from 'vue';
import { minimatch } from 'minimatch';
import { GlobalFileEntry, AuthorshipFileSegment } from '../types/types';
import cSegmentCollection from '../components/c-segment-collection.vue';
import getNonRepeatingColor from '../utils/random-color-generator';

const selectedColors = [
  '#1e90ff', '#f08080', '#00ff7f', '#ffd700', '#ba55d3',
  '#adff2f', '#808000', '#badfdb', '#f875aa', '#c71585',
];

export default defineComponent({
  name: 'c-global-file-browser',
  components: {
    cSegmentCollection,
  },
  props: {
    files: {
      type: Array as PropType<Array<GlobalFileEntry>>,
      required: true,
    },
  },
  data(): {
    searchPattern: string;
    viewMode: 'path' | 'repo';
    expandedRepos: Array<string>;
  } {
    return {
      searchPattern: '',
      viewMode: 'path',
      expandedRepos: [],
    };
  },
  computed: {
    filteredFiles(): Array<GlobalFileEntry> {
      const pattern = this.searchPattern.trim() || '*';
      return this.files
        .filter((file: GlobalFileEntry) =>
          minimatch(file.path, pattern, { matchBase: true, dot: true })
        )
        .sort((a: GlobalFileEntry, b: GlobalFileEntry) => a.path.localeCompare(b.path));
    },
    groupedByRepo(): Array<{ repoName: string, files: Array<GlobalFileEntry> }> {
      const repoMap: Record<string, Array<GlobalFileEntry>> = {};
      this.filteredFiles.forEach((file) => {
        if (!repoMap[file.repoName]) {
          repoMap[file.repoName] = [];
        }
        repoMap[file.repoName].push(file);
      });
      return Object.keys(repoMap)
        .sort((a, b) => a.localeCompare(b))
        .map((repoName) => ({ repoName, files: repoMap[repoName] }));
    },
  },
  methods: {
    collapseFiles(): void {
      this.files.forEach((file) => {
        file.active = false;
      });
      this.expandedRepos = [];
    },

    toggleFile(file: GlobalFileEntry): void {
      if (!file.isBinary && !file.isIgnored) {
        file.active = !file.active;

        if (file.active && !file.segments) {
          this.loadFileSegments(file);
        }
      }
    },

    getRepoDisplayText(repoName: string): string {
      return this.isRepoExpanded(repoName)
          ? 'Click to collapse'
          : 'Click to expand';
    },

    loadFileSegments(file: GlobalFileEntry): void {
      // Load lines if not already available
      if (!file.lines) {
        const repoFiles = window.REPOS[file.repoName].files;
        if (!repoFiles) return;

        const originalFile = repoFiles.find((f) => f.path === file.path);
        if (!originalFile || !originalFile.lines) return;

        file.lines = originalFile.lines;
      }

      file.segments = this.splitSegments(file.lines!);
      this.assignAuthorColors(file);
    },

    toggleAllFiles(repoName: string): void {
      const shouldOpen = !this.isRepoExpanded(repoName);
      this.toggleRepoGroup(repoName);

      const filesInRepo = this.filteredFiles.filter(
        (file) => file.repoName === repoName,
      );

      filesInRepo.forEach((file) => {
        file.active = shouldOpen;

        if (shouldOpen && !file.segments) {
          this.loadFileSegments(file);
        }
      });
    },

    splitSegments(lines: Array<{ content: string; author: { gitId: string }; isFullCredit: boolean }>):
    Array<AuthorshipFileSegment> {
      const segments: Array<AuthorshipFileSegment> = [];
      let lastAuthor: string | null = null;
      let lastIsFullCredit = true;

      lines.forEach((line, lineCount) => {
        const knownAuthor = line.author.gitId;
        const { isFullCredit } = line;

        if (segments.length === 0 || lastAuthor !== knownAuthor
            || lastIsFullCredit !== isFullCredit) {
          segments.push({
            knownAuthor,
            isFullCredit,
            lineNumbers: [],
            lines: [],
          });
          lastAuthor = knownAuthor;
          lastIsFullCredit = isFullCredit;
        }

        const content = line.content || ' ';
        segments[segments.length - 1].lines.push(content);
        segments[segments.length - 1].lineNumbers.push(lineCount + 1);
      });

      return segments;
    },

    assignAuthorColors(file: GlobalFileEntry): void {
      const existingColors: { [key: string]: string } = {
        ...this.$store.state.tabAuthorColors,
      };

      let colorIndex = Object.keys(existingColors).length;
      file.authors.forEach((author) => {
        if (!existingColors[author]) {
          if (colorIndex < selectedColors.length) {
            existingColors[author] = selectedColors[colorIndex];
          } else {
            existingColors[author] = getNonRepeatingColor(Object.values(existingColors));
          }
          colorIndex += 1;
        }
      });

      this.$store.commit('updateAuthorColors', existingColors);
    },

    toggleRepoGroup(repoName: string): void {
      const index = this.expandedRepos.indexOf(repoName);
      if (index >= 0) {
        this.expandedRepos.splice(index, 1);
      } else {
        this.expandedRepos.push(repoName);
      }
    },

    isRepoExpanded(repoName: string): boolean {
      return this.expandedRepos.includes(repoName);
    },

    getVisibleFiles(group: { repoName: string, files: Array<GlobalFileEntry> }): Array<GlobalFileEntry> {
      if (this.isRepoExpanded(group.repoName)) {
        return group.files;
      }
      return group.files.slice(0, 3);
    },
  },
});
</script>

<style scoped lang="scss">
@import '../styles/global-file-browser.scss';

.file-content {
  :deep(.segment .hljs.hljs) {
    &::-webkit-scrollbar {
      display: none;
    } /* Hide scrollbar for Chrome, Safari and Opera */
    overflow: hidden;
    -ms-overflow-style: none;  /* IE and Edge */
    padding: 0;
    scrollbar-width: none;  /* Firefox */
    white-space: normal;
  }
}
</style>
