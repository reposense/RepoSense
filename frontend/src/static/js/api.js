/* util funcs */
window.$ = id => document.getElementById(id);
window.enquery = (key, val) => `${key}=${encodeURIComponent(val)}`;

const REPORT_DIR = '';

/* api funcs */
function loadJSON(file, fn) {
  const err = () => window.alert('unable to get file');

  if (window.REPORT_ZIP) {
    window.REPORT_ZIP.file(file.slice(1)).async('text')
      .then(txt => fn(JSON.parse(txt)));
  } else {
    const xhr = new XMLHttpRequest();
    xhr.open('GET', file);
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

    loadJSON(`${REPORT_DIR}/summary.json`, (repos) => {
      const names = [];
      repos.forEach((repo) => {
        const repoName = `${repo.organization}_${repo.repoName}`;
        window.REPOS[repoName] = repo;
        names.push(repoName);
      });

      if (callback) {
        callback(names);
      }
    });
  },

  loadCommits(repoName, callback) {
    loadJSON(`${REPORT_DIR}/${repoName}/commits.json`, (data) => {
      const repo = window.REPOS[repoName];

      const commits = {};
      const rawCommits = data.commitResults;

      const msDay = 1000 * 60 * 60 * 24;
      const getTime = txt => new Date(txt).getTime();
      const getDate = num => new Date(num).toISOString().split('T')[0];

      const sinceDate = rawCommits[0].time;
      const untilDate = rawCommits.slice(-1)[0].time;
      const dateRange = (getTime(untilDate) - getTime(sinceDate)) / msDay;
      const getDiff = tstamp => (getTime(tstamp) - getTime(sinceDate)) / msDay;

      const defaultCommits = [];
      for (let day = 0; day <= dateRange; day += 1) {
        defaultCommits.push({
          raw: [],
          deletions: 0,
          insertions: 0,
          sinceDate: getDate(getTime(sinceDate) + (day * msDay)),
          untilDate: getDate(getTime(sinceDate) + ((day + 1) * msDay)),
        });
      }

      data.commitResults.forEach((commit) => {
        const uid = commit.author.gitId;
        const { time } = commit;
        if (!commits[uid]) {
          commits[uid] = JSON.parse(JSON.stringify(defaultCommits));
        }

        const curr = commits[uid][getDiff(time)];
        curr.deletions += commit.deletions;
        curr.insertions += commit.insertions;
        curr.raw.push(commit);
      });

      const res = [];
      Object.keys(data.authorDisplayNameMap).forEach((author) => {
        if (author) {
          const obj = {
            name: author,
            repoId: repoName,
            variance: data.authorContributionVariance[author],
            displayName: data.authorDisplayNameMap[author],
            dailyCommits: commits[author],
            totalCommits: data.authorFinalContributionMap[author],
          };

          const searchParams = [
            repo.organization, repo.repoName,
            obj.displayName, author,
          ];

          obj.searchPath = searchParams.join('/').toLowerCase();
          obj.repoPath = `${repo.organization}/${repo.repoName}`;
          obj.repoName = `${repo.organization}_${repo.repoName}`;

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
