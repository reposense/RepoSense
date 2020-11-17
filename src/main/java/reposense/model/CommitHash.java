package reposense.model;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import reposense.git.GitRevList;

/**
 * Represents a git commit hash in {@code RepoConfiguration}.
 */
public class CommitHash {
    private static final String COMMIT_HASH_REGEX = "^[0-9a-f]+$";
    private static final String COMMIT_RANGED_HASH_REGEX = "^[0-9a-f]+\\.\\.[0-9a-f]+$";
    private static final String INVALID_COMMIT_HASH_MESSAGE =
            "The provided commit hash, %s, contains illegal characters.";

    private String commit;

    public CommitHash(String commit) {
        validateCommit(commit);
        this.commit = commit;
    }

    @Override
    public String toString() {
        return commit;
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (this == other) {
            return true;
        }

        // instanceof handles null
        if (!(other instanceof CommitHash)) {
            return false;
        }

        CommitHash otherCommit = (CommitHash) other;
        return this.commit.equals(otherCommit.commit);
    }

    @Override
    public int hashCode() {
        return commit.hashCode();
    }

    /**
     * Converts all the strings in {@code commits} into {@code CommitHash} objects.
     * Returns null if {@code commits} is null.
     * @throws IllegalArgumentException if any of the strings are in invalid formats.
     */
    public static List<CommitHash> convertStringsToCommits(List<String> commits) throws IllegalArgumentException {
        if (commits == null) {
            return null;
        }

        return commits.stream()
                .map(CommitHash::new)
                .collect(Collectors.toList());
    }

    /**
     * Converts a commit {@code entry} into either itself, or a stream of CommitHashes if a range was provided.
     */
    public static Stream<CommitHash> getHashes(String root, String branchName, CommitHash entry) {
        if (entry.toString().matches(COMMIT_HASH_REGEX)) {
            return Stream.of(entry);
        }

        String[] startAndEnd = entry.toString().split("\\.\\.");
        String revList = GitRevList.getCommitHashInRange(root, branchName, startAndEnd[0], startAndEnd[1]);
        return Arrays.stream(revList.split("\n"))
                .map(CommitHash::new);
    }

    /**
     * Checks if {@code commitList} contains {@code commitHash}
     */
    public static boolean isInsideCommitList(String commitHash, List<CommitHash> commitList) {
        return commitList.stream().map(CommitHash::toString).anyMatch(commitHash::startsWith);
    }

    /**
     * Checks that all the strings in the {@code ignoreCommitList} are in valid formats.
     * @throws IllegalArgumentException if any of the values do not meet the criteria.
     */
    public static void validateCommits(List<String> commits) throws IllegalArgumentException {
        for (String commitHash : commits) {
            validateCommit(commitHash);
        }
    }

    /**
     * Checks that {@code commitHash} is in a valid format.
     * @throws IllegalArgumentException if {@code commitHash} does not meet the criteria.
     */
    private static void validateCommit(String commitHash) throws IllegalArgumentException {
        if (!commitHash.matches(COMMIT_HASH_REGEX) && !commitHash.matches(COMMIT_RANGED_HASH_REGEX)) {
            throw new IllegalArgumentException(String.format(INVALID_COMMIT_HASH_MESSAGE, commitHash));
        }
    }
}

