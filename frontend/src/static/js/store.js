window.store = new Vuex.Store({
  state: {
    authorshipTabInfo: {},
    zoomTabInfo: {},
  },
  mutations: {
    updateZoomTabInfo(state, info) {
      state.zoomTabInfo = info;
    },
    updateAuthorshipTabInfo(state, info) {
      state.authorshipTabInfo = info;
    },
  },
  actions: {
  },
});
