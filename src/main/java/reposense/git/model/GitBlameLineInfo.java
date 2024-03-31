package reposense.git.model;

/**
 * Stores the git blame info of a single line.
 */
public class GitBlameLineInfo {
    private final String commitHash;
    private final String authorName;
    private final String authorEmail;
    private final long timestampMilliseconds;

    public GitBlameLineInfo(String commitHash, String authorName, String authorEmail, long timestampMilliseconds) {
        this.commitHash = commitHash;
        this.authorName = authorName;
        this.authorEmail = authorEmail;
        this.timestampMilliseconds = timestampMilliseconds;
    }

    public String getCommitHash() {
        return commitHash;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getAuthorEmail() {
        return authorEmail;
    }

    public long getTimestampMilliseconds() {
        return timestampMilliseconds;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof GitBlameLineInfo)) {
            return false;
        }

        GitBlameLineInfo otherLineInfo = (GitBlameLineInfo) other;
        return commitHash.equals(otherLineInfo.commitHash)
                && authorName.equals(otherLineInfo.authorName)
                && authorEmail.equals(otherLineInfo.authorEmail)
                && timestampMilliseconds == otherLineInfo.timestampMilliseconds;
    }
}
