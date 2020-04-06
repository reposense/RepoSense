/* global Vuex */
const store = new Vuex.Store({
  state: {
    tabAuthorshipInfo: {},
    tabZoomInfo: {},
    summaryDates: {},
  },
  mutations: {
    updateTabZoomInfo(state, info) {
      state.tabZoomInfo = info;
    },
    updateTabAuthorshipInfo(state, info) {
      state.tabAuthorshipInfo = info;
    },
    updateSummaryDates(state, info) {
      state.summaryDates = info;
    },
  },
});

export default store;
