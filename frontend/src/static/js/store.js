/* global Vuex */
const store = new Vuex.Store({
  state: {
    tabAuthorshipInfo: {},
    tabZoomInfo: {},
    summaryDates: {},
    mergedGroups: [],
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
    updateMergedGroup(state, info) {
      state.mergedGroups = info;
    },
  },
});

export default store;
