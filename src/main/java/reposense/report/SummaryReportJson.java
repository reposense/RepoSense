package reposense.report;

import java.util.List;

import reposense.model.RepoConfiguration;

/**
 * Represents the structure of summary.json file in reposense-report folder.
 */
public class SummaryReportJson {
    private final String reportGeneratedTime;
    private final List<RepoConfiguration> repos;

    public SummaryReportJson(List<RepoConfiguration> repos, String reportGeneratedTime) {
        this.repos = repos;
        this.reportGeneratedTime = reportGeneratedTime;
    }
}
