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

const zeroPad = (num, places) => String(num).padStart(places, '0');

export function timezoneToStr(hours, useColon) {
  const sign = hours >= 0 ? '+' : '-';
  hours = Math.abs(hours);
  const roundedHours = Math.floor(hours);
  const minutes = (hours - roundedHours) * 60;
  const colon = useColon ? ':' : '';
  return `${sign}${zeroPad(roundedHours, 2)}${colon}${zeroPad(minutes, 2)}`;
}

function formatDateStr(dateStr) {
  if (!dateStr) {
    return '';
  }
  const [year, month, day] = dateStr.split('-');
  return `${day}/${month}/${year}`;
}

function generateRunSh(since, until, timezone) {
  let sinceStr;
  if (since) {
    sinceStr = formatDateStr(since);
  } else {
    sinceStr = 'd1';
  }

  let untilStr = '';
  if (until) {
    untilStr = `-u ${formatDateStr(until)}`;
  }

  return `#!/bin/bash

# Downloads a specific version of RepoSense.jar of your choice from our repository
## Examples of supported options:
### ./get-reposense.py --release               # Gets the latest release (Stable)
### ./get-reposense.py --master                # Gets the latest master  (Beta)
### ./get-reposense.py --tag v1.6.1            # Gets a specific version
### ./get-reposense.py --release --overwrite   # Overwrite RepoSense.jar, if exists, with the latest release

./get-reposense.py --master

# Executes RepoSense
# Do not change the default output folder name (reposense-report)
## Examples of other valid options; For more, please view the user guide
### java -jar RepoSense.jar --repos https://github.com/reposense/RepoSense.git

java -jar RepoSense.jar --config ./configs -s ${sinceStr} -t UTC${timezoneToStr(timezone)} ${untilStr}`;
}

function matrixToCsvString(matrix) {
  const strArr = matrix.map((r) => r.join(','));
  return strArr.join('\n');
}

export async function generateReport(data, store) {
  const {
    repos,
    since,
    until,
    timezone,
  } = data;
  for (let i = 0; i < repos.length; i += 1) {
    if (repos[i].url.substring(0, 19) !== 'https://github.com/') {
      return 'Repository names should start with: https://github.com/';
    }
  }
  const repoExists = await store.dispatch('repoExists');
  if (!repoExists) {
    await store.dispatch('forkReposense');
    // await store.dispatch('addSecret', store.state.githubStore.accessToken);
    await store.dispatch('enableGithubActions');
    store.dispatch('enableGithubPages');
  }
  const repoConfigArr = generateRepoConfigHeader();
  repos.forEach((repo) => repoConfigArr.push([`${repo.url}.git`, repo.branch, '', '', '', '', '']));
  const repoConfig = matrixToCsvString(repoConfigArr);
  const runsh = generateRunSh(since, until, timezone);
  try {
    await store.dispatch('updateFile', {
      path: 'configs/repo-config.csv',
      strContent: repoConfig,
    });
    await store.dispatch('updateFile', {
      path: 'run.sh',
      strContent: runsh,
    });
  } catch {
    return 'Invalid permissions given';
  }
  const { loginUser } = store.state.githubStore;
  return `Success! Your report will be generated at https://${loginUser}.github.io/publish-RepoSense/`;
}
