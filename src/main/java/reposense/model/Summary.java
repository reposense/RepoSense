package reposense.model;

import java.util.Date;
import java.util.List;

/**
 * Stores the summary.json file values.
 */
public class Summary {
    private final String dashboardUpdateDate;
    private final List<RepoConfiguration> repos;

    public Summary(List<RepoConfiguration> repos) {
        dashboardUpdateDate = new Date().toString();
        this.repos = repos;
    }

    public List<RepoConfiguration> getRepos() {
        return repos;
    }

    public String getDashboardUpdateDate() {
        return dashboardUpdateDate;
    }
}
