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
        repo.setCommits(GitLogger.getCommits(config));
        System.out.println("analyzing git log output...");
        System.out.println("aggregating file info...");
        ContentAnalyzer.aggregateFileInfos(config,repo);
        System.out.println("done analyzing commits...");
    }
}
