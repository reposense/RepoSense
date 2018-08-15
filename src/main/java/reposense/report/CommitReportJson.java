package reposense.report;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import reposense.authorship.model.AuthorshipSummary;
import reposense.commits.model.AuthorIntervalContribution;
import reposense.commits.model.CommitContributionSummary;
import reposense.model.Author;

/**
 * Class that holds the data to be serialized into JSON format in `commits.json`.
 */
public class CommitReportJson {
    private final Map<Author, List<AuthorIntervalContribution>> authorWeeklyIntervalContributions;
    private final Map<Author, List<AuthorIntervalContribution>> authorDailyIntervalContributions;
    private final Map<Author, Integer> authorFinalContributionMap;
    private final Map<Author, Float> authorContributionVariance;
    private final Map<Author, String> authorDisplayNameMap;

    /**
     * Empty constructor to construct an empty commit report.
     */
    public CommitReportJson() {
        Author emptyAuthor = new Author(Author.UNKNOWN_AUTHOR_GIT_ID);
        authorWeeklyIntervalContributions = Map.of(emptyAuthor, Collections.emptyList());
        authorDailyIntervalContributions = Map.of(emptyAuthor, Collections.emptyList());
        authorFinalContributionMap = Map.of(emptyAuthor, 0);
        authorContributionVariance = Map.of(emptyAuthor, (float) 0.0);
        authorDisplayNameMap = Map.of(emptyAuthor, "EMPTY REPOSITORY");
    }

    public CommitReportJson(CommitContributionSummary commitSummary, AuthorshipSummary authorshipSummary) {
        authorWeeklyIntervalContributions = commitSummary.getAuthorWeeklyIntervalContributions();
        authorDailyIntervalContributions = commitSummary.getAuthorDailyIntervalContributions();
        authorFinalContributionMap = authorshipSummary.getAuthorFinalContributionMap();
        authorContributionVariance = commitSummary.getAuthorContributionVariance();
        authorDisplayNameMap = commitSummary.getAuthorDisplayNameMap();
    }
}
