<template lang="pug">
  button.go-back-button(
    v-show="showBackToTop",
    type="button",
    aria-label="Scroll back to top",
    @click="$emit('scroll-to-top')"
  )
    font-awesome-icon(
      icon="arrow-up",
      aria-hidden="true"
    )
</template>

<script lang="ts">
import { defineComponent } from 'vue';

export default defineComponent({
  name: 'c-scroll-top-button',
  props: {
    showBackToTop: {
      type: Boolean,
      default: false,
    },
  },
  emits: ['scroll-to-top'],
});
</script>

<style lang='scss' scoped>
@import '../styles/_colors.scss';
@import '../styles/_z-indices.scss';

.go-back-button {
  background-color: mui-color('green');
  border: 0;
  border-radius: 50%;
  bottom: 20px;
  color: mui-color('white');
  cursor: pointer;
  display: flex;
  font-size: 20px;
  height: 50px;
  margin-left: auto;
  padding: 0;
  place-content: center;
  place-items: center;
  position: sticky;
  right: 20px;
  width: 50px;
  z-index: z-index('scroll-back-button');
}
</style>
