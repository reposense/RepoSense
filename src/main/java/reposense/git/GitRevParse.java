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
     * Asserts that the branch in {@code config} exists, with {@code repoRoot} as working directory.
     *
     * @throws GitBranchException when the branch does not exist.
     */
    public static void assertBranchExists(RepoConfiguration config, Path repoRoot) throws GitBranchException {
        String command = String.format("git rev-parse --verify %s", config.getBranch());
        try {
            CommandRunner.runCommand(repoRoot, command);
        } catch (RuntimeException rte) {
            throw new GitBranchException(rte);
        }
    }
}
