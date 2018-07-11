package reposense.commits.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


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

    public void addCommitContribution(CommitResult commitResult) {
        insertions += commitResult.getInsertions();
        deletions += commitResult.getDeletions();
        commitResults.add(commitResult);
    }

    public int getTotalContribution() {
        return insertions + deletions;
    }
}
