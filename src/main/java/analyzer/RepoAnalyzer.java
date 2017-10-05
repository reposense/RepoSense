package analyzer;

import dataObject.Author;
import dataObject.CommitInfo;
import dataObject.RepoConfiguration;
import dataObject.RepoInfo;
import git.GitChecker;
import git.GitLogger;

import java.util.HashSet;
import java.util.List;

/**
 * Created by matanghao1 on 21/6/17.
 */
public class RepoAnalyzer {


    public static void analyzeCommits(RepoConfiguration config, RepoInfo repo){
        System.out.println("analyzing commits...");
        List<CommitInfo> commits = GitLogger.getCommits(config.getRepoRoot(), config);
        CommitInfo lastCommit = commits.get(commits.size()-1);
        CommitAnalyzer.aggregateFileInfos(config,lastCommit);
        repo.setCommits(commits);
        System.out.println("done analyzing commits...");

    }
}
