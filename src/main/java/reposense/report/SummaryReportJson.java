package reposense.report;

import java.util.List;

import reposense.model.RepoConfiguration;
import reposense.model.ReportConfiguration;

/**
 * Represents the structure of summary.json file in reposense-report folder.
 */
public class SummaryReportJson {
    private final String reportGeneratedTime;
    private final List<RepoConfiguration> repos;
    private final String reportTitle;

    public SummaryReportJson(List<RepoConfiguration> repos, ReportConfiguration reportConfig,
            String reportGeneratedTime) {
        this.repos = repos;
        this.reportGeneratedTime = reportGeneratedTime;
        this.reportTitle = reportConfig.getTitle();
    }
}
