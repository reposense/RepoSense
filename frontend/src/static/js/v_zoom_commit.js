/* global Vuex */

window.vZoomCommit = {
  props: [
      'defaultExpansionState',
      'slice',
      'selectedFileTypes',
  ],
  emits: [
      'increment-expanded-count',
      'decrement-expanded-count',
  ],
  data() {
    return {
      isExpanded: false,
    };
  },
  template: window.$('v_zoom_commit').innerHTML,

  computed: {
    filteredSelectedFileTypes() {
      const fileTypes = Object.keys(this.slice.fileTypesAndContributionMap);
      return fileTypes.filter((fileType) => this.selectedFileTypes.includes(fileType));
    },

    sliceLink() {
      let baseLink;
      if (this.tabZoomInfo.zIsMerge) {
        baseLink = window.getBaseLink(this.slice.repoId);
      } else {
        baseLink = this.tabZoomInfo.zUser.repoId;
      }
      return `${baseLink}/commit/${this.slice.hash}`;
    },

    ...Vuex.mapState(['fileTypeColors', 'tabZoomInfo']),
  },

  watch: {
    defaultExpansionState() {
      // 1 means all expanded, 0 means all collapsed, -1 means inconsistent
      if (this.defaultExpansionState === -1) {
        return;
      }
      if (this.isExpanded !== (this.defaultExpansionState === 1)) {
        this.toggleExpansion();
      }
    },
  },

  methods: {
    toggleExpansion() {
      if (this.slice.messageBody === '') {
        return;
      }
      this.isExpanded = !this.isExpanded;
      if (this.isExpanded) {
        this.$emit('increment-expanded-count');
      } else {
        this.$emit('decrement-expanded-count');
      }
    },
  },
  created() {
    if (this.defaultExpansionState === 1) {
      this.toggleExpansion();
    }
  },
  beforeDestroy() {
    if (this.isExpanded) {
      this.$emit('decrement-expanded-count');
    }
  },
};
