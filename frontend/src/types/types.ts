import { FileResult } from './zod/authorship-type';
import {
  AuthorFileTypeContributions,
  CommitResultRaw,
  Commits,
} from './zod/commits-type';
import { RepoRaw } from './zod/summary-type';

// We add these three fields in setContributionOfCommitResultsAndInsertRepoId of utils/api.ts
export interface CommitResult extends CommitResultRaw {
  repoId: string;
  insertions: number;
  deletions: number;
  isOpen?: boolean;
}

// Similar to AuthorDailyContributions, but uses the updated CommitResult with the three new fields
export interface DailyCommit {
  commitResults: CommitResult[];
  date: string;
}

export interface WeeklyCommit {
  commitResults: CommitResult[];
  date: string;
  endDate: string;
}

// Similar to DailyCommit, but contains the total insertions and deletions for all CommitResults
export interface Commit extends DailyCommit, WeeklyCommit {
  deletions: number;
  insertions: number;
}

// This type predicate distinguishes between Commit and DailyCommit
// https://www.typescriptlang.org/docs/handbook/2/narrowing.html#using-type-predicates
export function isCommit(commit: Commit | DailyCommit): commit is Commit {
  return (commit as Commit).deletions !== undefined;
}

export interface User {
  checkedFileTypeContribution: number | undefined;
  commits: Commit[];
  dailyCommits: DailyCommit[];
  displayName: string;
  fileTypeContribution: AuthorFileTypeContributions;
  location: string;
  name: string;
  repoId: string;
  repoName: string;
  searchPath: string;
  variance: number;
  sinceDate: string;
  untilDate: string;
  defaultSortOrder: number;
}

// We add defaultSortOrder, commits, users in loadCommits and files in loadAuthorship of utils/api.ts
export interface Repo extends RepoRaw {
  commits?: Commits;
  users?: User[];
  files?: FileResult[];
}

export interface AuthorshipFileSegment {
  knownAuthor: string | null;
  isFullCredit: boolean;
  lineNumbers: number[];
  lines: string[];
}

export interface AuthorshipFile {
  active: boolean;
  blankLineCount?: number;
  charCount?: number;
  fileType: string;
  isBinary: boolean;
  isIgnored: boolean;
  lineCount: number;
  path: string;
  segments?: AuthorshipFileSegment[];
  wasCodeLoaded: boolean;
}

export interface Bar {
  width: number;
  color?: string;
  tooltipText?: string;
}

export interface SegmentState {
  id: number;
  author: string | null;
  isFullCredit: boolean;
}

export interface GlobalFileEntry {
  repoName: string;
  path: string;
  fileType: string;
  lineCount: number;
  authors: string[];
  authorContributionMap: Record<string, number>;
  isBinary: boolean;
  isIgnored: boolean;
  active: boolean;
  lines?: { lineNumber: number; author: { gitId: string }; content: string; isFullCredit: boolean }[];
  segments?: AuthorshipFileSegment[];
}

