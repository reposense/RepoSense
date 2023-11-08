import { defineComponent } from 'vue';

export default defineComponent({
  methods: {
    onTooltipHover(refName: string): void {
      const tooltipTextElement = (this.$refs[refName] as HTMLElement[])[0];
      if (this.isElementAboveViewport(tooltipTextElement)) {
        tooltipTextElement.classList.add('bottom-aligned');
      }
    },
    resetTooltip(refName: string): void {
      const tooltipTextElement = (this.$refs[refName] as HTMLElement[])[0];
      tooltipTextElement.classList.remove('bottom-aligned');
    },
    isElementAboveViewport(el: Element): boolean {
      return el.getBoundingClientRect().top <= 0;
    },
  },
});
