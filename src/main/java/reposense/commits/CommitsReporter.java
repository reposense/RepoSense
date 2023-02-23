package reposense.commits;

import java.util.List;

import reposense.commits.model.CommitContributionSummary;
import reposense.commits.model.CommitInfo;
import reposense.commits.model.CommitResult;
import reposense.model.RepoConfiguration;
import reposense.report.ReportGenerator;

/**
 * Generates the commit summary data for each repository.
 */
public class CommitsReporter {
    private final CommitInfoExtractor commitInfoExtractor = new CommitInfoExtractor();
    private final CommitInfoAnalyzer commitInfoAnalyzer = new CommitInfoAnalyzer();
    private final CommitResultAggregator commitResultAggregator = new CommitResultAggregator();

    /**
     * Generates and returns the commit contribution summary for each repo in {@code config}.
     */
    public CommitContributionSummary generateCommitSummary(RepoConfiguration config, ReportGenerator reportGenerator) {
        List<CommitInfo> commitInfos = commitInfoExtractor.extractCommitInfos(config);

        List<CommitResult> commitResults = commitInfoAnalyzer.analyzeCommits(commitInfos, config);

        return commitResultAggregator.aggregateCommitResults(config, commitResults, reportGenerator);
    }
}
