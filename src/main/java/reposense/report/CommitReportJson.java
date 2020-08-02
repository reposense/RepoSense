package reposense.report;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import reposense.authorship.model.AuthorshipSummary;
import reposense.commits.model.AuthorDailyContribution;
import reposense.commits.model.CommitContributionSummary;
import reposense.model.Author;
import reposense.model.FileType;

/**
 * Class that holds the data to be serialized into JSON format in `commits.json`.
 */
public class CommitReportJson {
    private final Map<Author, List<AuthorDailyContribution>> authorDailyContributionsMap;
    private final Map<Author, LinkedHashMap<FileType, Integer>> authorFileTypeContributionMap;
    private final Map<Author, Float> authorContributionVariance;
    private final Map<Author, String> authorDisplayNameMap;

    /**
     * Constructor to construct an empty commit report with the author's display name as {@code displayName}.
     */
    public CommitReportJson(String displayName) {
        Author emptyAuthor = Author.UNKNOWN_AUTHOR;

        authorDailyContributionsMap = new HashMap<>();
        authorDailyContributionsMap.put(emptyAuthor, Collections.emptyList());

        authorFileTypeContributionMap = new HashMap<>();
        authorFileTypeContributionMap.put(emptyAuthor, new LinkedHashMap<>());

        authorContributionVariance = new HashMap<>();
        authorContributionVariance.put(emptyAuthor, (float) 0.0);

        authorDisplayNameMap = new HashMap<>();
        authorDisplayNameMap.put(emptyAuthor, displayName);
    }

    public CommitReportJson(CommitContributionSummary commitSummary, AuthorshipSummary authorshipSummary) {
        authorDailyContributionsMap = commitSummary.getAuthorDailyContributionsMap();
        authorFileTypeContributionMap = authorshipSummary.getAuthorFileTypeContributionMap();
        authorContributionVariance = commitSummary.getAuthorContributionVariance();
        authorDisplayNameMap = commitSummary.getAuthorDisplayNameMap();
    }
}
