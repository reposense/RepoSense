<template lang="pug">
.segment-collection(v-observe-visibility="{ callback: visibilityChanged }")
  template(v-if="isRendered", v-for="segment in segments")
    c-segment(:segment="segment", :path="path")
</template>

<script lang='ts'>
import { defineComponent } from 'vue';
import cSegment from './c-segment.vue';
import { AuthorshipFileSegment } from '../types/types';

export default defineComponent({
  name: 'c-segment-collection',
  components: {
    cSegment,
  },
  props: {
    segments: {
      type: Array<AuthorshipFileSegment>,
      required: true,
    },
    path: {
      type: String,
      required: true,
    },
  },
  data(): {
    isRendered: boolean,
  } {
    return {
      isRendered: false,
    };
  },
  methods: {
    visibilityChanged(isVisible: boolean): void {
      if (isVisible) {
        this.isRendered = true;
      }
    },
  },
});
</script>
