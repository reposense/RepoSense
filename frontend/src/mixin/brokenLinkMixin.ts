import { defineComponent } from 'vue';

export default defineComponent({
  data() {
    return {
      disabledLinkMessage: 'This remote link is unsupported',
    };
  },
  methods: {
    isBrokenLink(link: string) {
      return link === undefined;
    },
    getLinkMessage(link: string, linkMessage: string): string {
      if (this.isBrokenLink(link)) {
        return this.disabledLinkMessage;
      }
      return linkMessage;
    },
  },
});
