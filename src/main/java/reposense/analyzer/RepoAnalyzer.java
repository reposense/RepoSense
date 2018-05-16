package reposense.analyzer;

import java.util.List;

import reposense.dataobject.CommitInfo;
import reposense.dataobject.FileInfo;
import reposense.dataobject.RepoConfiguration;
import reposense.dataobject.RepoInfo;
import reposense.git.GitChecker;
import reposense.git.GitLogger;


public class RepoAnalyzer {


    public static List<CommitInfo> analyzeCommits(RepoConfiguration config) {
        GitChecker.checkoutBranch(config.getRepoRoot(), config.getBranch());
        System.out.println("analyzing commits for " + config.getOrganization() + "/" + config.getRepoName() + "...");
        return GitLogger.getCommits(config);
    }

    public static List<FileInfo> analyzeAuthorship(RepoConfiguration config) {
        System.out.println("analyzing authorship...");
        return FileAnalyzer.analyzeAllFiles(config);
    }
}
