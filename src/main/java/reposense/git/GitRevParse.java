package reposense.git;

import java.nio.file.Path;

import reposense.git.exception.GitBranchException;
import reposense.model.RepoConfiguration;
import reposense.system.CommandRunner;

/**
 * Contains git rev parse related functionalities.
 * Git rev parse is responsible for verifying the existence of a particular branch.
 */
public class GitRevParse {
    /**
     * Asserts that the branch in {@code repoConfig} exists.
     * @throws RuntimeException when the branch does not exist.
     */
    public static void assertBranchExists(RepoConfiguration repoConfig, Path bareRepoRoot) throws GitBranchException {
        String command = String.format("git rev-parse --verify %s", repoConfig.getBranch());
        try {
            CommandRunner.runCommand(bareRepoRoot, command);
        } catch (RuntimeException rte) {
            throw new GitBranchException(rte);
        }
    }
}
