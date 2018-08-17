package reposense.commits;

import java.util.List;

import reposense.commits.model.CommitContributionSummary;
import reposense.commits.model.CommitInfo;
import reposense.commits.model.CommitResult;
import reposense.model.RepoConfiguration;

/**
 * Generates the commit summary data for each repository.
 */
public class CommitsReporter {

    /**
     * Generates and returns the commit contribution summary for each repo in {@code config}.
     */
    public static CommitContributionSummary generateCommitSummary(RepoConfiguration config) {
        List<CommitInfo> commitInfos = CommitInfoExtractor.extractCommitInfos(config);

        List<CommitResult> commitResults = CommitInfoAnalyzer.analyzeCommits(commitInfos, config);

        return CommitResultAggregator.aggregateCommitResults(config, commitResults);
    }
}
