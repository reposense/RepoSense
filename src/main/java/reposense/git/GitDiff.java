package reposense.git;

import static reposense.system.CommandRunner.runCommand;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

/**
 * Contains git diff related functionalities.
 * Git diff is responsible for obtaining the changes between commits, commit and working tree, etc.
 */
public class GitDiff {
    private static final String EMPTY_TREE_HASH = "4b825dc642cb6eb9a060e54bf8d69288fbee4904";
    private static final String CHECKED_OUT_COMMIT_REFERENCE = "HEAD";

    /**
     * Returns the git diff result of the current commit compared to {@code lastCommitHash}, without any context.
     */
    public static String diffCommit(String root, String lastCommitHash) {
        Path rootPath = Paths.get(root);
        return runCommand(rootPath, "git diff -U0 --ignore-submodules=all " + lastCommitHash);
    }

    /**
     * Returns a list of committed files with the corresponding number of lines added and deleted in the repo
     * {@code repoRoot}.
     */
    public static List<String> getModifiedFilesList(Path repoRoot) {
        String diffCommand = String.format("git diff --ignore-submodules=all --numstat %s %s",
                EMPTY_TREE_HASH, CHECKED_OUT_COMMIT_REFERENCE);
        String diffResult = runCommand(repoRoot.toAbsolutePath(), diffCommand);
        return Arrays.asList(diffResult.split("\n"));
    }
}
