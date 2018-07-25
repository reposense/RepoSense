package reposense.report;

import java.util.List;

import reposense.model.RepoConfiguration;

/**
 * Represents the structure of summary.json file in reposense-report folder.
 */
public class SummaryReportJson {
    private final String dashboardGeneratedTime;
    private final List<RepoConfiguration> repos;

    public SummaryReportJson(List<RepoConfiguration> repos, String dashboardGeneratedTime) {
        this.repos = repos;
        this.dashboardGeneratedTime = dashboardGeneratedTime;
    }
}
