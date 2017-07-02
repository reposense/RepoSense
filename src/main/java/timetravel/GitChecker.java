package timetravel;

import dataObject.CommitInfo;
import system.CommandRunner;

/**
 * Created by matanghao1 on 6/6/17.
 */
public class GitChecker {

    public static void checkOutToRecentBranch(String root){
        checkoutToCommit(root,"-");
    }


    public static void checkOutToCommit(String root, CommitInfo commit){
        System.out.println("Checking out "+commit.getHash()+"time:"+commit.getTime());
        checkoutToCommit(root,commit.getHash());
    }

    private static void checkoutToCommit(String root, String commitHash){
        CommandRunner.checkOut(root,commitHash);
    }
}

