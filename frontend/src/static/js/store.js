/* global Vuex */
const store = new Vuex.Store({
  state: {
    tabAuthorshipInfo: {},
    tabZoomInfo: {},
  },
  mutations: {
    updateTabZoomInfo(state, info) {
      state.tabZoomInfo = info;
    },
    updateTabAuthorshipInfo(state, info) {
      state.tabAuthorshipInfo = info;
    },
  },
});

export default store;
