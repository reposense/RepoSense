package reposense.git;

import reposense.dataObject.CommitInfo;
import reposense.system.CommandRunner;


public class GitChecker {

    public static void checkOutToRecentBranch(String root) {
        checkout(root, "-");
    }

    public static void checkoutBranch(String root, String branch) {
        checkout(root, branch);
    }

    public static void checkOutToCommit(String root, CommitInfo commit) {
        System.out.println("Checking out " + commit.getHash() + "time:" + commit.getTime());
        checkout(root, commit.getHash());
    }

    public static void checkout(String root, String commitHash) {
        CommandRunner.checkOut(root, commitHash);
    }

}

