package reposense.report;

import java.util.List;
import java.util.stream.Collectors;

import reposense.model.RepoConfiguration;

/**
 * Represents the structure of summary.json file in reposense-report folder.
 */
public class SummaryReportJson {
    private final String dashboardGeneratedTime;
    private final List<RepoConfigurationJson> repos;

    public SummaryReportJson(List<RepoConfiguration> repos, String dashboardGeneratedTime) {
        this.repos = repos.stream()
                .map(RepoConfigurationJson::new)
                .collect(Collectors.toList());
        this.dashboardGeneratedTime = dashboardGeneratedTime;
    }
}
