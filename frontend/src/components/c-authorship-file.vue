<template lang="pug">
.file(:ref="file.path")
  .title(
    :class="{'sticky': file.active}",
    :ref="`${file.path}-title`"
    )
    span.caret(@click="toggleFileActiveProperty(file)")
      .tooltip(
        v-show="file.active",
        @mouseover="onTitleTooltipHover(`${file.path}-hide-file-tooltip`, `${file.path}-title`)",
        @mouseout="resetTitleTooltip(`${file.path}-hide-file-tooltip`, `${file.path}-title`)"
      )
        font-awesome-icon(icon="caret-down", fixed-width)
        span.tooltip-text(:ref="`${file.path}-hide-file-tooltip`") Click to hide file details
      .tooltip(
        v-show="!file.active",
        @mouseover="onTitleTooltipHover(`${file.path}-show-file-tooltip`, `${file.path}-title`)",
        @mouseout="resetTitleTooltip(`${file.path}-show-file-tooltip`, `${file.path}-title`)"
      )
        font-awesome-icon(icon="caret-right", fixed-width)
        span.tooltip-text(:ref="`${file.path}-show-file-tooltip`") Click to show file details
    span.index {{ index + 1 }}. &nbsp;
    span.path
      span(
        :class="{'selected-parameter':\
          filesSortType === 'path' || filesSortType === 'fileName'}"
      ) {{ getFirstPartOfPath(file) }}&nbsp;
      span.in(v-if="filesSortType === 'fileName'") in&nbsp;
      span(v-if="filesSortType === 'fileName'") {{ getSecondPartOfPath(file) }}&nbsp;
    span.fileTypeLabel(
      v-if="!file.isBinary && !file.isIgnored",
      :style="{\
        'background-color': fileTypeColors[file.fileType],\
        'color': getFontColor(fileTypeColors[file.fileType])\
        }",
      :class="{'selected-label':\
        filesSortType === 'linesOfCode' || filesSortType === 'fileType'}"
    )
      span(
        :class="{'selected-parameter':\
          filesSortType === 'linesOfCode' || filesSortType === 'fileType'}"
      ) {{ getFirstPartOfLabel(file) }}&nbsp;
      span {{ getSecondPartOfLabel(file) }}
    span.fileTypeLabel.binary(v-if='file.isBinary') binary &nbsp;
    span.ignored-tag.fileTypeLabel(
      v-if='file.isIgnored'
    ) ignored ({{ file.lineCount }}) &nbsp;
    span.icons
      a(
        :class="!isBrokenLink(getHistoryLink(file)) ? '' : 'broken-link'",
        :href="getHistoryLink(file)", target="_blank"
      )
        .tooltip(
          @mouseover="onTitleTooltipHover(`${file.path}-view-history-tooltip`, `${file.path}-title`)",
          @mouseout="resetTitleTooltip(`${file.path}-view-history-tooltip`, `${file.path}-title`)"
        )
          font-awesome-icon.button(icon="history")
          span.tooltip-text(
            :ref="`${file.path}-view-history-tooltip`"
          ) {{getLinkMessage(getHistoryLink(file), 'Click to view the history view of file')}}
      a(
        v-if='!file.isBinary',
        :class="!isBrokenLink(getBlameLink(file)) ? '' : 'broken-link'",
        :href="getBlameLink(file)", target="_blank",
        title="click to view the blame view of file"
      )
        .tooltip(
          @mouseover="onTitleTooltipHover(`${file.path}-view-blame-tooltip`, `${file.path}-title`)",
          @mouseout="resetTitleTooltip(`${file.path}-view-blame-tooltip`, `${file.path}-title`)"
        )
          font-awesome-icon.button(icon="user-edit")
          span.tooltip-text(
            :ref="`${file.path}-view-blame-tooltip`"
          ) {{getLinkMessage(getBlameLink(file), 'Click to view the blame view of file')}}
    .author-breakdown(v-if="info.isMergeGroup")
      .author-breakdown__legend(
        v-for="author in getAuthors(file)",
        :key="author"
      )
        font-awesome-icon(
          icon="circle",
          :style="{ 'color': authorColors[author] }"
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
    c-segment-collection(:segments="file.segments", :path="file.path")
</template>

<script lang="ts">
import { PropType, defineComponent } from 'vue';
import brokenLinkDisabler from '../mixin/brokenLinkMixin';
import tooltipPositioner from '../mixin/dynamicTooltipMixin';
import cSegmentCollection from './c-segment-collection.vue';
import { AuthorshipFile } from '../types/types';
import { FilesSortType } from '../types/authorship';

export default defineComponent({
  name: 'c-authorship-file',
  components: {
    cSegmentCollection,
  },
  mixins: [brokenLinkDisabler, tooltipPositioner],
  props: {
    file: {
      type: Object as PropType<AuthorshipFile>,
      required: true,
    },
    index: {
      type: Number,
      required: true,
    },
    filesSortType: {
      type: String,
      required: true,
    },
    info: {
      type: Object as PropType<{ repo: string, isMergeGroup: Boolean }>,
      required: true,
    },
    authorColors: {
      type: Object as PropType<{ [key: string]: string }>,
      required: true,
    },
    fileTypeColors: {
      type: Object as PropType<{ [key: string]: string }>,
      required: true,
    },
  },
  emits: ['toggle-file-active-property'],
  methods: {
    getHistoryLink(file: AuthorshipFile): string | undefined {
      const repo = window.REPOS[this.$props.info.repo];
      return window.getHistoryLink(this.$props.info.repo, repo.branch, file.path);
    },

    getBlameLink(file: AuthorshipFile): string | undefined {
      const repo = window.REPOS[this.$props.info.repo];
      return window.getBlameLink(this.$props.info.repo, repo.branch, file.path);
    },

    getAuthors(file: AuthorshipFile): Array<string | null> {
      return Array.from(new Set(file.segments?.map((segment) => segment.knownAuthor)
        .filter(Boolean))).sort().slice(0, 50);
    },

    onTitleTooltipHover(tooltipTextElement: string, titleTextElement: string): void {
      this.onTooltipHover(tooltipTextElement);
      const titleElement = this.getElementByRef(titleTextElement);
      titleElement.classList.add('max-zIndex');
    },

    resetTitleTooltip(tooltipTextElement: string, titleTextElement: string): void {
      this.resetTooltip(tooltipTextElement);
      const titleElement = this.getElementByRef(titleTextElement);
      titleElement.classList.remove('max-zIndex');
    },

    toggleFileActiveProperty(file: AuthorshipFile): void {
      this.scrollFileIntoView(file);
      this.$emit('toggle-file-active-property', file);
    },

    scrollFileIntoView(file: AuthorshipFile): void {
      const fileElement = this.getElementByRef(file.path);
      if (this.isElementAboveViewport(fileElement)) {
        fileElement.scrollIntoView(true);
      }
    },

    getFirstPartOfPath(file: AuthorshipFile): string {
      const fileSplitIndex = file.path.lastIndexOf('/');
      const fileNameOnly = file.path.slice(fileSplitIndex + 1);

      if (this.$props.filesSortType === FilesSortType.FileName) {
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
      if (this.$props.filesSortType === FilesSortType.LinesOfCode) {
        return `${file.lineCount} (${file.lineCount - (file.blankLineCount ?? 0)})`;
      }
      return `${file.fileType}`;
    },

    getSecondPartOfLabel(file: AuthorshipFile): string {
      if (this.$props.filesSortType === FilesSortType.LinesOfCode) {
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

.file-content {
  background-color: mui-color('github', 'title-background');
  border: solid mui-color('github', 'border');
  border-radius: 0 0 4px 4px;
  border-width: 0 1px 1px 1px;
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
}
</style>
