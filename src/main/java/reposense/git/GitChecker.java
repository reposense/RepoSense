package reposense.git;

import java.util.logging.Logger;

import reposense.dataobject.CommitInfo;
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

    public static void checkOutToCommit(String root, CommitInfo commit) {
        logger.info("Checking out " + commit.getHash() + "time:" + commit.getTime());
        checkout(root, commit.getHash());
    }

    public static void checkout(String root, String commitHash) {
        CommandRunner.checkOut(root, commitHash);
    }

}

