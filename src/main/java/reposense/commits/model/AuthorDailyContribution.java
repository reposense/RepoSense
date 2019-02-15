package reposense.commits.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Holds the commits made and line contribution count of an {@code Author} for a single day.
 */
public class AuthorDailyContribution {
    private int insertions;
    private int deletions;
    private Date date;
    private List<CommitResult> commitResults;

    public AuthorDailyContribution(Date date) {
        this.date = date;

        insertions = 0;
        deletions = 0;
        commitResults = new ArrayList<>();
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getInsertions() {
        return insertions;
    }

    public void setInsertions(int insertions) {
        this.insertions = insertions;
    }

    public int getDeletions() {
        return deletions;
    }

    public void setDeletions(int deletions) {
        this.deletions = deletions;
    }

    /**
     * Adds the {@code commitResult} line contribution count into the {@code Author}'s total line contribution count
     * for the day.
     */
    public void addCommitContribution(CommitResult commitResult) {
        insertions += commitResult.getInsertions();
        deletions += commitResult.getDeletions();
        commitResults.add(commitResult);
    }

    /**
     * Returns the total line contribution made by the {@code Author} for the day.
     */
    public int getTotalContribution() {
        return insertions + deletions;
    }
}
