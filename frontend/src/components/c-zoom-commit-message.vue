<template lang="pug">
//- use tabindex to enable focus property on div
.commit-message(
  tabindex="-1",
  :key="slice.hash",
  :class="{ 'message-body active': slice.messageBody !== '' }"
)
  span.code-merge-icon(v-if="slice.isMergeCommit")
    font-awesome-icon(icon="code-merge")
    span &nbsp;
  a.message-title(:href="getSliceLink(slice)",
    :class="!isBrokenLink(getSliceLink(slice)) ? '' : 'broken-link'", target="_blank")
    .within-border {{ slice.messageTitle.substr(0, 50) }}
    .not-within-border(v-if="slice.messageTitle.length > 50")
      |{{ slice.messageTitle.substr(50) }}
  span(data-cy="changes") &nbsp; (+{{ slice.insertions }} -{{ slice.deletions }} lines) &nbsp;
  .hash
    span {{ slice.hash.substr(0, 7) }}
  span.fileTypeLabel(
    v-if="containsAtLeastOneSelected(Object.keys(slice.fileTypesAndContributionMap))",
    v-for="fileType in\
      Object.keys(slice.fileTypesAndContributionMap)",
    vbind:key="fileType",
    :style="{\
      'background-color': fileTypeColors[fileType],\
      'color': getFontColor(fileTypeColors[fileType])\
      }"
  ) {{ fileType }}
  template(v-if="slice.tags")
    .tag(
      v-for="tag in slice.tags",
      vbind:key="tag",
      tabindex="-1", :class="[`${slice.hash}`, tag]"
    )
      font-awesome-icon(icon="tags")
      span &nbsp;{{ tag }}
  a(
    v-if="slice.messageBody !== ''",
    @click="toggleSelectedCommitMessageBody(slice)"
  )
    .tooltip(
      @mouseover="onTooltipHover(`${slice.hash}-show-hide-message-body`)",
      @mouseout="resetTooltip(`${slice.hash}-show-hide-message-body`)"
    )
      font-awesome-icon.commit-message--button(icon="ellipsis-h")
      span.tooltip-text(
        :ref="`${slice.hash}-show-hide-message-body`"
        ) Click to show/hide the commit message body
  .body(v-if="slice.messageBody !== ''", v-show="slice.isOpen")
    pre {{ slice.messageBody }}
      .dashed-border
  template(
    v-if="showDiffstat"
  )
    c-stacked-bar-chart(
      :bars="getContributionBars(slice)"
    )
</template>

<script lang="ts">
import { defineComponent, PropType } from 'vue';
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome';
import brokenLinkDisabler from '../mixin/brokenLinkMixin';
import tooltipPositioner from '../mixin/dynamicTooltipMixin';
import cRamp from './c-ramp.vue';
import cStackedBarChart from './c-stacked-bar-chart.vue';
import {
  Bar,
  CommitResult,
  User,
} from '../types/types';

export default defineComponent({
  name: 'c-zoom-commit-message',
  components: {
    FontAwesomeIcon,
    cRamp,
    cStackedBarChart,
  },
  mixins: [brokenLinkDisabler, tooltipPositioner],
  props: {
    slice: {
      type: Object as PropType<CommitResult>,
      required: true,
    },
    info: {
      type: Object as PropType<{ zUser: User | undefined, zAvgContributionSize: number, zIsMerged: Boolean }>,
      required: true,
    },
    showDiffstat: {
      type: Boolean,
      required: true,
    },
    selectedFileTypes: {
      type: Array<String>,
      required: true,
    },
    fileTypeColors: {
      type: Object as PropType<{ [key: string]: string }>,
      required: true,
    },
  },
  methods: {
    getContributionBars(slice: CommitResult): Array<Bar> {
      let currentBarWidth = 0;
      const fullBarWidth = 100;

      let avgContributionSize = this.info.zAvgContributionSize;
      if (avgContributionSize === undefined || avgContributionSize > 1000) {
        avgContributionSize = 1000;
      }

      const contributionPerFullBar = avgContributionSize;

      const diffstatMappings: { [key: string]: number } = { limegreen: slice.insertions, red: slice.deletions };
      const allContributionBars: Array<Bar> = [];

      if (contributionPerFullBar === 0) {
        return allContributionBars;
      }

      Object.keys(diffstatMappings)
        .forEach((color) => {
          const contribution = diffstatMappings[color];
          let barWidth = (contribution / contributionPerFullBar) * fullBarWidth;
          const contributionBars = [];

          // if contribution bar for file type is able to fit on the current line
          if (currentBarWidth + barWidth < fullBarWidth) {
            contributionBars.push(barWidth);
            currentBarWidth += barWidth;
          } else {
            // take up all the space left on the current line
            contributionBars.push(fullBarWidth - currentBarWidth);
            barWidth -= fullBarWidth - currentBarWidth;
            // additional bar width will start on a new line
            const numOfFullBars = Math.floor(barWidth / fullBarWidth);
            for (let i = 0; i < numOfFullBars; i += 1) {
              contributionBars.push(fullBarWidth);
            }
            const remainingBarWidth = barWidth % fullBarWidth;
            if (remainingBarWidth > 0) {
              contributionBars.push(remainingBarWidth);
            }
            currentBarWidth = remainingBarWidth;
          }

          contributionBars.forEach((width) => {
            const bar = {
              color,
              width,
            };
            allContributionBars.push(bar);
          });
        });

      return allContributionBars;
    },
    getSliceLink(slice: CommitResult): string | undefined {
      if (this.info.zIsMerged) {
        return window.getCommitLink(slice.repoId, slice.hash);
      }
      return window.getCommitLink(this.info.zUser!.repoId, slice.hash);
    },
    toggleSelectedCommitMessageBody(slice: CommitResult): void {
      this.$store.commit('toggleZoomCommitMessageBody', slice);
    },
    containsAtLeastOneSelected(fileTypes: Array<string>): boolean {
      for (let i = 0; i < fileTypes.length; i += 1) {
        if (this.selectedFileTypes.includes(fileTypes[i])) {
          return true;
        }
      }
      return false;
    },
    getFontColor(color: string): string {
      return window.getFontColor(color);
    },
  },
});
</script>

<style lang="scss" scoped>
@import '../styles/_colors.scss';

.commit-message {
  border: 1px solid transparent;
  padding: 5px;

  &:focus,
  &:focus-within {
    border: 1px solid mui-color('blue', '500');
  }

  &.active {
    .body {
      background-color: mui-color('white');
      border: 1px solid mui-color('grey', '700');
      display: grid;
      margin: .25rem 0 .25rem 0;
      overflow-x: auto;
      padding: .4rem;
      resize: none;

      pre {
        @include mono-font;
        position: relative;

        .dashed-border {
          border-right: 1px dashed mui-color('grey', '500'); // 72nd character line
          height: 100%;
          pointer-events: none;
          position: absolute;
          top: 0;
          width: 72ch;
        }
      }
    }
  }

  .code-merge-icon {
    color: mui-color('grey');

    .fa-code-merge {
      width: .65rem;
    }
  }

  .body {
    display: none;
  }

  .tag {
    cursor: pointer;

    &:focus {
      border: 1px solid mui-color('blue', '500');
      outline: none;
    }
  }

  &--button {
    color: mui-color('grey');
    padding-left: .5rem;

    &:hover {
      cursor: pointer;
    }
  }

  pre {
    margin: 0;
  }

  span.loc {
    color: mui-color('grey');
  }

  .message-title {
    @include mono-font;
    display: inline;

    .within-border {
      display: inline;
    }

    .not-within-border {
      border-left: 1px dashed mui-color('grey', '500'); // 50th character line
      display: inline;
    }
  }
}
</style>
