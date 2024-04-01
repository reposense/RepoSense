<template lang="pug">
.markdown(v-html="markdownProcessed", v-if="markdownText != ''")
</template>

<script lang="ts">
import MarkdownIt from 'markdown-it';
import { defineComponent } from 'vue';

export default defineComponent({
  props: {
    markdownText: {
      type: String,
      required: true,
    },
  },
  computed: {
    markdownProcessed() {
      const md = new MarkdownIt({ html: true });
      return md.render(this.markdownText);
    },
  },
});
</script>

<style lang="scss" scoped>
.markdown {
  overflow-x: auto;
}
</style>
