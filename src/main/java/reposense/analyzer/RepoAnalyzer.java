package reposense.analyzer;

import reposense.dataObject.RepoConfiguration;
import reposense.dataObject.RepoInfo;
import reposense.git.GitChecker;
import reposense.git.GitLogger;


public class RepoAnalyzer {


    public static void analyzeCommits(RepoConfiguration config, RepoInfo repo) {
        GitChecker.checkoutBranch(config.getRepoRoot(), config.getBranch());
        System.out.println("analyzing commits for " + config.getOrganization() + "/" + config.getRepoName() + "...");
        repo.setCommits(GitLogger.getCommits(config));
        System.out.println("analyzing git log output...");
        System.out.println("aggregating file info...");
        ContentAnalyzer.aggregateFileInfos(config, repo);
        System.out.println("done analyzing commits...");
    }
}
