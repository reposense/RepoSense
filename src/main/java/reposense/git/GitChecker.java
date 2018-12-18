package reposense.git;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import reposense.commits.model.CommitResult;
import reposense.system.CommandRunner;
import reposense.system.LogsManager;

public class GitChecker {

    private static final Logger logger = LogsManager.getLogger(GitChecker.class);

    public static void checkOutToRecentBranch(String root) throws GitCheckerException {
        checkout(root, "-");
    }

    public static void checkoutBranch(String root, String branch) throws GitCheckerException {
        checkout(root, branch);
    }

    public static void checkOutToCommit(String root, CommitResult commit) throws GitCheckerException {
        logger.info("Checking out " + commit.getHash() + "time:" + commit.getTime());
        checkout(root, commit.getHash());
    }

    public static void checkout(String root, String commitHash) throws GitCheckerException {
        try {
            CommandRunner.checkout(root, commitHash);
        } catch (RuntimeException rte) {
            logger.log(Level.SEVERE, "Error encountered in Git Checkout.", rte);
            throw new GitCheckerException(rte);
        }
    }

    /**
     * Checks out to the latest commit before {@code untilDate} in {@code branchName} branch
     * if {@code untilDate} is not null.
     * @throws CommitNotFoundException if commits before {@code untilDate} cannot be found.
     * @throws GitCheckerException if git checkout command failed to run.
     */
    public static void checkoutToDate(String root, String branchName, Date untilDate)
            throws CommitNotFoundException, GitCheckerException {
        try {
            CommandRunner.checkoutToDate(root, branchName, untilDate);
        } catch (RuntimeException rte) {
            logger.log(Level.SEVERE, "Error encountered in Git Checkout.", rte);
            throw new GitCheckerException(rte);
        }
    }
}

