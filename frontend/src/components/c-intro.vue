<template lang="pug">
.intro
  c-markdown-chunk(:markdown-text="markdownText")
</template>

<script lang="ts">
import { defineComponent } from "vue";
import CMarkdownChunk from "./c-markdown-chunk.vue";

const INTRO_FILE_PATH = "intro.md";

export default defineComponent({
  name: "c-intro",
  components: { CMarkdownChunk },
  data() {
    return {
      markdownText: "",
    };
  },
  async beforeMount() {
    try {
      this.markdownText = await this.fetchMarkdown(INTRO_FILE_PATH);
    } catch (error) {
      this.markdownText = (error as Error).toString();
    }
  },
  methods: {
    async fetchMarkdown(path: string): Promise<string> {
      const response = await fetch(path);
      if (!response.ok) {
        return "";
      }
      return response.text();
    },
  },
});
</script>

<style lang="scss" scoped>
.intro {
  overflow-x: auto;
  padding: 0 1.5rem;
  // This is needed because the parent summary-wrapper center aligns everything
  text-align: initial;
}
</style>
