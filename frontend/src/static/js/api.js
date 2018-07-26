// utility functions //
window.$ = id => document.getElementById(id);
window.enquery = (key, val) => `${key}=${encodeURIComponent(val)}`;

const REPORT_DIR = '.';

// data retrieval functions //
function loadJSON(fname, fn) {
  const err = () => window.alert('unable to get file');

  if (window.REPORT_ZIP) {
    window.REPORT_ZIP.file(fname.slice(2)).async('text')
      .then(txt => fn(JSON.parse(txt)));
  } else {
    const xhr = new XMLHttpRequest();
    xhr.open('GET', fname);
    xhr.onload = function xhrOnload() {
      if (xhr.status === 200) {
        fn(JSON.parse(xhr.responseText));
      } else {
        err();
      }
    };
    xhr.send(null);
  }
}

window.api = {
  loadSummary(callback) {
    window.REPOS = {};

    loadJSON(`${REPORT_DIR}/summary.json`, (data) => {
      const names = [];
      window.app.creationDate = data.dashboardGeneratedTime;
      data.repos.forEach((repo) => {
        const repoName = `${repo.displayName}`;
        window.REPOS[repoName] = repo;
        names.push(repoName);
      });

      if (callback) {
        callback(names);
      }
    });
  },

  loadCommits(repoName, callback) {
    loadJSON(`${REPORT_DIR}/${repoName}/commits.json`, (commits) => {
      const res = [];
      const repo = window.REPOS[repoName];

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
            repo.organization, repo.repoName,
            obj.displayName, author,
          ];

          obj.searchPath = searchParams.join('_').toLowerCase();
          obj.repoPath = `${repo.displayName}`;
          obj.repoName = `${repo.displayName}`;

          res.push(obj);
        }
      });

      repo.commits = commits;
      repo.users = res;

      if (callback) {
        callback(res);
      }
    });
  },

  loadAuthorship(repoName, callback) {
    loadJSON(`${REPORT_DIR}/${repoName}/authorship.json`, (files) => {
      window.REPOS[repoName].files = files;

      if (callback) {
        callback(files);
      }
    });
  },

};
