<template lang="pug">
.summary-chart__contrib--bar(
  v-for="bar in bars"
  v-bind:title="bar.tooltipText",
  v-bind:style="{ width: `${bar.width}%`,\
    'background-color': bar.color }"
)
</template>

<script lang="ts">
import { PropType, defineComponent } from 'vue';
import { Bar } from '../types/types';

export default defineComponent({
  props: {
    bars: {
      type: Array as PropType<Bar[]>,
      required: true,
    },
  },
});
</script>
