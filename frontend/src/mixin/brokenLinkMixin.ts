import { defineComponent } from 'vue';

export default defineComponent({
  data(): { disabledLinkMessage: string } {
    return {
      disabledLinkMessage: 'This remote link is unsupported',
    };
  },
  methods: {
    isBrokenLink(link: string | undefined): boolean {
      return link === undefined;
    },
    getLinkMessage(link: string | undefined, linkMessage: string): string {
      if (this.isBrokenLink(link)) {
        return this.disabledLinkMessage;
      }
      return linkMessage;
    },
  },
});
