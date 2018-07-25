package reposense.model;

import java.util.List;

/**
 * Stores the summary.json file values.
 */
public class Summary {
    private final String dashboardUpdateDate;
    private final List<RepoConfiguration> repos;

    public Summary(List<RepoConfiguration> repos, String dashboardUpdateDate) {
        this.repos = repos;
        this.dashboardUpdateDate = dashboardUpdateDate;
    }

    public List<RepoConfiguration> getRepos() {
        return repos;
    }

    public String getDashboardUpdateDate() {
        return dashboardUpdateDate;
    }
}
