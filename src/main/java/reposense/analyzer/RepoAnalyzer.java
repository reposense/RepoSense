package reposense.analyzer;

import java.util.List;
import java.util.logging.Logger;

import reposense.dataobject.CommitInfo;
import reposense.dataobject.FileInfo;
import reposense.dataobject.RepoConfiguration;
import reposense.git.GitChecker;
import reposense.git.GitLogger;
import reposense.system.LogsManager;


public class RepoAnalyzer {

    private static final Logger logger = LogsManager.getLogger(RepoAnalyzer.class);

    public static List<CommitInfo> analyzeCommits(RepoConfiguration config) {
        GitChecker.checkoutBranch(config.getRepoRoot(), config.getBranch());
        logger.info("Analyzing commits for " + config.getOrganization() + "/" + config.getRepoName() + "...");
        return GitLogger.getCommits(config);
    }

    public static List<FileInfo> analyzeAuthorship(RepoConfiguration config) {
        logger.info("Analyzing authorship...");
        return FileAnalyzer.analyzeAllFiles(config);
    }
}
