<template lang="pug">
#summary
  .summary-chart__contribution
    template(v-if="showDiffstat")
      .summary-chart__contrib(
        v-for="(barWidths, barColor) in widthMappings"
        )
        .summary-chart__contrib--bar(
          v-for="width in barWidths",
          v-bind:style="{ width: `${width}%`,\
          'background-color': barColor }"
          )
    template(v-else-if="filterBreakdown")
      .summary-chart__contrib(
        v-for="(barWidths, fileType) in widthMappings"
        )
        .summary-chart__contrib--bar(
          v-for="width in barWidths",
          v-bind:style="{ width: `${width}%`,\
          'background-color': fileTypeColors[fileType] }",
          v-bind:title="`${fileType}: ${fileTypeLinesChanged} lines, \
          total: ${totalLinesChanged} lines (contribution from ${minDate} to \
          ${maxDate})`"
          )
    template(v-else)
      .summary-chart__contrib(
        v-bind:title="`Total contribution from ${minDate} to ${maxDate}: \
        ${totalLinesChanged} lines`"
        )
        .summary-chart__contrib--bar(
          v-for="width in widths",
          v-bind:style="{ width: `${width}%` }"
          )
</template>

<script lang="ts">
import { PropType, defineComponent } from 'vue';

export default defineComponent({
  props: {
    showDiffstat: {
      type: Boolean,
      default: false,
    },
    filterBreakdown: {
      type: Boolean,
      default: false,
    },
    widthMappings: {
      type: Object as PropType<{ [key: string]: number[] }>,
      default: () => {},
    },
    widths: {
      type: Array as PropType<number[]>,
      default: () => [],
    },
    fileTypeColors: {
      type: Object as PropType<{ [key: string]: string }>,
      default: () => {},
    },
    fileTypeLinesChanged: {
      type: Number,
      default: 0,
    },
    totalLinesChanged: {
      type: Number,
      default: 0,
    },
    minDate: {
      type: String,
      default: 'minDate',
    },
    maxDate: {
      type: String,
      default: 'maxDate',
    },
  },
});
</script>
