package analyzer;

import dataObject.CommitInfo;
import dataObject.FileInfo;
import timetravel.GitChecker;
import timetravel.GitLogger;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by matanghao1 on 21/6/17.
 */
public class RepoAnalyzer {


    public static ArrayList<CommitInfo> analyzeRecentNCommit(String repoRoot, int recent){
        ArrayList<CommitInfo> commits = GitLogger.getCommits(repoRoot, recent);
        processCommits(repoRoot, commits);
        return commits;
    }

    public static ArrayList<CommitInfo> analyzeAllCommit(String repoRoot){
        ArrayList<CommitInfo> commits = GitLogger.getCommits(repoRoot);
        processCommits(repoRoot, commits);
        return commits;
    }

    private static void processCommits(String repoRoot, ArrayList<CommitInfo> commits){
        for (CommitInfo commitInfo : commits){
            GitChecker.checkOutToCommit(repoRoot,commitInfo);
            CommitAnalyzer.aggregateFileInfos(repoRoot,commitInfo);
        }
        GitChecker.checkOutToRecentBranch(repoRoot);
    }
}
