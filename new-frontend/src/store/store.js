import Vue from 'vue';
import Vuex from 'vuex';

Vue.use(Vuex);

const store = new Vuex.Store({
  state: {
    tabAuthorshipInfo: {},
    tabZoomInfo: {},
    summaryDates: {},
    mergedGroups: [],
    fileTypeColors: {},
    loadingOverlayCount: 0,
    loadingOverlayMessage: '',
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
    updateFileTypeColors(state, info) {
      state.fileTypeColors = info;
    },
    updateMergedGroup(state, info) {
      state.mergedGroups = info;
    },
    incrementLoadingOverlayCount(state, increment) {
      state.loadingOverlayCount += increment;
      if (state.loadingOverlayCount === 0) {
        state.loadingOverlayMessage = 'Loading. Please wait...';
      }
    },
    updateLoadingOverlayMessage(state, message) {
      state.loadingOverlayMessage = message;
    },
  },
});

export default store;
