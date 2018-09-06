package reposense.commits.model;

import java.util.Date;

import reposense.model.Author;

/**
 * Stores the result from analyzing a {@code CommitInfo}.
 */
public class CommitResult {
    private final Author author;
    private final String hash;
    private final Date time;
    private final String message;
    private final int insertions;
    private final int deletions;

    public CommitResult(Author author, String hash, Date time, String message, int insertions, int deletions) {
        this.author = author;
        this.hash = hash;
        this.time = time;
        this.message = message;
        this.insertions = insertions;
        this.deletions = deletions;
    }

    public String getMessage() {
        return message;
    }

    public Author getAuthor() {
        return author;
    }

    public String getHash() {
        return hash;
    }

    public Date getTime() {
        return time;
    }

    public int getInsertions() {
        return insertions;
    }

    public int getDeletions() {
        return deletions;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof CommitResult)) {
            return false;
        }

        CommitResult otherCommitResult = (CommitResult) other;
        return author.equals(otherCommitResult.author)
                && hash.equals(otherCommitResult.hash)
                && time.equals(otherCommitResult.time)
                && message.equals(otherCommitResult.message)
                && insertions == otherCommitResult.insertions
                && deletions == otherCommitResult.deletions;
    }
}
