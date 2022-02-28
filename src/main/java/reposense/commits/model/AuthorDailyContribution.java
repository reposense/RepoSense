package reposense.commits.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Holds the commits made by an {@code Author} for a single day.
 */
public class AuthorDailyContribution {
    private LocalDateTime date;
    private List<CommitResult> commitResults;

    public AuthorDailyContribution(LocalDateTime date) {
        this.date = date;
        commitResults = new ArrayList<>();
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    /**
     * Adds the {@code commitResult} into the {@code Author}'s daily contribution.
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
