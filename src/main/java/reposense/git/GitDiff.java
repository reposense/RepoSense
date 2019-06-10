package reposense.git;

import static reposense.system.CommandRunner.runCommand;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * Contains git diff related functionalities.
 * Git diff is responsible for obtaining the changes between commits, commit and working tree, etc.
 */
public class GitDiff {
    public static final String EMPTY_COMMIT_HASH = "4b825dc642cb6eb9a060e54bf8d69288fbee4904";
    public static final String CHECKED_OUT_COMMIT_REFERENCE = "HEAD";

    /**
     * Returns the git diff result of the current commit compared to {@code lastCommitHash}, without any context.
     */
    public static String diffCommit(String root, String lastCommitHash) {
        Path rootPath = Paths.get(root);
        return runCommand(rootPath, "git diff -U0 " + lastCommitHash);
    }

    /**
     * Returns the raw message of the number of lines modified of a given file {@code relativePath} form the first
     * empty commit to the checked out commit.
     */
    public static String diffNumLinesModified(Path rootPath, String relativePath) {
        String message = String.format("git diff --numstat %s %s -- \"%s\"", EMPTY_COMMIT_HASH,
                CHECKED_OUT_COMMIT_REFERENCE, relativePath);
        return runCommand(rootPath, message);
    }
}
