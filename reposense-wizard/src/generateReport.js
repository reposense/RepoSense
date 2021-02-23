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
  const { pat, repos } = data;
  if (repos.length === 0) {
    return 'Please input at least 1 repository';
  }
  githubApi.setPat(pat);
  try {
    await githubApi.authenticate();
  } catch {
    return 'Invalid personal access token';
  }
  await githubApi.forkReposense();
  githubApi.addSecret();
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
