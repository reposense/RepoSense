import { z } from 'zod';
import { authorshipSchema } from '../types/authorship-type';
import { commitsSchema } from '../types/commit-type';
import { Summary, summarySchema } from '../types/summary-type';
import { DailyCommit, Repo, User } from '../types/repo-type';
import { UrlShape } from '../types/urlShape-type';

const REPORT_DIR: string = '.';

declare global {
  interface Window {
    api: any;
    REPORT_ZIP: any;
    REPOS: {
      [key:string]: Repo;
    };
    sinceDate: string;
    untilDate: string;
    repoSenseVersion: string;
    isSinceDateProvided: boolean;
    isUntilDateProvided: boolean;
    DOMAIN_URL_MAP: {
      NOT_RECOGNIZED: UrlShape;
      github: UrlShape;
    };
  }
}

window.api = {
  async loadJSON(fname: string) {
    if (window.REPORT_ZIP) {
      const zipObject = window.REPORT_ZIP.file(fname);
      if (zipObject) {
        try {
          return JSON.parse(await zipObject.async('text'));
        } catch (e) {
          throw new Error('Uploaded JSON is invalid.');
        }
      } else {
        throw new Error('Uploaded zip file is invalid.');
      }
    }
    try {
      const response = await fetch(`${REPORT_DIR}/${fname}`);
      return await response.json();
    } catch (e) {
      if (e instanceof z.ZodError) {
        throw e;
      }
      throw new Error(`Unable to read ${fname}.`);
    }
  },
  async loadSummary() {
    window.REPOS = {};
    let data: Summary;

    try {
      const json = await this.loadJSON('summary.json');
      data = summarySchema.parse(json);
    } catch (error: any) {
      if (error.message === 'Unable to read summary.json.') {
        return null;
      }
      throw error;
    }
    const { reportGeneratedTime, reportGenerationTime } = data;
    window.sinceDate = data.sinceDate;
    window.untilDate = data.untilDate;
    window.repoSenseVersion = data.repoSenseVersion;
    window.isSinceDateProvided = data.isSinceDateProvided;
    window.isUntilDateProvided = data.isUntilDateProvided;
    document.title = data.reportTitle || document.title;

    const errorMessages: { [key:string]: { errorMessage: string; repoName: string }; } = {};
    Object.entries(data.errorSet).forEach(([repoName, message]) => {
      errorMessages[repoName] = message;
    });

    window.DOMAIN_URL_MAP = data.supportedDomainUrlMap;

    const names: string[] = [];
    data.repos.forEach((repo) => {
      const repoName = `${repo.displayName}`;
      window.REPOS[repoName] = repo;
      names.push(repoName);
    });
    return {
      creationDate: reportGeneratedTime,
      reportGenerationTime,
      errorMessages,
      names,
    };
  },

  async loadCommits(repoName: string) {
    const folderName = window.REPOS[repoName].outputFolderName;
    const json = await this.loadJSON(`${folderName}/commits.json`);
    const commits = commitsSchema.parse(json);
    const res: User[] = [];
    const repo = window.REPOS[repoName];

    Object.keys(commits.authorDisplayNameMap).forEach((author) => {
      if (author) {
        const obj: User = {
          checkedFileTypeContribution: undefined,
          commits: [],
          dailyCommits: commits.authorDailyContributionsMap[author],
          displayName: commits.authorDisplayNameMap[author],
          fileTypeContribution: commits.authorFileTypeContributionMap[author],
          location: '',
          name: author,
          repoId: repoName,
          repoName: '',
          searchPath: '',
          variance: commits.authorContributionVariance[author],
        };

        this.setContributionOfCommitResultsAndInsertRepoId(obj.dailyCommits, obj.repoId);

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
  },

  async loadAuthorship(repoName: string) {
    const folderName = window.REPOS[repoName].outputFolderName;
    const json = await this.loadJSON(`${folderName}/authorship.json`);
    const result = authorshipSchema.parse(json);
    window.REPOS[repoName].files = result;
    return result;
  },

  // calculate and set the contribution of each commitResult and insert repoId into commitResult, since not provided in json file
  setContributionOfCommitResultsAndInsertRepoId(dailyCommits: DailyCommit[], repoId: string) {
    dailyCommits.forEach((commit) => {
      commit.commitResults.forEach((result) => {
        result.repoId = repoId;
        result.insertions = Object.values(result.fileTypesAndContributionMap)
            .reduce((acc, fileType) => acc + fileType.insertions, 0);
        result.deletions = Object.values(result.fileTypesAndContributionMap)
            .reduce((acc, fileType) => acc + fileType.deletions, 0);
      });
    });
  },
};
