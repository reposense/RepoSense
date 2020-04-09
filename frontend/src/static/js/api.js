// utility functions //
window.$ = (id) => document.getElementById(id);
window.enquery = (key, val) => `${key}=${encodeURIComponent(val)}`;
window.BASE_URL = 'https://github.com';
window.REPOS = {};
window.hashParams = {};
window.isMacintosh = navigator.platform.includes('Mac');
window.drags = [];

const REPORT_DIR = '.';
const REPORT_ZIP = null;

const getBaseTarget = (target) => (target.className === 'summary-chart__ramp'
    ? target
    : getBaseTarget(target.parentElement));

const dragViewDown = (evt) => {
  window.deactivateAllOverlays();

  const pos = evt.clientX;
  const ramp = window.getBaseTarget(evt.target);
  window.drags = [pos];

  const base = ramp.offsetWidth;
  const offset = ramp.parentElement.offsetLeft;

  const overlay = ramp.getElementsByClassName('overlay')[0];
  overlay.style.marginLeft = '0';
  overlay.style.width = `${(pos - offset) * 100 / base}%`;
  overlay.className += ' edge';
};

const dragViewUp = (evt) => {
  window.deactivateAllOverlays();
  const ramp = getBaseTarget(evt.target);

  const base = ramp.offsetWidth;
  window.drags.push(evt.clientX);
  window.drags.sort((a, b) => a - b);

  const offset = ramp.parentElement.offsetLeft;
  window.drags = window.drags.map((x) => (x - offset) * 100 / base);

  const overlay = ramp.getElementsByClassName('overlay')[0];
  overlay.style.marginLeft = `${window.drags[0]}%`;
  overlay.style.width = `${window.drags[1] - window.drags[0]}%`;
  overlay.className += ' show';
};

window.deactivateAllOverlays = function deactivateAllOverlays() {
  document.querySelectorAll('.summary-chart__ramp .overlay')
      .forEach((x) => {
        x.className = 'overlay';
      });
};

window.getDateStr = function getDateStr(date) {
  return (new Date(date)).toISOString().split('T')[0];
};

window.viewClick = function viewClick(evt) {
  const isKeyPressed = this.isMacintosh ? evt.metaKey : evt.ctrlKey;
  if (window.drags.length === 2) {
    window.drags = [];
  }

  if (isKeyPressed) {
    return window.drags.length === 0
        ? dragViewDown(evt)
        : dragViewUp(evt);
  }

  return null;
};

window.addHash = function addHash(newKey, newVal) {
  window.hashParams[newKey] = newVal;
};

window.removeHash = function removeHash(key) {
  delete window.hashParams[key];
};

window.encodeHash = function encodeHash() {
  const { hashParams } = window;

  window.location.hash = Object.keys(hashParams)
      .map((key) => `${key}=${encodeURIComponent(hashParams[key])}`)
      .join('&');
};

window.decodeHash = function decodeHash() {
  const hashParams = {};

  window.location.hash.slice(1).split('&')
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

window.dismissTab = function dismissTab(node) {
  const parent = node.parentNode;
  parent.style.display = 'none';
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
            totalCommits: commits.authorFinalContributionMap[author],
            fileTypeContribution: commits.authorFileTypeContributionMap[author],
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
    return this.loadJSON(`${REPORT_DIR}/${folderName}/authorship.json`)
        .then((files) => {
          window.REPOS[repoName].files = files;
          return files;
        });
  },
};
