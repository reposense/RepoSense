export default class User {
  checkedFileTypeContribution;

  commits;

  dailyCommits;

  displayName;

  fileTypeContribution;

  location;

  name;

  repoId;

  repoName;

  searchPath;

  variance;

  constructor(userObj) {
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
