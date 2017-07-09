package analyzer;

import dataObject.CommitInfo;
import dataObject.RepoInfo;
import git.GitChecker;
import git.GitLogger;

import java.util.ArrayList;

/**
 * Created by matanghao1 on 21/6/17.
 */
public class RepoAnalyzer {


    public static void analyzeRecentNCommit(String repoRoot, RepoInfo repo, int recent){
        ArrayList<CommitInfo> commits = GitLogger.getCommits(repoRoot, recent);
        processCommits(repoRoot, commits);
        repo.setCommits(commits);
    }

    public static void analyzeAllCommit(String repoRoot, RepoInfo repo){
        ArrayList<CommitInfo> commits = GitLogger.getCommits(repoRoot);
        processCommits(repoRoot, commits);
        repo.setCommits(commits);
    }

    private static void processCommits(String repoRoot, ArrayList<CommitInfo> commits){
        for (CommitInfo commitInfo : commits){
            GitChecker.checkOutToCommit(repoRoot,commitInfo);
            CommitAnalyzer.aggregateFileInfos(repoRoot,commitInfo);
        }
        GitChecker.checkOutToRecentBranch(repoRoot);
    }
}
