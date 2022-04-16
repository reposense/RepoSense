import { CommitsSchema } from './commit-type'
import { AuthorshipSchema } from './authorship-type'

export interface User {
    checkedFileTypeContribution?: number;
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
