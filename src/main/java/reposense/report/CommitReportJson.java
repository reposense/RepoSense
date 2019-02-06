package reposense.report;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
    private final Map<Author, LinkedHashMap<String, Integer>> authorFileTypeContributionMap;
    private final Map<Author, Float> authorContributionVariance;
    private final Map<Author, String> authorDisplayNameMap;

    /**
     * Empty constructor to construct an empty commit report.
     */
    public CommitReportJson() {
        Author emptyAuthor = Author.UNKNOWN_AUTHOR;
        authorWeeklyIntervalContributions = new HashMap<>();
        authorWeeklyIntervalContributions.put(emptyAuthor, Collections.emptyList());

        authorDailyIntervalContributions = new HashMap<>();
        authorDailyIntervalContributions.put(emptyAuthor, Collections.emptyList());

        authorFinalContributionMap = new HashMap<>();
        authorFinalContributionMap.put(emptyAuthor, 0);

        authorFileTypeContributionMap = new HashMap<>();
        authorFileTypeContributionMap.put(emptyAuthor, new LinkedHashMap<>());

        authorContributionVariance = new HashMap<>();
        authorContributionVariance.put(emptyAuthor, (float) 0.0);

        authorDisplayNameMap = new HashMap<>();
        authorDisplayNameMap.put(emptyAuthor, "FAILED TO CLONE OR CHECKOUT THIS REPOSITORY");
    }

    public CommitReportJson(CommitContributionSummary commitSummary, AuthorshipSummary authorshipSummary) {
        authorWeeklyIntervalContributions = commitSummary.getAuthorWeeklyIntervalContributions();
        authorDailyIntervalContributions = commitSummary.getAuthorDailyIntervalContributions();
        authorFinalContributionMap = authorshipSummary.getAuthorFinalContributionMap();
        authorFileTypeContributionMap = authorshipSummary.getAuthorFileTypeContributionMap();
        authorContributionVariance = commitSummary.getAuthorContributionVariance();
        authorDisplayNameMap = commitSummary.getAuthorDisplayNameMap();
    }
}
