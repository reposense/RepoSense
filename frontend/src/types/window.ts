import JSZip from 'jszip';
import User from '../utils/user';
import { Repo, User as UserType } from './types';
import { AuthorshipSchema } from './zod/authorship-type';
import { AuthorDailyContributions } from './zod/commits-type';
import { DomainUrlMap, ErrorMessage } from './zod/summary-type';

// Declares the types for all the global variables under the window object
export {};

interface comparatorFunction {
  (a: any, b: any): -1 | 0 | 1;
}

interface sortingFunction {
  (item: any, sortingOption?: string): any;
}

interface api {
  loadJSON: (fname: string) => Promise<any>;
  loadSummary: () => Promise<{
    creationDate: string,
    reportGenerationTime: string,
    errorMessages: { [key: string]: ErrorMessage },
    names: string[],
  } | null>;
  loadCommits: (repoName: string) => Promise<User[]>;
  loadAuthorship: (repoName: string) => Promise<AuthorshipSchema>;
  setContributionOfCommitResultsAndInsertRepoId: (dailyCommits: AuthorDailyContributions[], repoId: string) => void;
}

declare global {
  interface Window {
    $: (id: string) => HTMLElement | null;
    enquery: (key: string, val: string) => string;
    REPOSENSE_REPO_URL: string;
    HOME_PAGE_URL: string;
    UNSUPPORTED_INDICATOR: string;
    DAY_IN_MS: number;
    HASH_DELIMITER: string;
    REPOS: { [key: string]: Repo };
    hashParams: { [key: string]: string };
    isMacintosh: boolean;
    REPORT_ZIP: JSZip | null;
    deactivateAllOverlays: () => void;
    getDateStr: (date: Date) => string;
    getHexToRGB: (color: string) => number[];
    getFontColor: (color: string) => string;
    addHash: (newKey: string, newVal: string) => void;
    removeHash: (key: string) => void;
    encodeHash: () => void;
    decodeHash: () => void;
    comparator: (fn: sortingFunction, sortingOption: string) => comparatorFunction;
    filterUnsupported: (string: string) => string | undefined;
    getAuthorLink: (repoId: string, author: string) => string | undefined;
    getRepoLinkUnfiltered: (repoId: string) => string;
    getRepoLink: (repoId: string) => string | undefined;
    getBranchLink: (repoId: string, branch: string) => string | undefined;
    getCommitLink: (repoId: string, commitHash: string) => string | undefined;
    getBlameLink: (repoId: string, branch: string, filepath: string) => string | undefined;
    getHistoryLink: (repoId: string, branch: string, filepath: string) => string | undefined;
    getGroupName: (group: UserType[], filterGroupSelection: string) => string;
    getAuthorDisplayName: (authorRepos: Repo[]) => string;
    api: api;
    sinceDate: string;
    untilDate: string;
    repoSenseVersion: string;
    isSinceDateProvided: boolean;
    isUntilDateProvided: boolean;
    DOMAIN_URL_MAP: DomainUrlMap;
  }
}
