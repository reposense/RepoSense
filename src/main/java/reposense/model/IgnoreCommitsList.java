package reposense.model;

import java.util.ArrayList;
import java.util.List;

public class IgnoreCommitsList {
    private static final String COMMIT_HASH_REGEX = "^[0-9a-f]+$";
    private static final String INVALID_COMMIT_HASH_MESSAGE =
            "The provided commit hash, %s, contains illegal characters.";

    private List<String> ignoreCommits = new ArrayList<>();

    public List<String> getIgnoreCommitsList() {
        return ignoreCommits;
    }

    public void setIgnoreCommitsList(List<String> formats) {
        validateIgnoreCommits(formats);
        this.ignoreCommits = formats;
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (this == other) {
            return true;
        }

        // instanceof handles null
        if (!(other instanceof IgnoreCommitsList)) {
            return false;
        }

        IgnoreCommitsList otherList = (IgnoreCommitsList) other;
        return this.ignoreCommits.equals(otherList.ignoreCommits);
    }

    @Override
    public int hashCode() {
        return ignoreCommits.hashCode();
    }

    /**
     * Checks that all the strings in the {@code ignoreCommitList} are in valid formats.
     * @throws IllegalArgumentException if any of the values do not meet the criteria.
     */
    public static void validateIgnoreCommits(List<String> ignoreCommitList) throws IllegalArgumentException {
        for (String commitHash : ignoreCommitList) {
            if (!commitHash.matches(COMMIT_HASH_REGEX)) {
                throw new IllegalArgumentException(String.format(INVALID_COMMIT_HASH_MESSAGE, commitHash));
            }
        }
    }
}
