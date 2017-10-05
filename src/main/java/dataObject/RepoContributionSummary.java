package dataObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by matanghao1 on 4/10/17.
 */
public class RepoContributionSummary {

    private String displayName;
    private Map<Author, Map<Date,AuthorIntervalContribution>> authorIntervalContributions;
    private Map<Author, Integer> authorFinalContributionMap;

    public RepoContributionSummary(RepoInfo repoInfo){
        displayName = repoInfo.getOrganization() + "_" + repoInfo.getRepoName();
        authorIntervalContributions = new HashMap<>();
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Map<Author, Map<Date, AuthorIntervalContribution>> getAuthorIntervalContributions() {
        return authorIntervalContributions;
    }

    public void setAuthorIntervalContributions(Map<Author, Map<Date, AuthorIntervalContribution>> authorIntervalContributions) {
        this.authorIntervalContributions = authorIntervalContributions;
    }

    public Map<Author, Integer> getAuthorFinalContributionMap() {
        return authorFinalContributionMap;
    }

    public void setAuthorFinalContributionMap(Map<Author, Integer> authorFinalContributionMap) {
        this.authorFinalContributionMap = authorFinalContributionMap;
    }
}
