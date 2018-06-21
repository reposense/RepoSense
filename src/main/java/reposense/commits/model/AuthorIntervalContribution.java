package reposense.commits.model;

import java.util.Date;


public class AuthorIntervalContribution {
    private int insertions;
    private int deletions;
    private Date sinceDate;
    private Date untilDate;

    public AuthorIntervalContribution(int insertions, int deletions, Date sinceDate, Date untilDate) {
        this.insertions = insertions;
        this.deletions = deletions;
        this.sinceDate = sinceDate;
        this.untilDate = untilDate;
    }

    public Date getSinceDate() {
        return sinceDate;
    }

    public void setSinceDate(Date sinceDate) {
        this.sinceDate = sinceDate;
    }

    public Date getUntilDate() {
        return untilDate;
    }

    public void setUntilDate(Date untilDate) {
        this.untilDate = untilDate;
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

    public void updateForCommit(CommitResult commit) {
        insertions += commit.getInsertions();
        deletions += commit.getDeletions();
    }

    public int getTotalContribution() {
        return insertions + deletions;
    }
}
