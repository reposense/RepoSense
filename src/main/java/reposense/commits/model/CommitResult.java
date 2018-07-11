package reposense.commits.model;

import java.util.Date;

import reposense.model.Author;

/**
 * Stores the result from analyzing a {@code CommitInfo}.
 */
public class CommitResult {
    private final String hash;
    private final String message;
    private final int insertions;
    private final int deletions;

    private final transient Author author;
    private final transient Date time;

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
}
