package reposense.git;

import static reposense.system.CommandRunner.runCommand;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Contains git diff related functionalities.
 * Git diff is responsible to obtain the changes between commits, commit and working tree, etc.
 */
public class GitDiff {

    /**
     * Returns the git diff result of the current commit compared to {@code lastCommitHash}, without any context.
     */
    public static String diffCommit(String root, String lastCommitHash) {
        Path rootPath = Paths.get(root);
        return runCommand(rootPath, "git diff -U0 " + lastCommitHash);
    }
}
