const mixin = {
  methods: {
    isBrokenLink(link) {
      return link === undefined;
    },
    getLinkMessage(link, linkMessage) {
      if (this.isBrokenLink(link)) {
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
