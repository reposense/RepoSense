package reposense.git;

import reposense.model.RepoConfiguration;
import reposense.system.CommandRunner;

/**
 * Executes git commands to edit the repository's commit information.
 */
public class GitCommitEditer {

    /**
     * Removes all the author names for the commits specified to ignore in {@code config}.
     * For accurate results, the commits should be provided with their full hash and in reverse chronological order.
     */
    public static void removeAuthorFromIgnoredCommits(RepoConfiguration config) {
        config.getIgnoreCommitList().forEach(commitHash ->
                CommandRunner.removeCommitAuthor(config.getRepoRoot(), config.getBranch(), commitHash));
    }
}
