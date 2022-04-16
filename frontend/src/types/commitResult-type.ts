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
