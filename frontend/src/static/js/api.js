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
          window.app.sinceDate = data.sinceDate;
          window.app.untilDate = data.untilDate;
          window.app.repoSenseVersion = data.repoSenseVersion;

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

          res.push(obj);
        }
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

  calcCommitsVariance() {
    const repos = window.REPOS;
    const authorCommits = {};
    const authorVariance = {};

    Object.keys(repos).forEach((repo) => {
      let repoCommits = 0;
      let repoVariance = 0;
      repos[repo].users.forEach((author) => {
        repoCommits += author.totalCommits;
        repoVariance += author.variance;
        if (!Object.keys(author).includes('authorCommits')) {
          author.authorCommits = 0;
        }
        if (!Object.keys(author).includes('authorVariance')) {
          author.authorVariance = 0;
        }
        // Accumulate author's commits and variance
        author.authorCommits += author.totalCommits;
        author.authorVariance += author.variance;
      });

      // Write total commits and variance into repo's data
      repos[repo].users.forEach((author) => {
        author.repoCommits = repoCommits;
        author.repoVariance = repoVariance;
      });
    });

  },

};
