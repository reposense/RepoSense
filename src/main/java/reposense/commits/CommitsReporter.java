package reposense.commits;

import java.util.List;
import java.util.stream.Collectors;

import reposense.commits.model.CommitContributionSummary;
import reposense.commits.model.CommitInfo;
import reposense.commits.model.CommitResult;
import reposense.model.Author;
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

        List<CommitResult> commitResults = commitInfos.stream()
                .map(commitInfo -> CommitInfoAnalyzer.analyzeCommit(commitInfo, config.getAuthorAliasMap()))
                .filter(commitResult -> !commitResult.getAuthor().equals(new Author(Author.UNKNOWN_AUTHOR_GIT_ID)))
                .collect(Collectors.toList());

        return CommitResultAggregator.aggregateCommitResults(config, commitResults);
    }
}
