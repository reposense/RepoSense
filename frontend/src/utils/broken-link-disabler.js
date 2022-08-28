window.$ = (id) => document.getElementById(id);
const mixin = {
  methods: {
    isBrokenLink(link) {
      let isLinkBroken = true;
      Object.keys(window.DOMAIN_URL_MAP).forEach((k) => {
        if (k !== 'NOT_RECOGNIZED' && link.startsWith(window.DOMAIN_URL_MAP[k].BASE_URL)) {
          isLinkBroken = false;
        }
      });
      return isLinkBroken;
    },
  },
  data: {
    disabledLinkMessage: 'This remote link is unsupported',
  },
};
export default mixin;
