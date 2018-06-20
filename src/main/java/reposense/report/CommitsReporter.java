package reposense.report;

import java.util.List;
import java.util.stream.Collectors;

import reposense.analyzer.CommitInfoAnalyzer;
import reposense.analyzer.CommitInfoExtractor;
import reposense.analyzer.CommitResultAggregator;
import reposense.dataobject.Author;
import reposense.dataobject.CommitContributionSummary;
import reposense.dataobject.CommitInfo;
import reposense.dataobject.CommitResult;
import reposense.dataobject.RepoConfiguration;

@SuppressWarnings("PMD")
/**
 * Class for generating of the commit summary data for each repo.
 */
public class CommitsReporter {

    /**
     * Generates and returns the commit contribution summary for each repo in {@code config}.
     */
    public static CommitContributionSummary generateCommitSummary(RepoConfiguration config) {
        List<CommitInfo> commitInfos = CommitInfoExtractor.extractCommitInfos(config);

        List<CommitResult> commitResults = commitInfos.stream()
                .map(commitInfo -> CommitInfoAnalyzer.analyzeCommit(commitInfo, config.getAuthorAliasMap()))
                .filter(commitResult -> !commitResult.getAuthor().equals(new Author(Author.UNKNOWN_AUTHOR_GIT_ID)))
                .collect(Collectors.toList());

        return CommitResultAggregator.aggregateCommitResults(config, commitResults);
    }
}
