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

<script lang='ts'>
import { mapState } from 'vuex';
import { defineComponent } from 'vue';

const DRAG_BAR_WIDTH = 13.25;
const SCROLL_BAR_WIDTH = 17;
const GUIDE_BAR_WIDTH = 2;
const MAX_PORTRAIT_WIDTH = 768;

/** The following eslint suppression suppresses a rare false positive case where event cannot be accessed due to
 *  handler being a lambda function parameter. The explicit lambda function here allows us to easily discern handler's
 *  parameters, i.e. an event of type MouseEvent.
 */
// eslint-disable-next-line no-unused-vars
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

  data(): {
    guideWidth: number,
    guideHeight: number,
    flexWidth: number,
    flexHeight: number,
    isResizing: boolean,
    windowWidth: number,
    windowHeight: number,
  } {
    return {
      guideWidth: (0.5 * window.innerWidth - (GUIDE_BAR_WIDTH / 2)) / window.innerWidth,
      guideHeight: (0.5 * window.innerHeight - (GUIDE_BAR_WIDTH / 2)) / window.innerHeight,
      flexWidth: 0.5,
      flexHeight: 0.5,
      isResizing: false,
      windowWidth: window.innerWidth,
      windowHeight: window.innerHeight,
    };
  },

  computed: {
    isPortrait(): boolean {
      return this.windowWidth < MAX_PORTRAIT_WIDTH && this.windowHeight > this.windowWidth;
    },

    appStyles(): string {
      if (this.isResizing) {
        if (this.isPortrait) {
          return 'user-select: none; cursor: row-resize;';
        }
        return 'user-select: none; cursor: col-resize;';
      }
      return '';
    },

    guideStyles(): string {
      if (this.isResizing) {
        if (this.isPortrait) {
          return `display: block; bottom: ${this.guideHeight * 100}%;`;
        }
        return `display: block; right: ${this.guideWidth * 100}%;`;
      }
      return '';
    },

    rightContainerStyles(): string {
      if (this.isPortrait) {
        return `flex: 0 0 ${this.flexHeight * 100}%; height: ${this.flexHeight * 100}%;`;
      }
      return `flex: 0 0 ${this.flexWidth * 100}%;`;
    },

    mouseMove(): Function {
      if (this.isResizing) {
        return throttledEvent(25, (event: MouseEvent) => {
          if (this.isPortrait) {
            this.guideHeight = (
              Math.min(
                Math.max(
                  window.innerHeight - event.clientY,
                  SCROLL_BAR_WIDTH + DRAG_BAR_WIDTH,
                ),
                window.innerHeight - SCROLL_BAR_WIDTH,
              )
                - (GUIDE_BAR_WIDTH / 2)
            ) / window.innerHeight;
          } else {
            this.guideWidth = (
              Math.min(
                Math.max(
                  window.innerWidth - event.clientX,
                  SCROLL_BAR_WIDTH + DRAG_BAR_WIDTH,
                ),
                window.innerWidth - SCROLL_BAR_WIDTH,
              )
                - (GUIDE_BAR_WIDTH / 2)
            ) / window.innerWidth;
          }
        });
      }
      return () => {};
    },

    ...mapState(['isTabActive']),
  },

  mounted() {
    window.addEventListener('resize', this.handleResize);
  },

  beforeUnmount() {
    window.removeEventListener('resize', this.handleResize);
  },

  methods: {
    handleResize() {
      this.windowWidth = window.innerWidth;
      this.windowHeight = window.innerHeight;
    },

    registerMouseMove(): void {
      this.isResizing = true;
    },

    deregisterMouseMove(): void {
      this.isResizing = false;
      if (this.isPortrait) {
        this.flexHeight = this.guideHeight;
      } else {
        this.flexWidth = this.guideWidth;
      }

    },

    closeTab(): void {
      this.$store.commit('updateTabState', false);
    },
  },
});
</script>
