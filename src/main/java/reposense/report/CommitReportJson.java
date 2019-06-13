package reposense.report;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import reposense.authorship.model.AuthorshipSummary;
import reposense.commits.model.AuthorDailyContribution;
import reposense.commits.model.CommitContributionSummary;
import reposense.model.Author;

/**
 * Class that holds the data to be serialized into JSON format in `commits.json`.
 */
public class CommitReportJson {
    private final Map<Author, List<AuthorDailyContribution>> authorDailyContributionsMap;
    private final Map<Author, Integer> authorFinalContributionMap;
    private final Map<Author, LinkedHashMap<String, Integer>> authorFileFormatContributionMap;
    private final Map<Author, Float> authorContributionVariance;
    private final Map<Author, String> authorDisplayNameMap;

    public CommitReportJson(CommitContributionSummary commitSummary, AuthorshipSummary authorshipSummary) {
        authorDailyContributionsMap = commitSummary.getAuthorDailyContributionsMap();
        authorFinalContributionMap = authorshipSummary.getAuthorFinalContributionMap();
        authorFileFormatContributionMap = authorshipSummary.getAuthorFileFormatContributionMap();
        authorContributionVariance = commitSummary.getAuthorContributionVariance();
        authorDisplayNameMap = commitSummary.getAuthorDisplayNameMap();
    }
}
