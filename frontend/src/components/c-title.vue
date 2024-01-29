<template lang="pug">
div(v-html="markdownText")
</template>

<script lang="ts">
import MarkdownIt from 'markdown-it';
import MarkdownItAnchor from 'markdown-it-anchor';
import MarkdownItHighlightjs from 'markdown-it-highlightjs';
import MarkdownItTOC from 'markdown-it-toc-done-right';
import { defineComponent } from 'vue';
import titleText from '../markdown/title.md';

const emptyHtml = '<div />';

export default defineComponent({
  data() {
    return {
      markdownText: this.getMarkDownText(),
    };
  },
  methods: {
    getMarkDownText() {
      let output = emptyHtml;
      try {
        output = new MarkdownIt()
          .use(MarkdownItAnchor)
          .use(MarkdownItHighlightjs)
          .use(MarkdownItTOC)
          .render(titleText);
      } catch (e) {
        // We don't want to crash the app if the markdown is invalid,
        // so we just log the error and return an empty string.
        console.error(e);
      }
      return output;
    },
  },
});
</script>
