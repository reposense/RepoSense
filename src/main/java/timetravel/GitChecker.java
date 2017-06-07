package timetravel;

import data.CommitInfo;
import system.CommandRunner;

/**
 * Created by matanghao1 on 6/6/17.
 */
public class GitChecker {
    public static void checkOutToCommit(String root, CommitInfo commit){
        System.out.println("Checking out "+commit.getHash()+"time:"+commit.getTime());
        CommandRunner.checkOut(root,commit.getHash());
    }
}
