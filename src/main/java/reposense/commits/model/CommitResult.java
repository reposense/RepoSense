package reposense.commits.model;

import java.util.Date;

import reposense.model.Author;

/**
 * Stores the result from analyzing a {@code CommitInfo}.
 */
public class CommitResult {
    private final String hash;
    private final String messageTitle;
    private final String messageBody;
    private final int insertions;
    private final int deletions;

    private final transient Author author;
    private final transient Date time;

    public CommitResult(Author author, String hash, Date time, String messageTitle,
            String messageBody, int insertions, int deletions) {
        this.author = author;
        this.hash = hash;
        this.time = time;
        this.messageTitle = messageTitle;
        this.messageBody = messageBody;
        this.insertions = insertions;
        this.deletions = deletions;
    }

    public String getMessageTitle() {
        return messageTitle;
    }

    public String getMessageBody() {
        return messageBody;
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
                && messageTitle.equals(otherCommitResult.messageTitle)
                && messageBody.equals(otherCommitResult.messageBody)
                && insertions == otherCommitResult.insertions
                && deletions == otherCommitResult.deletions;
    }
}
