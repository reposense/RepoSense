<template lang="pug">
.stacked-bar-container
  .stacked-bar__contrib--bar(
    v-for="bar in bars",
    v-bind:title="bar.tooltipText",
    v-bind:style="{ width: `${bar.width}%`,\
      'background-color': bar.color }"
  )
</template>

<script lang="ts">
import { defineComponent } from 'vue';
import { Bar } from '../types/types';

export default defineComponent({
  props: {
    bars: {
      type: Array<Bar>,
      required: true,
    },
  },
});
</script>

<style scoped lang="scss">
@import '../styles/_colors.scss';

.stacked-bar-container {
  display: flex;
  flex-wrap: wrap;
}

.stacked-bar__contrib--bar {
  background-color: mui-color('blue');
  height: 4px;
  margin-top: 2px;
}

</style>
