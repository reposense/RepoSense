<template lang="pug">
  #app-wrapper(
    :style="appStyles",
    @mousemove="mouseMove",
    @mouseup="deregisterMouseMove",
    @mouseleave="deregisterMouseMove"
  )
    #tab-resize-guide(:style="guideStyles")
    .left-resize-container
      slot(name="left")
    #tab-resize(
      @mousedown.left="registerMouseMove",
      v-show="isTabActive"
    )
      .tab-close(@click="closeTab")
        i.fas.fa-caret-right
    .right-resize-container(
      :style="rightContainerStyles",
      v-if="isTabActive"
    )
      slot(name="right")
</template>

<script lang="ts">
import { mapState } from 'vuex';
import { defineComponent } from 'vue';

const DRAG_BAR_THICKNESS = 13.25;
const SCROLL_BAR_WIDTH = 17;
const GUIDE_BAR_WIDTH = 2;

const throttledEvent = (delay: number, handler: (event: MouseEvent) => unknown): ((event: MouseEvent) => void) => {
  let lastCalled = 0;
  return (event: MouseEvent): void => {
    if (Date.now() - lastCalled > delay) {
      lastCalled = Date.now();
      handler(event);
    }
  };
};

export default defineComponent({
  name: 'c-resizer',

  data() {
    const isPortrait = window.innerHeight > window.innerWidth;
    return {
      isPortrait,
      guideRatio: 0.5,
      flexRatio: 0.5,
      isResizing: false
    };
  },

  computed: {
    ...mapState(['isTabActive']),

    appStyles(): string {
      return this.isResizing
        ? `user-select: none; cursor: ${this.isPortrait ? 'row-resize' : 'col-resize'};`
        : '';
    },

    guideStyles(): string {
      if (!this.isResizing) return '';
      const percent = this.guideRatio * 100;
      return this.isPortrait
        ? `display: block; bottom: ${percent}%;`
        : `display: block; right: ${percent}%;`;
    },

    rightContainerStyles(): string {
      const percent = this.flexRatio * 100;
      return this.isPortrait
        ? `flex: 0 0 ${percent}%; height: ${percent}%;`
        : `flex: 0 0 ${percent}%;`;
    },

    mouseMove(): Function {
      if (!this.isResizing) return () => {};

      return throttledEvent(25, (event: MouseEvent) => {
        if (this.isPortrait) {
          const available = window.innerHeight;
          const offset = Math.min(
            Math.max(
              available - event.clientY,
              SCROLL_BAR_WIDTH + DRAG_BAR_THICKNESS
            ),
            available - SCROLL_BAR_WIDTH
          );
          this.guideRatio = (offset - GUIDE_BAR_WIDTH / 2) / available;
        } else {
          const available = window.innerWidth;
          const offset = Math.min(
            Math.max(
              available - event.clientX,
              SCROLL_BAR_WIDTH + DRAG_BAR_THICKNESS
            ),
            available - SCROLL_BAR_WIDTH
          );
          this.guideRatio = (offset - GUIDE_BAR_WIDTH / 2) / available;
        }
      });
    }
  },

  mounted() {
    window.addEventListener('resize', () => {
      this.isPortrait = window.innerHeight > window.innerWidth;
    });
  },

  methods: {
    registerMouseMove(): void {
      this.isResizing = true;
    },

    deregisterMouseMove(): void {
      if (!this.isResizing) return;

      this.isResizing = false;
      const available = this.isPortrait ? window.innerHeight : window.innerWidth;
      this.flexRatio = (this.guideRatio * available + GUIDE_BAR_WIDTH / 2) / available;
    },

    closeTab(): void {
      this.$store.commit('updateTabState', false);
    }
  }
});
</script>
