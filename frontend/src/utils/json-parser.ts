import { z } from 'zod';

const REPORT_DIR: string = '.';

const urlShape = z.object({
  BASE_URL: z.string(),
  BLAME_PATH: z.string(),
  BRANCH: z.string(),
  COMMIT_PATH: z.string(),
  HISTORY_PATH: z.string(),
  REPO_URL: z.string(),
});

type UrlShape = z.infer<typeof urlShape>

const summarySchema = z.object({
  repoSenseVersion: z.string(),
  reportGeneratedTime: z.string(),
  reportGenerationTime: z.string(),
  zoneId: z.string(),
  reportTitle: z.string(),
  repos: z.array(
      z.object({
        location: z.object({
          domainName: z.string(),
          location: z.string(),
          repoName: z.string(),
          organization: z.string(),
        }),
        branch: z.string(),
        displayName: z.string(),
        outputFolderName: z.string(),
      }),
  ),
  errorSet: z.array(
      z.object({ repoName: z.string(), errorMessage: z.string() }),
  ),
  sinceDate: z.string(),
  untilDate: z.string(),
  isSinceDateProvided: z.boolean(),
  isUntilDateProvided: z.boolean(),
  supportedDomainUrlMap: z.object({
    NOT_RECOGNIZED: z.object(urlShape.shape),
    github: z.object(urlShape.shape),
  }),
});

type Summary = z.infer<typeof summarySchema>

const commitsSchema = z.object({
  authorFileTypeContributionMap: z.record(z.record(z.number())),
  authorDailyContributionsMap: z.record(z.array(
      z.object({
        date: z.string(),
        commitResults: z.array(
            z.object({
              deletions: z.number().optional(),
              fileTypesAndContributionMap: z.record(z.object({
                deletions: z.number(), insertions: z.number(),
              })),
              hash: z.string(),
              insertions: z.number().optional(),
              messageBody: z.string(),
              messageTitle: z.string(),
              repoId: z.string().optional(),
            }),
        ),
      }),
  )),
  authorContributionVariance: z.record(z.number()),
  authorDisplayNameMap: z.record(z.string()),
});

type CommitsSchema = z.infer<typeof commitsSchema>

const authorshipSchema = z.array(
    z.object({
      path: z.string(),
      fileType: z.string(),
      lines: z.array(
          z.object({
            lineNumber: z.number(),
            author: z.record(z.string()),
            content: z.string(),
          }),
      ),
      authorContributionMap: z.record(z.number()),
    }),
);

type AuthorshipSchema = z.infer<typeof authorshipSchema>

interface CommitResult {
  deletions?: number;
  fileTypesAndContributionMap: {
    [key:string]: {
      deletions: number;
      insertions: number;
    }
  };
  hash: string;
  insertions?: number;
  messageBody: string;
  messageTitle: string;
  repoId?: string;
}

declare global {
  interface Window {
    api: any;
    REPORT_ZIP: any;
    REPOS: {
      [key:string]: {
        branch: string;
        commits?: CommitsSchema;
        displayName: string;
        files?: AuthorshipSchema;
        location: {
          domainName: string;
          location: string;
          organization: string;
          repoName: string;
        };
        outputFolderName: string;
        users?: {
          checkedFileTypeContribution: number;
          commits: {
            commitResults: CommitResult[];
            date: string;
            deletions: number;
            insertions: number;
          }[];
          dailyCommits: {
            commitResults: CommitResult[];
            date: string;
          }[];
          displayName: string;
          fileTypeContribution: {
            [key:string]: number;
          };
          location: string;
          name: string;
          repoId: string;
          repoName: string;
          searchPath: string;
          variance: number;
        }[]
      };
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
  async loadJSON(fname: string, type: string) {
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
      // Not directly returned in case response is not actually json.
      let json = await response.json();
      if (type === 'summary') {
        json = summarySchema.parse(json);
      } else if (type === 'commits') {
        json = commitsSchema.parse(json);
      } else if (type === 'authorship') {
        json = authorshipSchema.parse(json);
      }
      return json;
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
      data = await this.loadJSON('summary.json', 'summary');
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

    const errorMessages: { [key:string]: {errorMessage: string; repoName: string}; } = {};
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
    const commits = await this.loadJSON(`${folderName}/commits.json`, 'commits');
    const res: any[] = [];
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
          searchPath: '',
          repoName: '',
          location: '',
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

  loadAuthorship(repoName: string) {
    const folderName = window.REPOS[repoName].outputFolderName;
    return this.loadJSON(`${folderName}/authorship.json`, 'authorship')
        .then((files: AuthorshipSchema) => {
          window.REPOS[repoName].files = files;
          return files;
        });
  },

  // calculate and set the contribution of each commitResult and insert repoId into commitResult, since not provided in json file
  setContributionOfCommitResultsAndInsertRepoId(dailyCommits: { date: string; commitResults: { deletions: number;
    fileTypesAndContributionMap: Map<string, {insertions: number; deletions: number}>; hash: string; insertions: number;
    repoId: string; messageBody: string; messageTitle: string; }[] }[], repoId: string) {
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
