<template lang="pug">
.markdown(v-html="parsedHtml", v-if="markdownText != ''")
</template>

<script lang="ts">
import { defineComponent } from 'vue';
import MarkdownIt from 'markdown-it';

export default defineComponent({
  props: {
    markdownText: {
      type: String,
      required: true,
    },
  },
  computed: {
    parsedHtml() {
      const md = new MarkdownIt({ html: true });
      return md.render(this.markdownText);
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
