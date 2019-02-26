window.vZoomin = {
  props: ['info'],
  template: window.$('v_zoomin').innerHTML,
  data: () => ({}),
  methods: {
    openSummary() {
      this.$emit('view-summary', {});
    }
  },
  components: {
    v_ramp: window.vRamp,
  },
};
