// utility functions //
window.$ = id => document.getElementById(id);
window.enquery = (key, val) => `${key}=${encodeURIComponent(val)}`;
const REPORT_DIR = '.';

// data retrieval functions //
function loadJSON(fname) {
  if (window.REPORT_ZIP) {
    return window.REPORT_ZIP.file(fname.slice(2)).async('text')
      .then(txt => JSON.parse(txt));
  }
  return new Promise((resolve) => {
    const xhr = new XMLHttpRequest();
    xhr.open('GET', fname);
    xhr.onload = function xhrOnload() {
      if (xhr.status === 200) {
        resolve(JSON.parse(xhr.responseText));
      } else {
        window.alert('unable to get file');
      }
    };
    xhr.send(null);
  });
}

window.api = {
  loadSummary() {
    window.REPOS = {};

    return loadJSON(`${REPORT_DIR}/summary.json`)
      .then((data) => {
        window.app.creationDate = data.dashboardGeneratedTime;

        const names = [];
        data.repos.forEach((repo) => {
          const repoName = `${repo.displayName}`;
          window.REPOS[repoName] = repo;
          names.push(repoName);
        });
        return names;
      });
  },

  loadCommits(repoName) {
    return loadJSON(`${REPORT_DIR}/${repoName}/commits.json`).then((commits) => {
      let res = [];
      const repo = window.REPOS[repoName];
      let repoTotalCommits = 0;

      Object.keys(commits.authorDisplayNameMap).forEach((author) => {
        if (author) {
          const obj = {
            name: author,
            repoId: repoName,
            variance: commits.authorContributionVariance[author],
            displayName: commits.authorDisplayNameMap[author],
            weeklyCommits: commits.authorWeeklyIntervalContributions[author],
            dailyCommits: commits.authorDailyIntervalContributions[author],
            totalCommits: commits.authorFinalContributionMap[author],
          };

          const searchParams = [
            repo.displayName,
            obj.displayName, author,
          ];

          obj.searchPath = searchParams.join('_').toLowerCase();
          obj.repoPath = `${repo.displayName}`;
          obj.repoName = `${repo.displayName}`;
          obj.location = `${repo.location.location}`;
          repoTotalCommits += obj.totalCommits;
          res.push(obj);
        }
      });
      res.forEach((author) => {
        author.repoTotalCommits = repoTotalCommits;
      });
      repo.commits = commits;
      repo.users = res;

      return res;
    });
  },

  loadAuthorship(repoName) {
    return loadJSON(`${REPORT_DIR}/${repoName}/authorship.json`)
      .then((files) => {
        window.REPOS[repoName].files = files;
        return files;
      });
  },

};
