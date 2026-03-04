<template lang="pug">
#global-file-browser
  .header
    h3 Global File Browser
    .search-container
      input.search-input(
        type="search",
        v-model="searchPattern",
        placeholder="Filter by glob pattern (e.g., *.vue, src/**/*.ts)",
        @input="onSearchInput"
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
          .repo-group-is-show-top-badge {{ getRepoDisplayText(group.repoName) }}
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
/* eslint-disable import/no-relative-packages */
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
    onSearchInput(): void {
      // Debouncing could be added here if needed
    },

    collapseFiles(): void {
      this.files.forEach((file) => {
        file.active = false;
      });
      this.expandedRepos = [];
    },

    getIndentLevel(path: string): number {
      const depth = (path.match(/\//g) || []).length;
      return depth * 16; // 16px per level
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
#global-file-browser {
  background: #fff;
  border-right: 1px solid #d4d4d4;
  display: flex;
  flex-direction: column;
  height: 100%;
}

.header {
  border-bottom: 1px solid #d4d4d4;
  padding: 1rem;

  h3 {
    font-size: 1.2rem;
    margin: 0 0 .5rem 0;
  }

  .search-container {
    display: flex;
    flex-direction: column;
    gap: .5rem;
  }

  .search-input {
    border: 1px solid #d4d4d4;
    border-radius: 4px;
    font-size: .9rem;
    padding: .5rem;
    width: 100%;

    &:focus {
      border-color: #007bff;
      outline: none;
    }
  }

  .filter-row {
    align-items: center;
    display: flex;
    justify-content: space-between;
  }

  .file-count {
    color: #666;
    font-size: .85rem;
  }

  .view-toggle {
    display: flex;
    gap: .25rem;
  }

  .toggle-btn {
    background: #f0f0f0;
    border: 1px solid #d4d4d4;
    border-radius: 4px;
    cursor: pointer;
    font-size: .85rem;
    padding: .3rem .75rem;

    &:hover {
      background: #e0e0e0;
    }

    &.active {
      background: #007bff;
      border-color: #007bff;
      color: white;
    }
  }
}

.file-list {
  flex: 1;
  overflow-y: auto;
  padding: .5rem;
}

.file-item {
  border: 1px solid #e0e0e0;
  border-radius: 4px;
  margin-bottom: .5rem;

  &.is-expanded {
    border-color: #007bff;
  }
}

.file-header {
  align-items: center;
  background: #f8f8f8;
  cursor: pointer;
  display: flex;
  padding: .5rem;
  transition: background .2s;

  &:hover {
    background: #e8e8e8;
  }

  .caret {
    color: #666;
    margin-right: .5rem;
  }

  .file-path {
    flex: 1;
    font-family: monospace;
    font-size: .9rem;
    overflow: hidden;
    text-align: left;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .file-info {
    align-items: center;
    display: flex;
    font-size: .85rem;
    gap: .5rem;
  }

  .repo-badge {
    background: #007bff;
    border-radius: 3px;
    color: white;
    font-size: .75rem;
    padding: .2rem .5rem;
  }

  .line-count {
    color: #666;
  }

  .binary-badge,
  .ignored-badge {
    background: #ffc107;
    border-radius: 3px;
    font-size: .75rem;
    padding: .2rem .5rem;
  }
}

.file-content {
  background: #fff;
  border-top: 1px solid #e0e0e0;
  padding: 0;
  text-align: left;

  :deep(.segment .hljs.hljs) {
    -webkit-scrollbar {
      display: none;
    } /* Hide scrollbar for Chrome, Safari and Opera */
    overflow: hidden;
    -ms-overflow-style: none;  /* IE and Edge */
    padding: 0; /* Hide scrollbar for IE, Edge and Firefox */
    scrollbar-width: none;  /* Firefox */
    white-space: normal;
  }

  .loading {
    color: #666;
    font-style: italic;
    padding: 1rem;
    text-align: center;
  }
}

.repo-group {
  margin-bottom: .75rem;
}

.repo-group-is-show-top-badge {
  background: #28a745;
  border-radius: 3px;
  color: white;
  font-size: .7rem;
  margin-left: .5rem;
  padding: .1rem .4rem;
}

.repo-group-header {
  align-items: center;
  background: #e8eef4;
  border: 1px solid #d4d4d4;
  border-radius: 4px;
  cursor: pointer;
  display: flex;
  font-weight: bold;
  gap: .5rem;
  margin-bottom: .25rem;
  padding: .5rem .75rem;
  transition: background .2s;

  &:hover {
    background: #dce4ec;
  }

  .caret {
    color: #666;
  }

  .repo-group-name {
    flex: 1;
    font-size: .95rem;
  }

  .repo-group-count {
    color: #666;
    font-size: .85rem;
    font-weight: normal;
  }
}

.more-files {
  color: #007bff;
  cursor: pointer;
  font-size: .85rem;
  padding: .25rem .75rem .5rem;
  text-align: left;

  &:hover {
    text-decoration: underline;
  }
}

.empty-state {
  align-items: center;
  color: #999;
  display: flex;
  flex: 1;
  font-style: italic;
  justify-content: center;
}
</style>
