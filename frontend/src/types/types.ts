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

// Similar to DailyCommit, but contains the total insertions and deletions for all CommitResults
export interface Commit extends DailyCommit {
  deletions: number;
  insertions: number;
}

export interface User {
  checkedFileTypeContribution: number;
  commits?: Commit[];
  dailyCommits: DailyCommit[];
  displayName: string;
  fileTypeContribution: AuthorFileTypeContributions;
  location: string;
  name: string;
  repoId: string;
  repoName: string;
  searchPath: string;
  variance: number;
}

// We add these three fields in loadCommits and loadAuthorship of utils/api.ts
export interface Repo extends RepoRaw {
  commits?: Commits;
  files?: FileResult[];
  users?: User[];
}

export interface AuthorshipFileSegment {
  knownAuthor: string | null;
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
