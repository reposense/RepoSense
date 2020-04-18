package reposense.commits.model;

/**
 * Stores the insertions and deletions as a pair.
 */
public class ContributionPair {
    private int insertions;
    private int deletions;

    public ContributionPair() {
    }

    public ContributionPair(int insertions, int deletions) {
        this.insertions = insertions;
        this.deletions = deletions;
    }

    public int getInsertions() {
        return insertions;
    }

    public int getDeletions() {
        return deletions;
    }

    public void addInsertions(int insertions) {
        this.insertions += insertions;
    }

    public void addDeletions(int deletions) {
        this.deletions += deletions;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof ContributionPair)) {
            return false;
        }

        ContributionPair otherContributionPair = (ContributionPair) other;
        return insertions == otherContributionPair.insertions && deletions == otherContributionPair.deletions;
    }
}
