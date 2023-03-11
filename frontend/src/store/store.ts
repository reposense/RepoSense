import { createStore } from 'vuex';
import { AuthorshipFile, CommitResult, DailyCommit } from '../types/types';
import {
  AuthorshipInfo,
  StoreState,
  SummaryDates,
  ZoomInfo,
} from '../types/vuex.d';

export default createStore({
  state: {
    tabAuthorshipInfo: {} as AuthorshipInfo,
    tabZoomInfo: {} as ZoomInfo,
    summaryDates: {} as SummaryDates,
    mergedGroups: [],
    fileTypeColors: {},
    loadingOverlayCount: 0,
    loadingOverlayMessage: '',
    isTabActive: true,
  } as StoreState,
  mutations: {
    updateTabZoomInfo(state, info: ZoomInfo) {
      state.tabZoomInfo = info;
    },
    updateTabAuthorshipInfo(state, info: AuthorshipInfo) {
      state.tabAuthorshipInfo = info;
    },
    updateSummaryDates(state, info: SummaryDates) {
      state.summaryDates = info;
    },
    updateFileTypeColors(state, info: { [key: string]: string }) {
      state.fileTypeColors = info;
    },
    updateMergedGroup(state, info: string[]) {
      state.mergedGroups = info;
    },
    incrementLoadingOverlayCount(state, increment: number) {
      state.loadingOverlayCount += increment;
      if (state.loadingOverlayCount === 0) {
        state.loadingOverlayMessage = 'Loading. Please wait...';
      }
    },
    updateLoadingOverlayMessage(state, message: string) {
      state.loadingOverlayMessage = message;
    },
    updateTabState(state, isTabOpen: boolean) {
      state.isTabActive = isTabOpen;
      window.addHash('tabOpen', isTabOpen.toString());
      if (!isTabOpen) {
        window.removeHash('tabType');
      }
      window.encodeHash();
    },
    toggleZoomCommitMessageBody(_, slice: CommitResult) {
      if (slice.isOpen !== undefined) {
        slice.isOpen = !slice.isOpen;
      }
    },
    setAllZoomCommitMessageBody(_, { isOpen, commits }: { isOpen: boolean; commits: DailyCommit[] }) {
      commits.forEach((commit) => {
        commit.commitResults.forEach((slice) => {
          if (slice.isOpen !== undefined) {
            slice.isOpen = isOpen;
          }
        });
      });
    },
    updateTabAuthorshipFiles(state, files: AuthorshipFile[]) {
      state.tabAuthorshipInfo.files.splice(0, state.tabAuthorshipInfo.files.length, ...files);
    },
    toggleAuthorshipFileActiveProperty(_, file: AuthorshipFile) {
      file.active = !file.active;
      file.wasCodeLoaded = file.wasCodeLoaded || file.active;
    },
    setAllAuthorshipFileActiveProperty(_, { isActive, files }: { isActive: boolean; files: AuthorshipFile[] }) {
      files.forEach((file) => {
        file.active = isActive;
        file.wasCodeLoaded = file.wasCodeLoaded || file.active;
      });
    },
  },
  actions: {
    // Actions are called with dispatch

    async incrementLoadingOverlayCountForceReload({ commit }, increment: number) {
      commit('incrementLoadingOverlayCount', increment);
      await new Promise(window.requestAnimationFrame);
      await new Promise(window.requestAnimationFrame);
      // Needed as browsers render lazily by default
      // https://stackoverflow.com/a/44146560
    },
  },
});
