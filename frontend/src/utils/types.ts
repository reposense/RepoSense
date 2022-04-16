import {z} from "zod";

const url_map = z.object({
    BASE_URL: z.string(),
    BLAME_PATH: z.string(),
    BRANCH: z.string(),
    COMMIT_PATH: z.string(),
    HISTORY_PATH: z.string(),
    REPO_URL: z.string(),
});

export type Url_Shape = z.infer<typeof url_map>

export const summarySchema = z.object({
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
        NOT_RECOGNIZED: z.object(url_map.shape),
        github: z.object(url_map.shape),
    }),
});

export type Summary = z.infer<typeof summarySchema>

export const commitsSchema = z.object({
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

export type CommitsSchema = z.infer<typeof commitsSchema>

export const authorshipSchema = z.array(
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

export type AuthorshipSchema = z.infer<typeof authorshipSchema>

export interface CommitResult {
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

export interface User {
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
}

export interface Repo {
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
    users?: User[]
}