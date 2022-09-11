const mixin = {
  methods: {
    isBrokenLink(link: string) {
      return link === undefined;
    },
    getLinkMessage(link: string, linkMessage: string): string {
      if (this.isBrokenLink(link)) {
        // @ts-ignore
        return this.disabledLinkMessage;
      }
      return linkMessage;
    },
  },
  data: {
    disabledLinkMessage: 'This remote link is unsupported',
  },
};
export default mixin;
