package reposense.commits.model;

import java.io.File;

public class FileChangeStats {
    private final int numFilesChanged;
    private final int totalInsertions;
    private final int totalDeletions;

    public FileChangeStats(int numFilesChanged, int totalInsertions, int totalDeletions) {
        this.numFilesChanged = numFilesChanged;
        this.totalInsertions = totalInsertions;
        this.totalDeletions = totalDeletions;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof ContributionPair)) {
            return false;
        }

        FileChangeStats otherFileChangeStats = (FileChangeStats) other;
        return numFilesChanged == otherFileChangeStats.numFilesChanged &&
                totalInsertions == otherFileChangeStats.totalInsertions &&
                totalDeletions == otherFileChangeStats.totalDeletions;
    }
}
