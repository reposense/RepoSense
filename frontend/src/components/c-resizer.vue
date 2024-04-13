<template lang="pug">
#app-wrapper(
  v-bind:style="appStyles",
  @mousemove="mouseMove",
  @mouseup="deregisterMouseMove",
  @mouseleave="deregisterMouseMove"
)
  #tab-resize-guide(v-bind:style="guideStyles")
  .left-resize-container
    slot(name="left")
  #tab-resize(
    @mousedown.left="registerMouseMove",
    v-show="isTabActive"
  )
    .tab-close(v-on:click="closeTab")
      i.fas.fa-caret-right
  .right-resize-container(
    v-bind:style="rightContainerStyles",
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
    flexWidth: number,
    isResizing: boolean
    } {
    return {
      guideWidth: (0.5 * window.innerWidth - (GUIDE_BAR_WIDTH / 2)) / window.innerWidth,
      flexWidth: 0.5,
      isResizing: false,
    };
  },

  computed: {
    appStyles(): string {
      return this.isResizing
        ? 'user-select: none; cursor: col-resize;'
        : '';
    },

    guideStyles(): string {
      return this.isResizing
        ? `display: block; right: ${this.guideWidth * 100}%;`
        : '';
    },

    rightContainerStyles(): string {
      return `flex: 0 0 ${this.flexWidth * 100}%;`;
    },

    mouseMove(): Function {
      if (this.isResizing) {
        return throttledEvent(25, (event: MouseEvent) => {
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
        });
      }
      return () => {};
    },

    ...mapState(['isTabActive']),
  },

  methods: {
    registerMouseMove(): void {
      this.isResizing = true;
    },

    deregisterMouseMove(): void {
      this.isResizing = false;
      this.flexWidth = (this.guideWidth * window.innerWidth + (GUIDE_BAR_WIDTH / 2))
        / window.innerWidth;
    },

    closeTab(): void {
      this.$store.commit('updateTabState', false);
    },
  },
});
</script>
