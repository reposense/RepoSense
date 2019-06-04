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
     * Returns the number of lines modified (numLinesAdded - numLinesDeleted) from the commit {@code priorCommitHash}
     *   to the commit {@code lastCommitHash} if the file is not a binary file. Returns {@code null}
     *   if the file is a binary file.
     */
    public static Optional<Integer> getNumLinesModified(Path rootPath, Path relativePath,
                                                        Optional<String> priorCommitHash, Optional<String> lastCommitHash) {
        if (!priorCommitHash.isPresent()) {
            priorCommitHash = Optional.of(EMPTY_COMMIT_HASH);
        }
        if (!lastCommitHash.isPresent()) {
            lastCommitHash = Optional.of(CHECKED_OUT_COMMIT_REFERENCE);
        }

        String message = String.format("git diff --numstat %s %s %s",
                priorCommitHash.get(), lastCommitHash.get(), relativePath);

        String returnMessage = runCommand(rootPath, message);

        returnMessage = returnMessage.replace("\t", " ");
        String[] tokenizedMessage = returnMessage.split(" ");

        if (tokenizedMessage[0].equals("-") && tokenizedMessage[1].equals("-")) {
            return Optional.empty();
        } else {
            return Optional.of(Integer.parseInt(tokenizedMessage[0]) - Integer.parseInt(tokenizedMessage[1]));
        }
    }
}
