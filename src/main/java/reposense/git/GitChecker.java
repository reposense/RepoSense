package reposense.git;

import java.util.Date;
import java.util.logging.Logger;

import reposense.commits.model.CommitResult;
import reposense.system.CommandRunner;
import reposense.system.LogsManager;

public class GitChecker {

    private static final Logger logger = LogsManager.getLogger(GitChecker.class);

    public static void checkOutToRecentBranch(String root) {
        checkout(root, "-");
    }

    public static void checkoutBranch(String root, String branch) {
        checkout(root, branch);
    }

    public static void checkOutToCommit(String root, CommitResult commit) {
        logger.info("Checking out " + commit.getHash() + "time:" + commit.getTime());
        checkout(root, commit.getHash());
    }

    public static void checkout(String root, String commitHash) {
        CommandRunner.checkout(root, commitHash);
    }

    /**
     * Checks out to the latest commit before {@code untilDate} in {@code branchName} branch
     * if {@code untilDate} is not null.
     */
    public static void checkoutToDate(String root, String branchName, Date untilDate) {
        CommandRunner.checkoutToDate(root, branchName, untilDate);
    }
}

