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

function isDisplayNameUnique(currName, arrNames) {
  const setNames = new Set(arrNames);
  return !setNames.has(currName);
}

function makeDisplayNameUnique(currName, mapNames) {
  return currName + " (" + mapNames[currName] + ")";
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
          const mapNames = {};
          data.repos.forEach((repo) => {
            let repoName = `${repo.displayName}`;
            if (isDisplayNameUnique(repoName, names)) {
              mapNames[repoName] = 0;
            } else {
              mapNames[repoName] += 1;
              repoName = makeDisplayNameUnique(repoName, mapNames);
              repo.displayName = repoName;  // to check whether it works
              console.log(repoName + " not unique " + repo.outputFolderName);
            }

            window.REPOS[repoName] = repo;
            names.push(repoName);
          });
          return names;
        });
  },

  loadCommits(repoName) {
    const folderName = window.REPOS[repoName].outputFolderName;
    return loadJSON(`${REPORT_DIR}/${folderName}/commits.json`).then((commits) => {
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
    const folderName = window.REPOS[repoName].outputFolderName;
    return loadJSON(`${REPORT_DIR}/${folderName}/authorship.json`)
        .then((files) => {
          window.REPOS[repoName].files = files;
          return files;
        });
  },

};
