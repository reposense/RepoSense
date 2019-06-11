window.vCode = {
  props: ['segment', 'path'],
  template: window.$('v_code').innerHTML,
  data() {
    return {
      loaded: this.segment.authored,
    }
  },
  methods: {
    loadCode() {
      if (!this.loaded) {
        this.loaded = true;
      }
    },
    isClose() {
      return !this.segment.authored && this.segment.lines.length > 4;
    }
  },
};
