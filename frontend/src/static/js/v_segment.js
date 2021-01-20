window.vSegment = {
  props: ['segment', 'path'],
  template: window.$('v_segment').innerHTML,
  data() {
    return {
      isOpen: this.segment.authored || this.segment.lines.length < 5,
      hasCloser: !this.segment.authored && this.segment.lines.length > 4,
    };
  },
  methods: {
    toggleCode() {
      this.isLoaded = true;
      this.isOpen = !this.isOpen;
    },
  },
};
