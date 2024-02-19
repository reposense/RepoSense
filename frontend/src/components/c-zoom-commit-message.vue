<template lang="pug">
//- use tabindex to enable focus property on div
.commit-message(
  tabindex="-1",
  v-bind:key="slice.hash",
  v-bind:class="{ 'message-body active': slice.messageBody !== '' }"
)
  span.code-merge-icon(v-if="slice.isMergeCommit")
    font-awesome-icon(icon="code-merge")
    span &nbsp;
  a.message-title(v-bind:href="getSliceLink(slice)",
    v-bind:class="!isBrokenLink(getSliceLink(slice)) ? '' : 'broken-link'", target="_blank")
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
    v-bind:style="{\
      'background-color': fileTypeColors[fileType],\
      'color': getFontColor(fileTypeColors[fileType])\
      }"
  ) {{ fileType }}
  template(v-if="slice.tags")
    .tag(
      v-for="tag in slice.tags",
      vbind:key="tag",
      tabindex="-1", v-bind:class="[`${slice.hash}`, tag]"
    )
      font-awesome-icon(icon="tags")
      span &nbsp;{{ tag }}
  a(
    v-if="slice.messageBody !== ''",
    v-on:click="toggleSelectedCommitMessageBody(slice)"
  )
    .tooltip(
      v-on:mouseover="onTooltipHover(`${slice.hash}-show-hide-message-body`)",
      v-on:mouseout="resetTooltip(`${slice.hash}-show-hide-message-body`)"
    )
      font-awesome-icon.commit-message--button(icon="ellipsis-h")
      span.tooltip-text(
        v-bind:ref="`${slice.hash}-show-hide-message-body`"
        ) Click to show/hide the commit message body
  .body(v-if="slice.messageBody !== ''", v-show="slice.isOpen")
    pre {{ slice.messageBody }}
      .dashed-border
  template(
    v-if="showDiffstat"
  )
    c-stacked-bar-chart(
      v-bind:bars="getContributionBars(slice)"
    )

</template>

<script lang="ts">
import { defineComponent } from 'vue';
import { mapState } from 'vuex';
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome';
import brokenLinkDisabler from '../mixin/brokenLinkMixin';
import tooltipPositioner from '../mixin/dynamicTooltipMixin';
import cRamp from './c-ramp.vue';
import cStackedBarChart from './c-stacked-bar-chart.vue';

import {
  Bar,
  CommitResult,
} from '../types/types';
import { StoreState } from '../types/vuex.d';

export default defineComponent({
  name: 'c-zoom-commit-message',
  components: {
    FontAwesomeIcon,
    cRamp,
    cStackedBarChart,
  },
  mixins: [brokenLinkDisabler, tooltipPositioner],
  props: {
    day: {
      type: Object,
      required: true,
    },
    showDiffstat: {
      type: Boolean,
      required: true,
    },
    selectedFileTypes: {
      type: Array,
      required: true,
    },
    fileTypeColors: {
      type: Object,
      required: true,
    },
  },
  computed: {
    ...mapState({
      fileTypeColors: (state: unknown) => (state as StoreState).fileTypeColors,
      info: (state: unknown) => (state as StoreState).tabZoomInfo,
    }),
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

    toggleSelectedCommitMessageBody(slice: CommitResult) {
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

    getFontColor(color: string) {
      return window.getFontColor(color);
    },
  },
});
</script>
