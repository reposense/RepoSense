package reposense.dataobject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RepoContributionSummary {
    private Map<Author, List<AuthorIntervalContribution>> authorWeeklyIntervalContributions = new HashMap<>();
    private Map<Author, List<AuthorIntervalContribution>> authorDailyIntervalContributions = new HashMap<>();
    private Map<Author, Integer> authorFinalContributionMap = new HashMap<>();
    private Map<Author, Float> authorContributionVariance = new HashMap<>();
    private Map<Author, String> authorDisplayNameMap = new HashMap<>();

    public Map<Author, String> getAuthorDisplayNameMap() {
        return authorDisplayNameMap;
    }

    public void setAuthorDisplayNameMap(Map<Author, String> authorDisplayNameMap) {
        this.authorDisplayNameMap = authorDisplayNameMap;
    }

    public Map<Author, List<AuthorIntervalContribution>> getAuthorWeeklyIntervalContributions() {
        return authorWeeklyIntervalContributions;
    }

    public void setAuthorWeeklyIntervalContributions(
            Map<Author, List<AuthorIntervalContribution>> authorWeeklyIntervalContributions) {
        this.authorWeeklyIntervalContributions = authorWeeklyIntervalContributions;
    }

    public Map<Author, List<AuthorIntervalContribution>> getAuthorDailyIntervalContributions() {
        return authorDailyIntervalContributions;
    }

    public void setAuthorDailyIntervalContributions(
            Map<Author, List<AuthorIntervalContribution>> authorDailyIntervalContributions) {
        this.authorDailyIntervalContributions = authorDailyIntervalContributions;
    }

    public Map<Author, Integer> getAuthorFinalContributionMap() {
        return authorFinalContributionMap;
    }

    public void setAuthorFinalContributionMap(Map<Author, Integer> authorFinalContributionMap) {
        this.authorFinalContributionMap = authorFinalContributionMap;
    }

    public Map<Author, Float> getAuthorContributionVariance() {
        return authorContributionVariance;
    }

    public void setAuthorContributionVariance(Map<Author, Float> authorContributionVariance) {
        this.authorContributionVariance = authorContributionVariance;
    }
}
