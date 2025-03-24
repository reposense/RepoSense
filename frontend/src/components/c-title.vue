<template lang="pug">
.title
  c-markdown-chunk(:markdown-text="markdownText")
</template>

<script lang="ts">
import { defineComponent } from 'vue';
import CMarkdownChunk from "./c-markdown-chunk.vue";

export default defineComponent({
  components: {CMarkdownChunk},
  data() {
    return {
      markdownText: '',
    };
  },
  beforeMount() {
    fetch('title.md').then((response) => {
      if (!response.ok) { // file not found
        return '';
      }
      return response.text();
    }).then((text) => {
      this.markdownText = text;
    }).catch((error) => {
      this.markdownText = (error as Error).toString();
    });
  },
});
</script>

<style lang="scss" scoped>
.title {
  overflow-x: auto;
  padding: 0 1.5rem;
  // This is needed because the parent summary-wrapper center aligns everything
  text-align: initial;
}
</style>
