package reposense.report;

import java.util.List;
import java.util.Map;

import reposense.dataobject.Author;
import reposense.dataobject.AuthorIntervalContribution;
import reposense.dataobject.AuthorshipSummary;
import reposense.dataobject.CommitContributionSummary;

@SuppressWarnings("PMD")
/**
 * Class that holds the data to be serialized into JSON format in `commits.json`.
 */
public class CommitReportJson {
    private final Map<Author, List<AuthorIntervalContribution>> authorWeeklyIntervalContributions;
    private final Map<Author, List<AuthorIntervalContribution>> authorDailyIntervalContributions;
    private final Map<Author, Integer> authorFinalContributionMap;
    private final Map<Author, Float> authorContributionVariance;
    private final Map<Author, String> authorDisplayNameMap;

    public CommitReportJson(CommitContributionSummary commitSummary, AuthorshipSummary authorshipSummary) {
        authorWeeklyIntervalContributions = commitSummary.getAuthorWeeklyIntervalContributions();
        authorDailyIntervalContributions = commitSummary.getAuthorDailyIntervalContributions();
        authorFinalContributionMap = authorshipSummary.getAuthorFinalContributionMap();
        authorContributionVariance = commitSummary.getAuthorContributionVariance();
        authorDisplayNameMap = commitSummary.getAuthorDisplayNameMap();
    }
}
