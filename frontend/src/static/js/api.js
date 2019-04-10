// utility functions //
window.$ = (id) => document.getElementById(id);
window.enquery = (key, val) => `${key}=${encodeURIComponent(val)}`;
const REPORT_DIR = '.';

// data retrieval functions //
function loadJSON(fname) {
  if (window.REPORT_ZIP) {
    const zipObject = window.REPORT_ZIP.file(fname.slice(2));
    if (zipObject) {
      return zipObject.async('text').then((txt) => JSON.parse(txt));
    }
    return Promise.reject(new Error('Zip file is invalid.'));
  }
  return new Promise((resolve, reject) => {
    const xhr = new XMLHttpRequest();
    xhr.open('GET', fname);
    xhr.onload = function xhrOnload() {
      if (xhr.status === 200) {
        resolve(JSON.parse(xhr.responseText));
      } else {
        reject(new Error('Unable to get file.'));
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
          window.app.creationDate = data.reportGeneratedTime;
          window.app.sinceDate = data.repos[0].sinceDate;

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
      const res = [];
      const repo = window.REPOS[repoName];
      let repoTotalCommits = 0;
      let repoTotalVariance = 0;

      Object.keys(commits.authorDisplayNameMap).forEach((author) => {
        if (author) {
          const obj = {
            name: author,
            repoId: repoName,
            variance: commits.authorContributionVariance[author],
            displayName: commits.authorDisplayNameMap[author],
            dailyCommits: commits.authorDailyContributionsMap[author],
            totalCommits: commits.authorFinalContributionMap[author],
            fileFormatContribution: commits.authorFileFormatContributionMap[author],
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
          repoTotalVariance += obj.variance;
          res.push(obj);
        }
      });

      repo.commits = commits;
      repo.users = res;
      res.forEach((author) => {
        author.repoTotalCommits = repoTotalCommits;
        author.repoTotalVariance = repoTotalVariance;
      });

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

  loadAuthorCommitsVariance() {
    const repos = window.REPOS;
    const authorCommits = {};
    const authorVariance = {};
    Object.keys(repos).forEach((repo) => {
      repos[repo].users.forEach((user) => {
        if (!Object.keys(authorCommits).includes(user)) {
          authorCommits[user.name] = 0;
          authorVariance[user.name] = 0;
        }
        authorCommits[user.name] += user.totalCommits;
        authorVariance[user.name] += user.variance;
      });
    });
    Object.keys(repos).forEach((repo) => {
      repos[repo].users.forEach((user) => {
        user.authorCommits = authorCommits[user.name];
        user.authorVariance = authorVariance[user.name];
      });
    });
  },

};
