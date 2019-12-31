window.vSegment = {
  props: ['segment', 'path'],
  template: window.$('v_segment').innerHTML,
  data() {
    return {
      loaded: this.segment.authored || this.segment.lines.length < 5,
    };
  },
  methods: {
    loadCode() {
      this.loaded = true;
      // Update button and code css only once code has loaded into DOM
      this.$nextTick(() => {
        window.toggleNext(this.$el.childNodes[0]);
      });
    },
  },
};
