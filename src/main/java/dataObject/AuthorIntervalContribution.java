package dataObject;

import java.util.Date;


public class AuthorIntervalContribution {
    private int insertions;
    private int deletions;
    private Date fromDate;
    private Date toDate;

    public AuthorIntervalContribution(int insertions, int deletions, Date fromDate, Date toDate) {
        this.insertions = insertions;
        this.deletions = deletions;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
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

    public void updateForCommit(CommitInfo commit) {
        insertions += commit.getInsertions();
        deletions += commit.getDeletions();
    }

    public int getTotalContribution() {
        return insertions + deletions;
    }
}
