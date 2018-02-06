package analyzer;

import dataObject.CommitInfo;
import dataObject.RepoConfiguration;
import dataObject.RepoInfo;
import git.GitChecker;
import git.GitLogger;

import java.util.List;

/**
 * Created by matanghao1 on 21/6/17.
 */
public class RepoAnalyzer {


    public static void analyzeCommits(RepoConfiguration config, RepoInfo repo){
        GitChecker.checkoutBranch(config.getRepoRoot(),config.getBranch());
        System.out.println("analyzing commits for "+config.getOrganization()+"/"+config.getRepoName()+"...");
        List<CommitInfo> commits = GitLogger.getCommits(config);
        System.out.println("analyzing git log output...");
        if (commits.isEmpty()) return;
        CommitInfo lastCommit = commits.get(commits.size()-1);
        System.out.println("aggregating file info...");
        CommitAnalyzer.aggregateFileInfos(config,lastCommit);
        repo.setCommits(commits);
        System.out.println("done analyzing commits...");

    }
}
