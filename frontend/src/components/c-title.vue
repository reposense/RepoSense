<template lang="pug">
div(v-html="markdownText")
</template>

<script lang="ts">
import MarkdownIt from 'markdown-it';
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
          .render(titleText);
      } catch (e) {
        // We don't want to crash the app if the markdown is invalid,
        // so we just return an empty div
      }
      return output;
    },
  },
});
</script>
