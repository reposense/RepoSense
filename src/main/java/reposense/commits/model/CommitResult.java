package reposense.commits.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

import reposense.model.Author;
import reposense.model.FileType;

/**
 * Stores the result from analyzing a {@code CommitInfo}.
 */
public class CommitResult {
    private final String hash;
    private final String messageTitle;
    private final String messageBody;
    private final String[] tags;
    private final Map<FileType, ContributionPair> fileTypesAndContributionMap;

    private final transient Author author;
    private final transient Date time;

    public CommitResult(Author author, String hash, Date time, String messageTitle, String messageBody, String[] tags,
            Map<FileType, ContributionPair> fileTypesAndContributionMap) {
        this.author = author;
        this.hash = hash;
        this.time = time;
        this.messageTitle = messageTitle;
        this.messageBody = messageBody;
        this.tags = tags;
        this.fileTypesAndContributionMap = fileTypesAndContributionMap;
    }

    public CommitResult(Author author, String hash, Date time, String messageTitle, String messageBody, String[] tags) {
        this.author = author;
        this.hash = hash;
        this.time = time;
        this.messageTitle = messageTitle;
        this.messageBody = messageBody;
        this.tags = tags;
        this.fileTypesAndContributionMap = Collections.emptyMap();
    }

    public String getMessageTitle() {
        return messageTitle;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public String[] getTags() {
        return tags;
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
        int insertions = 0;
        for (ContributionPair contributionPair : fileTypesAndContributionMap.values()) {
            insertions += contributionPair.getInsertions();
        }
        return insertions;
    }

    public int getDeletions() {
        int deletions = 0;
        for (ContributionPair contributionPair : fileTypesAndContributionMap.values()) {
            deletions += contributionPair.getDeletions();
        }
        return deletions;
    }

    public Map<FileType, ContributionPair> getFileTypesAndContributionMap() {
        return fileTypesAndContributionMap;
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
                && Arrays.equals(tags, otherCommitResult.tags)
                && fileTypesAndContributionMap.equals(otherCommitResult.fileTypesAndContributionMap);
    }

    @Override
    public int hashCode() {
        return hash.hashCode();
    }
}
