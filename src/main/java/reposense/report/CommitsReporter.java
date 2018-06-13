package reposense.report;

import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import reposense.analyzer.CommitInfoAnalyzer;
import reposense.analyzer.CommitInfoExtractor;
import reposense.analyzer.CommitResultAggregator;
import reposense.dataobject.CommitContributionSummary;
import reposense.dataobject.CommitInfo;
import reposense.dataobject.CommitResult;
import reposense.dataobject.RepoConfiguration;
import reposense.git.GitDownloader;
import reposense.git.GitDownloaderException;
import reposense.system.LogsManager;
import reposense.util.FileUtil;

public class CommitsReporter {
    private static final Logger logger = LogsManager.getLogger(CommitsReporter.class);

    /**
     * Generates the commit contribution report for each repo in {@code config} at {@code reportReportDirectory}.
     */
    public static void generateCommitReport(RepoConfiguration config, String repoReportDirectory) {
        try {
            GitDownloader.downloadRepo(config.getOrganization(), config.getRepoName(), config.getBranch());
        } catch (GitDownloaderException gde) {
            logger.warning("Exception met while trying to clone the repo, will skip this one");
            return;
        }

        List<CommitInfo> commitInfos = CommitInfoExtractor.extractCommitInfos(config);

        List<CommitResult> commitResults = commitInfos.stream()
                .map(commitInfo -> CommitInfoAnalyzer.analyzeCommit(commitInfo, config.getAuthorAliasMap()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        CommitContributionSummary commitSummary = CommitResultAggregator.aggregateCommitResults(config, commitResults);

        FileUtil.writeJsonFile(commitSummary, getIndividualCommitsPath(repoReportDirectory));
    }

    private static String getIndividualCommitsPath(String repoReportDirectory) {
        return repoReportDirectory + "/commits.json";
    }
}
