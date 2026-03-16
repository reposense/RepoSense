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
  props: {
    scrollContainerId: {
      type: String,
      default: null,
    },
  },
  data(): {
    showBackToTop: boolean,
    scrollContainer: Element | null,
  } {
    return {
      showBackToTop: false,
      scrollContainer: null,
    };
  },
  mounted() {
    this.scrollContainer = (this.scrollContainerId
        ? document.getElementById(this.scrollContainerId)
        : null);
    if (this.scrollContainer) {
      this.scrollContainer.addEventListener("scroll", this.handleScroll);
      this.handleScroll(); // Check initial state
    }
  },
  beforeUnmount() {
    if (this.scrollContainer) {
      this.scrollContainer.removeEventListener("scroll", this.handleScroll);
    }
  },
  methods: {
    handleScroll() {
      const scrollTop = (this.scrollContainer as Element).scrollTop;
      this.showBackToTop = scrollTop > 200;
    },
    topFunction() {
      (this.scrollContainer as Element).scrollTo({top: 0, behavior: "smooth"});
    },
  },
});
</script>

<style lang="scss">
#go-back-button {
  background-color: darkseagreen;
  border-radius: 50%;
  bottom: 20px;
  color: white;
  cursor: pointer;
  display: flex;
  font-size: 20px;
  height: 50px;
  margin-left: auto;
  place-content: center;
  place-items: center;
  position: sticky;
  right: 20px;
  width: 50px;
  z-index: 99;
}
</style>
