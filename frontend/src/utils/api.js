// utility functions //
window.$ = (id) => document.getElementById(id);
window.enquery = (key, val) => `${key}=${encodeURIComponent(val)}`;
window.REPOSENSE_REPO_URL = 'https://github.com/reposense/RepoSense';
window.HOME_PAGE_URL = 'https://reposense.org';
window.UNSUPPORTED_INDICATOR = 'UNSUPPORTED';
window.DAY_IN_MS = (1000 * 60 * 60 * 24);
window.HASH_DELIMITER = '~';
window.REPOS = {};
window.hashParams = {};
window.isMacintosh = navigator.platform.includes('Mac');
window.REPORT_ZIP = null;

const HASH_ANCHOR = '?';

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

window.filterUnsupported = function filterUnsupported(string) {
  // checks for a pre-defined unsupported tag
  return string.includes(window.UNSUPPORTED_INDICATOR) ? undefined : string;
};

window.getAuthorLink = function getAuthorLink(repoId, author) {
  const domainName = window.REPOS[repoId].location.domainName;
  return window.filterUnsupported(window.DOMAIN_URL_MAP[domainName].BASE_URL + author);
};

window.getRepoLinkUnfiltered = function getRepoLink(repoId) {
  // abstraction for repo link construction. Not supposed to be used by other files
  const domainName = window.REPOS[repoId].location.domainName;
  return window.DOMAIN_URL_MAP[domainName].REPO_URL
      .replace('$ORGANIZATION', window.REPOS[repoId].location.organization)
      .replace('$REPO_NAME', window.REPOS[repoId].location.repoName);
};

window.getRepoLink = function getRepoLink(repoId) {
  return window.filterUnsupported(window.getRepoLinkUnfiltered(repoId));
};

window.getBranchLink = function getBranchLink(repoId, branch) {
  const domainName = window.REPOS[repoId].location.domainName;
  return window.filterUnsupported(window.getRepoLinkUnfiltered(repoId) + window.DOMAIN_URL_MAP[domainName].BRANCH
      .replace('$BRANCH', branch));
};

window.getCommitLink = function getCommitLink(repoId, commitHash) {
  const domainName = window.REPOS[repoId].location.domainName;
  return window.filterUnsupported(window.getRepoLinkUnfiltered(repoId) + window.DOMAIN_URL_MAP[domainName].COMMIT_PATH
      .replace('$COMMIT_HASH', commitHash));
};

window.getBlameLink = function getBlameLink(repoId, branch, filepath) {
  const domainName = window.REPOS[repoId].location.domainName;
  return window.filterUnsupported(window.getRepoLinkUnfiltered(repoId) + window.DOMAIN_URL_MAP[domainName].BLAME_PATH
      .replace('$BRANCH', branch)
      .replace('$FILE_PATH', filepath));
};

window.getHistoryLink = function getHistoryLink(repoId, branch, filepath) {
  const domainName = window.REPOS[repoId].location.domainName;
  return window.filterUnsupported(window.getRepoLinkUnfiltered(repoId) + window.DOMAIN_URL_MAP[domainName].HISTORY_PATH
      .replace('$BRANCH', branch)
      .replace('$FILE_PATH', filepath));
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

window.getAuthorDisplayName = function getAuthorDisplayName(authorRepos) {
  return authorRepos.reduce((displayName, user) => (
    user.displayName > displayName ? user.displayName : displayName
  ), authorRepos[0].displayName);
};

export default 'test';
