import { createStore } from 'vuex';
import { AuthorshipFile, CommitResult, DailyCommit } from '../types/types';
import {
  AuthorshipInfo,
  StoreState,
  SummaryDates,
  ZoomInfo,
} from '../types/vuex.d';

export default createStore<StoreState>({
  state: {
    tabAuthorshipInfo: {} as AuthorshipInfo,
    tabZoomInfo: {} as ZoomInfo,
    summaryDates: {} as SummaryDates,
    mergedGroups: [],
    fileTypeColors: {},
    tabAuthorColors: {},
    loadingOverlayCount: 0,
    loadingOverlayMessage: '',
    isTabActive: true,
  } as StoreState,
  mutations: {
    updateTabZoomInfo(state: StoreState, info: ZoomInfo): void {
      state.tabZoomInfo = info;
    },
    updateTabAuthorshipInfo(state: StoreState, info: AuthorshipInfo): void {
      state.tabAuthorshipInfo = info;
    },
    updateSummaryDates(state: StoreState, info: SummaryDates): void {
      state.summaryDates = info;
    },
    updateFileTypeColors(state: StoreState, info: { [key: string]: string }): void {
      state.fileTypeColors = info;
    },
    updateAuthorColors(state: StoreState, info: { [key: string]: string }): void {
      state.tabAuthorColors = info;
    },
    updateMergedGroup(state: StoreState, info: string[]): void {
      state.mergedGroups = info;
    },
    incrementLoadingOverlayCount(state: StoreState, increment: number): void {
      state.loadingOverlayCount += increment;
      if (state.loadingOverlayCount === 0) {
        state.loadingOverlayMessage = 'Loading. Please wait...';
      }
    },
    updateLoadingOverlayMessage(state: StoreState, message: string): void {
      state.loadingOverlayMessage = message;
    },
    updateTabState(state: StoreState, isTabOpen: boolean): void {
      state.isTabActive = isTabOpen;
      window.addHash('tabOpen', isTabOpen.toString());
      if (!isTabOpen) {
        window.removeHash('tabType');
      }
      window.encodeHash();
    },
    toggleZoomCommitMessageBody(_, slice: CommitResult): void {
      if (slice.isOpen !== undefined) {
        slice.isOpen = !slice.isOpen;
      }
    },
    setAllZoomCommitMessageBody(_, { isOpen, commits }: { isOpen: boolean; commits: DailyCommit[] }): void {
      commits.forEach((commit) => {
        commit.commitResults.forEach((slice) => {
          if (slice.isOpen !== undefined) {
            slice.isOpen = isOpen;
          }
        });
      });
    },
    updateTabAuthorshipFiles(state: StoreState, files: AuthorshipFile[]): void {
      state.tabAuthorshipInfo.files.splice(0, state.tabAuthorshipInfo.files.length, ...files);
    },
    toggleAuthorshipFileActiveProperty(_, file: AuthorshipFile): void {
      file.active = !file.active;
      file.wasCodeLoaded = file.wasCodeLoaded || file.active;
    },
    setAllAuthorshipFileActiveProperty(_, { isActive, files }: { isActive: boolean; files: AuthorshipFile[] }): void {
      files.forEach((file) => {
        file.active = isActive;
        file.wasCodeLoaded = file.wasCodeLoaded || file.active;
      });
    },
  },
  actions: {
    // Actions are called with dispatch

    async incrementLoadingOverlayCountForceReload({ commit }, increment: number): Promise<void> {
      commit('incrementLoadingOverlayCount', increment);
      await new Promise(window.requestAnimationFrame);
      await new Promise(window.requestAnimationFrame);
      // Needed as browsers render lazily by default
      // https://stackoverflow.com/a/44146560
    },
  },
});
