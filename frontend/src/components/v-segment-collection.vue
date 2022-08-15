<template lang="pug">
.segment-collection(v-observe-visibility="{ callback: visibilityChanged }")
  template(v-if="isRendered", v-for="segment in segments")
    v-segment(v-bind:segment="segment", v-bind:path="path")
</template>

<script>
import vSegment from './v-segment.vue';

export default {
  name: 'v-segment-collection',
  components: {
    vSegment,
  },
  props: ['segments', 'path'],
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
