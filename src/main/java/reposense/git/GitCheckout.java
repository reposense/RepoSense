package reposense.git;

import static reposense.system.CommandRunner.runCommand;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.logging.Logger;

import reposense.commits.model.CommitResult;
import reposense.git.exception.CommitNotFoundException;
import reposense.system.LogsManager;

/**
 * Contains git checkout related functionalities.
 * Git branch is responsible for switch branches, revision or restore working tree files.
 */
public class GitCheckout {

    private static final Logger logger = LogsManager.getLogger(GitCheckout.class);

    public static void checkoutRecentBranch(String root) {
        checkout(root, "-");
    }

    public static void checkoutBranch(String root, String branch) {
        checkout(root, branch);
    }

    /**
     * Checkouts to the hash revision given in the {@code commit}.
     */
    public static void checkoutCommit(String root, CommitResult commit) {
        logger.info("Checking out " + commit.getHash() + "time:" + commit.getTime());
        checkout(root, commit.getHash());
    }

    /**
     * Checkouts to the given {@code hash} revision.
     */
    public static void checkout(String root, String hash) {
        Path rootPath = Paths.get(root);
        runCommand(rootPath, "git checkout " + hash);
    }

    /**
     * Checks out to the latest commit before {@code untilDate} in {@code branchName} branch
     * if {@code untilDate} is not null.
     * @throws CommitNotFoundException if commits before {@code untilDate} cannot be found.
     */
    public static void checkoutDate(String root, String branchName, LocalDateTime untilDate, ZoneId zoneId)
            throws CommitNotFoundException {
        if (untilDate == null) {
            return;
        }

        String hash = GitRevList.getCommitHashUntilDate(root, branchName, untilDate, zoneId);
        if (hash.isEmpty()) {
            throw new CommitNotFoundException("Commit before until date is not found.");
        }

        Path rootPath = Paths.get(root);
        String checkoutCommand = "git checkout " + hash;
        runCommand(rootPath, checkoutCommand);
    }
}
