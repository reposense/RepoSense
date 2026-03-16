<template lang="pug">
  #go-back-button(v-show="showBackToTop", @click="topFunction")
    font-awesome-icon(
      icon="arrow-up",
      :title="'Click to scroll back to top'"
    )
</template>

<script lang="ts">
import { defineComponent } from "vue";

export default defineComponent({
  name: "c-scroll-top-button",
  data(): {
    showBackToTop: boolean,
    scrollContainer: Element | Window | null,
  } {
    return {
      showBackToTop: false,
      scrollContainer: null,
    };
  },
  mounted() {
    // Use summary-wrapper if it exists, otherwise fall back to window
    this.scrollContainer = document.getElementById("summary-wrapper") || window;
    this.scrollContainer.addEventListener("scroll", this.handleScroll);
    this.handleScroll(); // Check initial state
  },
  beforeUnmount() {
    if (this.scrollContainer) {
      this.scrollContainer.removeEventListener("scroll", this.handleScroll);
    }
  },
  methods: {
    handleScroll() {
      const scrollTop = this.scrollContainer instanceof Window
        ? this.scrollContainer.scrollY
        : (this.scrollContainer as Element).scrollTop;
      this.showBackToTop = scrollTop > 200;
    },
    topFunction() {
      if (this.scrollContainer instanceof Window) {
        this.scrollContainer.scrollTo({ top: 0, behavior: "smooth" });
      } else {
        (this.scrollContainer as Element).scrollTo({ top: 0, behavior: "smooth" });
      }
    },
  },
});
</script>

<style lang="scss">
#go-back-button {
  align-content: center;
  background-color: darkseagreen;
  border-radius: 50%;
  bottom: 20px;
  color: white;
  cursor: pointer;
  font-size: 20px;
  height: 50px;
  margin-left: auto;
  place-items: center;
  position: sticky;
  right: 20px;
  width: 50px;
  z-index: 99;
}
</style>