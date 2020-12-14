// utility functions //
window.$ = (id) => document.getElementById(id);
window.enquery = (key, val) => `${key}=${encodeURIComponent(val)}`;
window.BASE_URL = 'https://github.com';
window.HOME_PAGE_URL = 'https://reposense.org';
window.DAY_IN_MS = (1000 * 60 * 60 * 24);
window.HASH_DELIMITER = '~';
window.REPOS = {};
window.hashParams = {};
window.isMacintosh = navigator.platform.includes('Mac');

const HASH_ANCHOR = '?';
const REPORT_DIR = '.';
const REPORT_ZIP = null;

window.deactivateAllOverlays = function deactivateAllOverlays() {
  document.querySelectorAll('.summary-chart__ramp .overlay')
      .forEach((x) => {
        x.className = 'overlay';
      });
};

window.getDateStr = function getDateStr(date) {
  return (new Date(date)).toISOString().split('T')[0];
};

window.getHexToRGB = function getHexToRGB(color) {
  // to convert color from hex code to rgb format
  const arr = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(color);
  return arr.slice(1).map((val) => parseInt(val, 16));
};

window.getFontColor = function getFontColor(color) {
  const result = window.getHexToRGB(color);
  const red = result[0];
  const green = result[1];
  const blue = result[2];

  const luminosity = 0.2126 * red + 0.7152 * green + 0.0722 * blue; // per ITU-R BT.709

  return luminosity < 120 ? '#ffffff' : '#000000';
};

window.addHash = function addHash(newKey, newVal) {
  window.hashParams[newKey] = newVal;
};

window.removeHash = function removeHash(key) {
  delete window.hashParams[key];
};

window.encodeHash = function encodeHash() {
  const { hashParams } = window;

  const hash = Object.keys(hashParams)
      .map((key) => `${key}=${encodeURIComponent(hashParams[key])}`)
      .join('&');

  const newUrl = `${window.location.protocol}//${window.location.host}${window.location.pathname}${HASH_ANCHOR}${hash}`;
  window.history.replaceState(null, '', newUrl);
};

window.decodeHash = function decodeHash() {
  const hashParams = {};

  const hashIndex = window.location.href.indexOf(HASH_ANCHOR);
  const parameterString = hashIndex === -1 ? '' : window.location.href.slice(hashIndex + 1);

  parameterString.split('&')
      .forEach((param) => {
        const [key, val] = param.split('=');
        if (key) {
          try {
            hashParams[key] = decodeURIComponent(val);
          } catch (error) {
            this.userUpdated = false;
            this.isLoading = false;
          }
        }
      });
  window.hashParams = hashParams;
};

window.comparator = (fn, sortingOption = '') => function compare(a, b) {
  let a1;
  let b1;
  if (sortingOption) {
    a1 = fn(a, sortingOption).toLowerCase
        ? fn(a, sortingOption).toLowerCase()
        : fn(a, sortingOption);
    b1 = fn(b, sortingOption).toLowerCase
        ? fn(b, sortingOption).toLowerCase()
        : fn(b, sortingOption);
  } else {
    a1 = fn(a).toLowerCase ? fn(a).toLowerCase() : fn(a);
    b1 = fn(b).toLowerCase ? fn(b).toLowerCase() : fn(b);
  }
  if (a1 === b1) {
    return 0;
  } if (a1 < b1) {
    return -1;
  }
  return 1;
};

window.toggleNext = function toggleNext(ele) {
  // function for toggling unopened code
  const targetClass = 'active';

  const parent = ele.parentNode;
  const classes = parent.className.split(' ');
  const idx = classes.indexOf(targetClass);

  if (idx === -1) {
    classes.push(targetClass);
  } else {
    classes.splice(idx, 1);
  }

  parent.className = classes.join(' ');
};

window.getBaseLink = function getBaseLink(repoId) {
  return `${window.BASE_URL}/${
    window.REPOS[repoId].location.organization}/${
    window.REPOS[repoId].location.repoName}`;
};

window.getGroupName = function getGroupName(group, filterGroupSelection) {
  switch (filterGroupSelection) {
  case 'groupByRepos':
    return group[0].repoName;
  case 'groupByAuthors':
    return group[0].name;
  default:
    return '';
  }
};

window.api = {
  loadJSON(fname) {
    if (REPORT_ZIP) {
      const zipObject = REPORT_ZIP.file(fname.slice(2));
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
  },
  loadSummary() {
    window.REPOS = {};

    return this.loadJSON(`${REPORT_DIR}/summary.json`)
        .then((data) => {
          window.app.creationDate = data.reportGeneratedTime;
          window.app.sinceDate = data.sinceDate;
          window.app.untilDate = data.untilDate;
          window.app.repoSenseVersion = data.repoSenseVersion;
          window.app.reportGenerationTime = data.reportGenerationTime;
          window.app.isSinceDateProvided = data.isSinceDateProvided;
          window.app.isUntilDateProvided = data.isUntilDateProvided;

          Object.entries(data.errorList).forEach(([repoName, message]) => {
            window.app.errorMessages[repoName] = message;
          });

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
    const folderName = window.REPOS[repoName].outputFolderName;
    return this.loadJSON(`${REPORT_DIR}/${folderName}/commits.json`).then((commits) => {
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

          this.setContributionOfCommitResults(obj.dailyCommits);

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
    return this.loadJSON(`${REPORT_DIR}/${folderName}/authorship.json`)
        .then((files) => {
          window.REPOS[repoName].files = files;
          return files;
        });
  },

  // calculate and set the contribution of each commitResult, since not provided in json file
  setContributionOfCommitResults(dailyCommits) {
    dailyCommits.forEach((commit) => {
      commit.commitResults.forEach((result) => {
        result.insertions = Object.values(result.fileTypesAndContributionMap)
            .reduce((acc, fileType) => acc + fileType.insertions, 0);
        result.deletions = Object.values(result.fileTypesAndContributionMap)
            .reduce((acc, fileType) => acc + fileType.deletions, 0);
      });
    });
  },
};
