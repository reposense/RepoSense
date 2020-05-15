package reposense.commits.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Holds the commits made and line contribution count of an {@code Author} for a single day.
 */
public class AuthorDailyContribution {
    private Date date;
    private List<CommitResult> commitResults;

    public AuthorDailyContribution(Date date) {
        this.date = date;
        commitResults = new ArrayList<>();
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * Adds the {@code commitResult} line contribution count into the {@code Author}'s total line contribution count
     * for the day.
     */
    public void addCommitContribution(CommitResult commitResult) {
        commitResults.add(commitResult);
    }

    /**
     * Returns the total line contribution made by the {@code Author} for the day.
     */
    public int getTotalContribution() {
        int totalContribution = 0;
        for (CommitResult commitResult : commitResults) {
            totalContribution += commitResult.getDeletions();
            totalContribution += commitResult.getInsertions();
        }
        return totalContribution;
    }
}
