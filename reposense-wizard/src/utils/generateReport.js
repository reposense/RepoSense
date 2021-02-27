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

async function generateReport(data, githubApi) {
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
  const repoExists = await githubApi.repoExists();
  if (!repoExists) {
    await githubApi.forkReposense();
    await githubApi.addSecret();
    await githubApi.enableGithubActions();
  }
  const repoConfigArr = generateRepoConfigHeader();
  repos.forEach((repo) => repoConfigArr.push([repo.url, repo.branch, '', '', '', '', '']));
  const repoConfig = matrixToCsvString(repoConfigArr);
  try {
    await githubApi.updateFile('configs/repo-config.csv', repoConfig);
  } catch {
    return 'Invalid permissions given';
  }
  return 'Success!';
}

export default generateReport;
