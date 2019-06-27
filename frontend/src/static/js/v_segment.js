window.toggleSegment = function toggleSegment(ele) {
  // function for toggling unopened segment
  const targetClass = 'active toggled';

  const parent = ele.parentNode;
  const classes = parent.className.split(' ');
  const idxActive = classes.indexOf('active');
  const idxToggled = classes.indexOf('toggled');

  if (idxActive === -1 && idxToggled === -1) {
    classes.push(targetClass);
  } else {
    classes.splice(idxActive, 2);
  }

  parent.className = classes.join(' ');
};

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
      window.toggleSegment(this.$el.childNodes[0]);
    },
  },
};
