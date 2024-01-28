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

<script lang='ts'>
import { defineComponent, PropType } from 'vue';
import { mapState } from 'vuex';
import { StoreState } from '../types/vuex.d';
import { AuthorshipFileSegment } from '../types/types';

export default defineComponent({
  name: 'c-segment',
  props: {
    segment: {
      type: Object as PropType<AuthorshipFileSegment>,
      required: true,
    },
    path: {
      type: String,
      required: true,
    },
  },
  data() {
    return {
      isOpen: (this.segment.knownAuthor !== null) || this.segment.lines.length < 5 as boolean,
      canOpen: (this.segment.knownAuthor === null) && this.segment.lines.length > 4 as boolean,
      transparencyValue: '30' as string,
    };
  },
  computed: {
    ...mapState({
      authorColors: (state: unknown) => (state as StoreState).tabAuthorColors,
    }),
  },
  methods: {
    toggleCode() {
      this.isOpen = !this.isOpen;
    },
  },
});
</script>

<style lang="css">
@import '../styles/hightlight-js-style.css';
</style>
