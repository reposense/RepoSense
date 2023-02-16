export default class User {
  checkedFileTypeContribution : number;

  commits: Array<object>;

  dailyCommits: Array<object>;

  displayName: string;

  fileTypeContribution: object;

  location: string;

  name: string;

  repoId: string;

  repoName: string;

  searchPath: string;

  variance: number;

  constructor(userObj: User) {
    this.checkedFileTypeContribution = userObj.checkedFileTypeContribution || 0;
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
