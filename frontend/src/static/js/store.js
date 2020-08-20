/* global Vuex */
const store = new Vuex.Store({
  state: {
    tabAuthorshipInfo: {},
    tabZoomInfo: {},
    summaryDates: {},
    mergedGroups: {},
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
    updateMergedGroup(state, {group, info}) {
      state.mergedGroups[group] = info;
      state.mergedGroups = { ...state.mergedGroups };
    },
  },
  actions: {
    initialize(context) {
      context.commit('updateMergedGroup',
                     {
                        group: 'groupByNone',
                        info: []
                     });
      context.commit('updateMergedGroup',
                     {
                       group: 'groupByRepos',
                       info: []
                     });
      context.commit('updateMergedGroup',
                     {
                       group: 'groupByAuthors',
                       info: []
                     });
    }
  }
});

export default store;
