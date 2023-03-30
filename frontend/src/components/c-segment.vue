<template lang="pug">
.segment(
  v-bind:class="{ untouched: !segment.knownAuthor, active: isOpen }",
  v-bind:style="{ 'border-left': `0.25rem solid ${authorColors[segment.knownAuthor]}` }",
  v-bind:title="`Author: ${segment.knownAuthor || \"Unknown\"}`"
)
  .closer(v-if="canOpen",
    v-on:click="toggleCode", ref="topButton")
    font-awesome-icon.icon(
      v-show="!isOpen",
      icon="plus-circle",
      v-bind:title="'Click to open code'"
    )
    font-awesome-icon.icon(
      v-show="isOpen",
      icon="chevron-circle-down",
      v-bind:title="'Click to hide code'"
    )
  div(v-if="isOpen", v-hljs="path")
    .code(
      v-for="(line, index) in segment.lines", v-bind:key="index",
      v-bind:style="{ 'background-color': `${authorColors[segment.knownAuthor]}${transparencyValue}` }"
    )
      .line-number {{ `${segment.lineNumbers[index]}\n` }}
      .line-content {{ `${line}\n` }}
  .closer.bottom(v-if="canOpen", v-on:click="toggleCode")
    font-awesome-icon.icon(
      v-show="isOpen",
      icon="chevron-circle-up",
      v-bind:title="'Click to hide code'"
    )
</template>

<script>
import { mapState } from 'vuex';
import Segment from '../utils/segment';

export default {
  name: 'c-segment',
  props: {
    segment: {
      type: Segment,
      required: true,
    },
    path: {
      type: String,
      required: true,
    },
  },
  data() {
    return {
      isOpen: this.segment.knownAuthor || this.segment.lines.length < 5,
      canOpen: !this.segment.knownAuthor && this.segment.lines.length > 4,
      transparencyValue: '30',
    };
  },
  computed: {
    ...mapState({
      authorColors: ['tabAuthorColors'],
    }),
  },
  methods: {
    toggleCode() {
      this.isOpen = !this.isOpen;
    },
  },
};
</script>

<style lang="css">
@import '../styles/hightlight-js-style.css';
</style>
