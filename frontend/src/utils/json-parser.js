const REPORT_DIR = '.';

window.api = {
  async loadJSON(fname) {
    if (window.REPORT_ZIP) {
      const zipObject = window.REPORT_ZIP.file(fname);
      if (zipObject) {
        try {
          return JSON.parse(await zipObject.async('text'));
        } catch (e) {
          throw new Error('Uploaded JSON is invalid.');
        }
      } else {
        throw new Error('Uploaded zip file is invalid.');
      }
    }
    try {
      const response = await fetch(`${REPORT_DIR}/${fname}`);
      // Not directly returned in case response is not actually json.
      const json = await response.json();
      return json;
    } catch (e) {
      throw new Error(`Unable to read ${fname}.`);
    }
  },
  async loadSummary() {
    window.REPOS = {};
    let data = {};
    try {
      data = await this.loadJSON('summary.json');
    } catch (error) {
      if (error.message === 'Unable to read summary.json.') {
        return null;
      }
      throw error;
    }
    const { reportGeneratedTime, reportGenerationTime } = data;
    window.sinceDate = data.sinceDate;
    window.untilDate = data.untilDate;
    window.repoSenseVersion = data.repoSenseVersion;
    window.isSinceDateProvided = data.isSinceDateProvided;
    window.isUntilDateProvided = data.isUntilDateProvided;
    document.title = data.reportTitle || document.title;

    const errorMessages = {};
    Object.entries(data.errorSet).forEach(([repoName, message]) => {
      errorMessages[repoName] = message;
    });

    window.DOMAIN_URL_MAP = data.supportedDomainUrlMap;

    const names = [];
    data.repos.forEach((repo) => {
      const repoName = `${repo.displayName}`;
      window.REPOS[repoName] = repo;
      names.push(repoName);
    });
    return {
      creationDate: reportGeneratedTime,
      reportGenerationTime,
      errorMessages,
      names,
    };
  },

  async loadCommits(repoName) {
    const folderName = window.REPOS[repoName].outputFolderName;
    const commits = await this.loadJSON(`${folderName}/commits.json`);
    const res = [];
    const repo = window.REPOS[repoName];

    Object.keys(commits.authorDisplayNameMap).forEach((author) => {
      if (author) {
        const obj = {
          name: author,
          repoId: repoName,
          variance: commits.authorContributionVariance[author],
          displayName: commits.authorDisplayNameMap[author],
          dailyCommits: commits.authorDailyContributionsMap[author],
          fileTypeContribution: commits.authorFileTypeContributionMap[author],
        };

        this.setContributionOfCommitResultsAndInsertRepoId(obj.dailyCommits, obj.repoId);

        const searchParams = [
            repo.displayName,
            obj.displayName, author,
        ];

        obj.searchPath = searchParams.join('_').toLowerCase();
        obj.repoName = `${repo.displayName}`;
        obj.location = `${repo.location.location}`;

        res.push(obj);
      }
    });

    repo.commits = commits;
    repo.users = res;

    return res;
  },

  loadAuthorship(repoName) {
    const folderName = window.REPOS[repoName].outputFolderName;
    return this.loadJSON(`${folderName}/authorship.json`)
        .then((files) => {
          window.REPOS[repoName].files = files;
          return files;
        });
  },

  // calculate and set the contribution of each commitResult and insert repoId into commitResult, since not provided in json file
  setContributionOfCommitResultsAndInsertRepoId(dailyCommits, repoId) {
    dailyCommits.forEach((commit) => {
      commit.commitResults.forEach((result) => {
        result.repoId = repoId;
        result.insertions = Object.values(result.fileTypesAndContributionMap)
            .reduce((acc, fileType) => acc + fileType.insertions, 0);
        result.deletions = Object.values(result.fileTypesAndContributionMap)
            .reduce((acc, fileType) => acc + fileType.deletions, 0);
      });
    });
  },
};
