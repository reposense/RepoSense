/* global Vuex */
const store = new Vuex.Store({
  state: {
    tabAuthorshipInfo: {},
    tabZoomInfo: {},
    summaryDates: {},
    mergedGroups: [],
    minDate: '',
    maxDate: '',
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
    updateMinDate(state, newDate) {
      state.minDate = newDate;
    },
    updateMaxDate(state, newDate) {
      state.maxDate = newDate;
    },
  },
});

export default store;
