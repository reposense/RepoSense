<template lang="pug">
.markdown(v-html="markdownText", v-if="markdownText != ''")
</template>

<script lang="ts">
import { defineComponent } from 'vue';

export default defineComponent({
  props: {
    markdownText: {
      type: String,
      required: true,
    },
  },
});
</script>

<style lang="scss" scoped>
.markdown {
  overflow-x: auto;
  padding: 0 1.5rem;
}
</style>
