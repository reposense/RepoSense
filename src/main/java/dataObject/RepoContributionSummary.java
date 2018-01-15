package dataObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by matanghao1 on 4/10/17.
 */
public class RepoContributionSummary {

    private String repo;
    private String organization;
    private String branch;
    private String displayName;
    private Map<Author, List<AuthorIntervalContribution>> authorWeeklyIntervalContributions = new HashMap<>();
    private Map<Author, List<AuthorIntervalContribution>> authorDailyIntervalContributions = new HashMap<>();
    private Map<Author, Integer> authorFinalContributionMap = new HashMap<>();
    private Map<Author, Float> authorRushiness = new HashMap<>() ;
    private Map<Author, String> authorDisplayNameMap = new HashMap<>();

    public RepoContributionSummary(RepoInfo repoInfo){
        repo = repoInfo.getRepoName();
        organization = repoInfo.getOrganization();
        branch = repoInfo.getBranch();
        displayName = repoInfo.getDirectoryName();
    }

    public Map<Author, String> getAuthorDisplayNameMap() {
        return authorDisplayNameMap;
    }

    public void setAuthorDisplayNameMap(Map<Author, String> authorDisplayNameMap) {
        this.authorDisplayNameMap = authorDisplayNameMap;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getRepo() {
        return repo;
    }

    public void setRepo(String repo) {
        this.repo = repo;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Map<Author, List<AuthorIntervalContribution>> getAuthorWeeklyIntervalContributions() {
        return authorWeeklyIntervalContributions;
    }

    public void setAuthorWeeklyIntervalContributions(Map<Author, List<AuthorIntervalContribution>> authorWeeklyIntervalContributions) {
        this.authorWeeklyIntervalContributions = authorWeeklyIntervalContributions;
    }

    public Map<Author, List<AuthorIntervalContribution>> getAuthorDailyIntervalContributions() {
        return authorDailyIntervalContributions;
    }

    public void setAuthorDailyIntervalContributions(Map<Author, List<AuthorIntervalContribution>> authorDailyIntervalContributions) {
        this.authorDailyIntervalContributions = authorDailyIntervalContributions;
    }

    public Map<Author, Integer> getAuthorFinalContributionMap() {
        return authorFinalContributionMap;
    }

    public void setAuthorFinalContributionMap(Map<Author, Integer> authorFinalContributionMap) {
        this.authorFinalContributionMap = authorFinalContributionMap;
    }

    public Map<Author, Float> getAuthorRushiness() {
        return authorRushiness;
    }

    public void setAuthorRushiness(Map<Author, Float> authorRushiness) {
        this.authorRushiness = authorRushiness;
    }
}
