<template lang="pug">
.title(v-html="markdownText", v-if="markdownText != ''")
</template>

<script lang="ts">
import MarkdownIt from 'markdown-it';
import { defineComponent } from 'vue';
export default defineComponent({
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
      const md = new MarkdownIt({ html: true });
      this.markdownText = md.render(text);
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
