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
import reposense.system.LogsManager;

public class CommitsReporter {
    private static final Logger logger = LogsManager.getLogger(CommitsReporter.class);

    /**
     * Generates and returns the commit contribution summary for each repo in {@code config}.
     */
    public static CommitContributionSummary generateCommitSummary(RepoConfiguration config) {
        List<CommitInfo> commitInfos = CommitInfoExtractor.extractCommitInfos(config);

        List<CommitResult> commitResults = commitInfos.stream()
                .map(commitInfo -> CommitInfoAnalyzer.analyzeCommit(commitInfo, config.getAuthorAliasMap()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return CommitResultAggregator.aggregateCommitResults(config, commitResults);
    }
}
