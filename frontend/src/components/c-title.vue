<template lang="pug">
.title(v-html="markdownText")
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
    try {
      fetch('title.md').then((response) => {
        if (!response.ok) { // file not found
          return '';
        }
        return response.text();
      }).then((text) => {
        const md = new MarkdownIt();
        this.markdownText = md.render(text);
      });
    } catch (error) {
      // do nothing
    }
  },
});
</script>

<style lang="scss" scoped>
.title {
  padding: 1rem 1.5rem;
}
</style>
