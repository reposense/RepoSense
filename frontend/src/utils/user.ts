import { Commit, DailyCommit, User as UserType } from '../types/types';
import { AuthorFileTypeContributions } from '../types/zod/commits-type';

export default class User implements UserType {
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

  constructor(userObj: User) {
    this.checkedFileTypeContribution = userObj.checkedFileTypeContribution;
    this.commits = userObj.commits || [];
    this.dailyCommits = userObj.dailyCommits || [];
    this.displayName = userObj.displayName || '';
    this.fileTypeContribution = userObj.fileTypeContribution || {};
    this.location = userObj.location || '';
    this.name = userObj.name || '';
    this.repoId = userObj.repoId || '';
    this.repoName = userObj.repoName || '';
    this.searchPath = userObj.searchPath || '';
    this.variance = userObj.variance || 0;
  }
}
