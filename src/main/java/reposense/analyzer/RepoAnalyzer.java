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

    /**
     * Analyzes each relevant file to identify the {@code Author} of each line in those files within date range.
     */
    public static List<FileInfo> analyzeAuthorship(RepoConfiguration config) {
        logger.info("Analyzing authorship...");

        // checks out to the latest commit of the date range to ensure the FileInfo generated correspond to the
        // git blame file analyze output
        GitChecker.checkoutToDate(config.getRepoRoot(), config.getBranch(), config.getToDate());
        return FileAnalyzer.analyzeAllFiles(config);
    }
}
