import { defineComponent } from 'vue';

export default defineComponent({
  methods: {
    onTooltipHover(refName: string): void {
      const tooltipTextElement = this.getElementByRef(refName);
      if (this.isElementAboveViewport(tooltipTextElement)) {
        tooltipTextElement.classList.add('bottom-aligned');
      }
    },
    resetTooltip(refName: string): void {
      const tooltipTextElement = this.getElementByRef(refName);
      tooltipTextElement.classList.remove('bottom-aligned');
    },
    isElementAboveViewport(el: Element): boolean {
      return el.getBoundingClientRect().top <= 0;
    },
    /**
     * Note: this.$refs[refName] can be an array of HTMLElements
     * if the ref is on a v-for loop, else it will be a single HTMLElement.
     */
    getElementByRef(refName: string): HTMLElement {
      return Array.isArray(this.$refs[refName])
       ? (this.$refs[refName] as HTMLElement[])[0]
       : this.$refs[refName] as HTMLElement;
    },
  },
});
