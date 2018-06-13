package reposense.dataobject;

import java.util.List;
import java.util.Map;

public class CommitContributionSummary {

    private final Map<Author, List<AuthorIntervalContribution>> authorWeeklyIntervalContributions;
    private final Map<Author, List<AuthorIntervalContribution>> authorDailyIntervalContributions;
    private final Map<Author, Float> authorContributionVariance;
    private final Map<Author, String> authorDisplayNameMap;

    public CommitContributionSummary(Map<Author, String> authorDisplayNameMap,
                                     Map<Author, List<AuthorIntervalContribution>> authorDailyIntervalContributions,
                                     Map<Author, List<AuthorIntervalContribution>> authorWeeklyIntervalContributions,
                                     Map<Author, Float> authorContributionVariance) {

        this.authorDisplayNameMap = authorDisplayNameMap;
        this.authorDailyIntervalContributions = authorDailyIntervalContributions;
        this.authorWeeklyIntervalContributions = authorWeeklyIntervalContributions;
        this.authorContributionVariance = authorContributionVariance;
    }

    public Map<Author, List<AuthorIntervalContribution>> getAuthorWeeklyIntervalContributions() {
        return authorWeeklyIntervalContributions;
    }

    public Map<Author, List<AuthorIntervalContribution>> getAuthorDailyIntervalContributions() {
        return authorDailyIntervalContributions;
    }

    public Map<Author, Float> getAuthorContributionVariance() {
        return authorContributionVariance;
    }
}
