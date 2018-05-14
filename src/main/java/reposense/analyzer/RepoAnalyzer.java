package reposense.analyzer;

import reposense.dataobject.RepoConfiguration;
import reposense.dataobject.RepoInfo;
import reposense.git.GitChecker;
import reposense.git.GitLogger;


public class RepoAnalyzer {


    public static void analyzeCommits(RepoConfiguration config, RepoInfo repo) {
        GitChecker.checkoutBranch(config.getRepoRoot(), config.getBranch());
        System.out.println("analyzing commits for " + config.getOrganization() + "/" + config.getRepoName() + "...");
        repo.setCommits(GitLogger.getCommits(config));
    }

    public static void analyzeAuthorship(RepoConfiguration config, RepoInfo repo) {
        System.out.println("aggregating file info...");
        ContentAnalyzer.aggregateFileInfos(config, repo);
        System.out.println("done analyzing commits...");
    }
}
