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
      .file-count {{ filteredFiles.length }} file(s)

  .file-list(v-if="filteredFiles.length > 0")
    .file-item(
      v-for="(file, index) in filteredFiles",
      :key="`${file.repoName}-${file.path}`",
      :class="{ 'is-expanded': file.active }"
    )
      .file-header(@click="toggleFile(file)")
        span.caret
          font-awesome-icon(
            :icon="file.active ? 'caret-down' : 'caret-right'",
            fixed-width
          )
        span.file-path(:style="{ paddingLeft: getIndentLevel(file.path) + 'px' }") {{ file.path }}
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

  .empty-state(v-else)
    p No files match the current filter
</template>

<script lang='ts'>
/* eslint-disable import/no-relative-packages */
import { defineComponent, PropType } from 'vue';
import { minimatch } from 'minimatch';
import { GlobalFileEntry, AuthorshipFileSegment } from '../types/types';
import cSegmentCollection from './c-segment-collection.vue';

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
  } {
    return {
      searchPattern: '',
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
  },
  methods: {
    onSearchInput(): void {
      // Debouncing could be added here if needed
    },

    getIndentLevel(path: string): number {
      const depth = (path.match(/\//g) || []).length;
      return depth * 16; // 16px per level
    },

    toggleFile(file: GlobalFileEntry): void {
      file.active = !file.active;

      // Load segments if expanding and not yet loaded
      if (file.active && !file.segments) {
        this.loadFileSegments(file);
      }
    },

    loadFileSegments(file: GlobalFileEntry): void {
      // Find the original file data from window.REPOS
      const repoFiles = window.REPOS[file.repoName].files;
      if (!repoFiles) return;

      const originalFile = repoFiles.find((f) => f.path === file.path);
      if (!originalFile || !originalFile.lines) return;

      // Build segments from lines (group consecutive lines by author)
      const segments: Array<AuthorshipFileSegment> = [];
      let currentSegment: AuthorshipFileSegment | null = null;

      originalFile.lines.forEach((line) => {
        const author = line.author.gitId;
        const isFullCredit = line.isFullCredit;

        if (
          currentSegment &&
          currentSegment.knownAuthor === author &&
          currentSegment.isFullCredit === isFullCredit
        ) {
          // Continue current segment
          currentSegment.lineNumbers.push(line.lineNumber);
          currentSegment.lines.push(line.content);
        } else {
          // Start new segment
          if (currentSegment) {
            segments.push(currentSegment);
          }
          currentSegment = {
            knownAuthor: author,
            isFullCredit,
            lineNumbers: [line.lineNumber],
            lines: [line.content],
          };
        }
      });

      // Push the last segment
      if (currentSegment) {
        segments.push(currentSegment);
      }

      // Update the file with segments
      file.segments = segments;
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

  .file-count {
    color: #666;
    font-size: .85rem;
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
  padding: 1rem;

  .loading {
    color: #666;
    font-style: italic;
    text-align: center;
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
