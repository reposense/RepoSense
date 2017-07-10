package analyzer;

import dataObject.CommitInfo;
import dataObject.Configuration;
import dataObject.RepoInfo;
import git.GitChecker;
import git.GitLogger;

import java.util.ArrayList;

/**
 * Created by matanghao1 on 21/6/17.
 */
public class RepoAnalyzer {


    public static void analyzeRecentNCommit(Configuration config, RepoInfo repo){
        ArrayList<CommitInfo> commits = GitLogger.getCommits(config.getRepoRoot(), config.getCommitNum());
        processCommits(config, commits);
        repo.setCommits(commits);
    }

    public static void analyzeAllCommit(Configuration config, RepoInfo repo){
        ArrayList<CommitInfo> commits = GitLogger.getCommits(config.getRepoRoot());
        processCommits(config, commits);
        repo.setCommits(commits);
    }

    private static void processCommits(Configuration config, ArrayList<CommitInfo> commits){
        for (CommitInfo commitInfo : commits){
            GitChecker.checkOutToCommit(config.getRepoRoot(),commitInfo);
            CommitAnalyzer.aggregateFileInfos(config,commitInfo);
        }
        GitChecker.checkOutToRecentBranch(config.getRepoRoot());
    }
}
