<template lang="pug">
div(v-html="markdownText")
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
    fetch('/RepoSense/reposense-report/title.md').then((response) => response.text(), (error) => {
      console.error(error);
      return '';
    }).then((text) => {
      const md = new MarkdownIt();
      this.markdownText = md.render(text);
    });
  },
});
</script>
