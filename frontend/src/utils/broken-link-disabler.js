const mixin = {
  methods: {
    isBrokenLink(link) {
      const linkFormat = /^(https:\/\/)(github.com|bitbucket.org|gitlab.com).*/;
      return !linkFormat.test(link);
    },
  },
  data: {
    disabledLinkMessage: 'This remote link is unsupported',
  },
};
export default mixin;
