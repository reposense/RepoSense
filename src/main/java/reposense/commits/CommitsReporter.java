package reposense.commits;

import java.util.HashMap;
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
    public static HashMap<String, CommitContributionSummary> generateCommitSummary(RepoConfiguration config) {
        HashMap<String, List<CommitInfo>> commitInfos = CommitInfoExtractor.extractCommitInfos(config);
        HashMap<String, CommitContributionSummary> commitSummariesForEachDocType = new HashMap<>();
        for (HashMap.Entry<String, List<CommitInfo>> commitInfosForEachDocType: commitInfos.entrySet()) {
            commitSummariesForEachDocType.put(commitInfosForEachDocType.getKey(),
                    generateCommitSummaryForEachDocType(config, commitInfosForEachDocType.getValue()));
        }
        return commitSummariesForEachDocType;
    }

    private static CommitContributionSummary generateCommitSummaryForEachDocType(
            RepoConfiguration config, List<CommitInfo> commitInfos) {

        List<CommitResult> commitResults = commitInfos.stream()
                .map(commitInfo -> CommitInfoAnalyzer.analyzeCommit(commitInfo, config.getAuthorAliasMap()))
                .filter(commitResult -> !commitResult.getAuthor().equals(new Author(Author.UNKNOWN_AUTHOR_GIT_ID)))
                .collect(Collectors.toList());

        return CommitResultAggregator.aggregateCommitResults(config, commitResults);
    }
}
