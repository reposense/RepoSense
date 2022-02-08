package reposense.git;

import static reposense.system.CommandRunner.runCommand;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import reposense.git.exception.CommitNotFoundException;
import reposense.system.LogsManager;

/**
 * Contains git cat file related functionalities.
 * Git cat file provides content or type and size information for repository objects.
 */
public class GitCatFile {

    private static final Logger logger = LogsManager.getLogger(GitCatFile.class);

    /**
     * Returns parent commits for the commit associated with the input {@code commitHash}.
     *
     * @throws CommitNotFoundException if there is no commit associated with {@code commitHash}.
     */
    public static List<String> getParentCommits(String root, String commitHash) throws CommitNotFoundException {
        Path rootPath = Paths.get(root);
        String catFileCommand = "git cat-file -p " + commitHash;
        try {
            String output = runCommand(rootPath, catFileCommand);
            List<String> parentCommits = new ArrayList<>();
            for (String line : output.split("\n")) {
                if (line.startsWith("parent")) {
                    parentCommits.add(line.substring(7).trim());
                }
            }
            return parentCommits;
        } catch (RuntimeException e) {
            throw new CommitNotFoundException("Commit not found: " + commitHash);
        }
    }

    /**
     * Given the input list of {@code commitHashes}, return a list of commit hashes for the parent
     * commits of all the commits.
     */
    public static List<String> getParentsOfCommits(String root, List<String> commitHashes) {
        List<String> parentCommits = new ArrayList<>();
        for (String commitHash : commitHashes) {
            try {
                parentCommits.addAll(getParentCommits(root, commitHash));
            } catch (CommitNotFoundException e) {
                logger.warning("Invalid commit hash ignored: " + commitHash);
            }
        }
        return parentCommits;
    }
}
