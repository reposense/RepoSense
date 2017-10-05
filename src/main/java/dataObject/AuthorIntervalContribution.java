package dataObject;

/**
 * Created by matanghao1 on 4/10/17.
 */
public class AuthorIntervalContribution {
    private int insertions;
    private int deletions;

    public AuthorIntervalContribution(int insertions, int deletions) {
        this.insertions = insertions;
        this.deletions = deletions;
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

    public void updateForCommit(CommitInfo commit){
        insertions += commit.getInsertions();
        deletions += commit.getDeletions();

    }
}
