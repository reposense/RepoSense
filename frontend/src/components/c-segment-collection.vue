<template lang="pug">
.segment-collection(v-observe-visibility="{ callback: visibilityChanged }")
  template(v-if="isRendered", v-for="segment in segments")
    c-segment(v-bind:segment="segment", v-bind:path="path")
</template>

<script>
import cSegment from './c-segment.vue';

export default {
  name: 'c-segment-collection',
  components: {
    cSegment,
  },
  props: {
    segments: {
      type: Array,
      required: true,
    },
    path: {
      type: String,
      required: true,
    },
  },
  data() {
    return {
      isRendered: false,
    };
  },
  methods: {
    visibilityChanged(isVisible) {
      if (isVisible) {
        this.isRendered = true;
      }
    },
  },
};
</script>
