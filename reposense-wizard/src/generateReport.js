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
  githubApi.setPat(pat);
  await githubApi.authenticate();
  await githubApi.forkReposense();
  githubApi.addSecret();
  const repoConfigArr = generateRepoConfigHeader();
  repos.forEach((repo) => repoConfigArr.push([repo.url, repo.branch, '', '', '', '', '']));
  const repoConfig = matrixToCsvString(repoConfigArr);
  githubApi.updateFile('configs/repo-config.csv', repoConfig);
}

export default generateReport;
