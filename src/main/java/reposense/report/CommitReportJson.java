package reposense.report;

import java.util.Collections;
import java.util.HashMap;
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
        authorWeeklyIntervalContributions = new HashMap<>();
        authorWeeklyIntervalContributions.put(emptyAuthor, Collections.emptyList());

        authorDailyIntervalContributions = new HashMap<>();
        authorWeeklyIntervalContributions.put(emptyAuthor, Collections.emptyList());

        authorFinalContributionMap = new HashMap<>();
        authorFinalContributionMap.put(emptyAuthor, 0);

        authorContributionVariance = new HashMap<>();
        authorContributionVariance.put(emptyAuthor, (float) 0.0);

        authorDisplayNameMap = new HashMap<>();
        authorDisplayNameMap.put(emptyAuthor, "NON EXISTENT REPOSITORY");
    }

    public CommitReportJson(CommitContributionSummary commitSummary, AuthorshipSummary authorshipSummary) {
        authorWeeklyIntervalContributions = commitSummary.getAuthorWeeklyIntervalContributions();
        authorDailyIntervalContributions = commitSummary.getAuthorDailyIntervalContributions();
        authorFinalContributionMap = authorshipSummary.getAuthorFinalContributionMap();
        authorContributionVariance = commitSummary.getAuthorContributionVariance();
        authorDisplayNameMap = commitSummary.getAuthorDisplayNameMap();
    }
}
