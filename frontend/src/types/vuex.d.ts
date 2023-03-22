import { Store } from 'vuex';
import { AuthorshipFile, User } from './types';

interface AuthorshipInfo {
  author: string;
  checkedFileTypes?: string[];
  files: AuthorshipFile[];
  isMergeGroup: boolean;
  isRefresh?: boolean;
  location: string | undefined;
  maxDate: string;
  minDate: string;
  name?: string;
  repo: string;
}

interface ZoomInfo {
  isRefreshing?: boolean;
  zAuthor: string;
  zAvgCommitSize: number | string;
  zFileTypeColors?: { [key: string]: string };
  zFilterGroup: string;
  zFilterSearch: string;
  zFromRamp: boolean;
  zIsMerged: boolean;
  zLocation?: string | undefined;
  zRepo: string;
  zSince: string;
  zTimeFrame: string;
  zUntil: string;
  zUser?: User;
}

interface SummaryDates {
  since: string;
  until: string;
}

interface StoreState {
  tabAuthorshipInfo: AuthorshipInfo;
  tabZoomInfo: ZoomInfo;
  summaryDates: SummaryDates;
  mergedGroups: string[];
  fileTypeColors: { [key: string]: string };
  tabAuthorColors: { [key: string]: string };
  loadingOverlayCount: number;
  loadingOverlayMessage: string;
  isTabActive: boolean;
}

declare module '@vue/runtime-core' {
  // eslint-disable-next-line @typescript-eslint/no-empty-interface
  interface State extends StoreState {
  }
  interface ComponentCustomProperties {
    $store: Store<State>;
  }
}
