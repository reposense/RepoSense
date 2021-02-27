const GITHUB_OAUTH_URL = 'https://github.com/login/oauth/authorize';
const CLIENT_ID = 'c498493d4c565ced8d0b';

export function oAuthAuthenticate() {
  const queries = {
    client_id: CLIENT_ID,
    scope: 'public_repo',
  };
  const queryString = new URLSearchParams(queries).toString();
  window.location = `${GITHUB_OAUTH_URL}?${queryString}`;
}

function generateRepoConfigHeader() {
  return [[
      "Repository's Location",
      'Branch',
      'File formats',
      'Ignore Glob List',
      'Ignore standalone config',
      'Ignore Commits List',
      'Ignore Authors List',
  ]];
}

function matrixToCsvString(matrix) {
  const strArr = matrix.map((r) => r.join(','));
  return strArr.join('\n');
}

export async function generateReport(data, store) {
  const { repos } = data;
  if (repos.length === 0) {
    return 'Please input at least 1 repository';
  }
  // githubApi.setPat(pat);
  // try {
  //   await githubApi.authenticate();
  // } catch {
  //   return 'Invalid personal access token';
  // }
  const repoExists = await store.dispatch('repoExists');
  if (!repoExists) {
    await store.dispatch('forkReposense');
    // await store.dispatch('addSecret');
    // await store.dispatch('enableGithubActions');
  }
  const repoConfigArr = generateRepoConfigHeader();
  repos.forEach((repo) => repoConfigArr.push([repo.url, repo.branch, '', '', '', '', '']));
  const repoConfig = matrixToCsvString(repoConfigArr);
  try {
    await store.dispatch('updateFile', {
      path: 'configs/repo-config.csv',
      strContent: repoConfig,
    });
  } catch {
    return 'Invalid permissions given';
  }
  return 'Success!';
}
