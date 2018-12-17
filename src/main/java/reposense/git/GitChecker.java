package reposense.git;

import static reposense.system.CommandRunner.runCommand;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.logging.Logger;

import reposense.commits.model.CommitResult;
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

    public static void checkout(String root, String hash) {
        Path rootPath = Paths.get(root);
        runCommand(rootPath, "git checkout " + hash);
    }

    /**
     * Checks out to the latest commit before {@code untilDate} in {@code branchName} branch
     * if {@code untilDate} is not null.
     * @throws CommitNotFoundException if commits before {@code untilDate} cannot be found.
     */
    public static void checkoutToDate(String root, String branchName, Date untilDate) throws CommitNotFoundException {
        if (untilDate == null) {
            return;
        }

        Path rootPath = Paths.get(root);

        String substituteCommand = "git rev-list -1 --before="
                + Util.GIT_LOG_UNTIL_DATE_FORMAT.format(untilDate) + " " + branchName;
        String hash = runCommand(rootPath, substituteCommand);
        if (hash.isEmpty()) {
            throw new CommitNotFoundException("Commit before until date is not found.");
        }
        String checkoutCommand = "git checkout " + hash;
        runCommand(rootPath, checkoutCommand);
    }
}

