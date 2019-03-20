package reposense.commits;

import java.util.Date;
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
    public static CommitContributionSummary generateCommitSummary(RepoConfiguration config,
            Date sinceDate, Date untilDate) {
        List<CommitInfo> commitInfos = CommitInfoExtractor.extractCommitInfos(config, sinceDate, untilDate);

        List<CommitResult> commitResults = CommitInfoAnalyzer.analyzeCommits(commitInfos, config);

        return CommitResultAggregator.aggregateCommitResults(config, commitResults, sinceDate);
    }
}
