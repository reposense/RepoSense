const mixin = {
  methods: {
    isBrokenLink(link) {
      return link !== undefined;
    },
  },
  data: {
    disabledLinkMessage: 'This remote link is unsupported',
  },
};
export default mixin;
