import { CommitsSchema } from './commit-type'
import { AuthorshipSchema } from './authorship-type'

export interface Commit {
    commitResults: CommitResult[];
    date: string;
    deletions: number;
    insertions: number;
}

export interface DailyCommit {
    commitResults: CommitResult[];
    date: string;
}

export interface Location {
    domainName: string;
    location: string;
    organization: string;
    repoName: string;
}

export interface User {
    checkedFileTypeContribution?: number;
    commits: Commit[];
    dailyCommits: DailyCommit[];
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
    location: Location;
    outputFolderName: string;
    users?: User[]
}
