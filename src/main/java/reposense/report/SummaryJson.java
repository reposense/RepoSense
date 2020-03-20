package reposense.report;

import java.util.Date;
import java.util.List;
import java.util.Map;

import reposense.model.RepoConfiguration;

/**
 * Represents the structure of summary.json file in reposense-report folder.
 */
public class SummaryJson {
    public static final String SUMMARY_JSON_FILE_NAME = "summary.json";

    private final String repoSenseVersion;
    private final String reportGeneratedTime;
    private final String reportGenerationTime;
    private final List<RepoConfiguration> repos;
    private final List<Map<String, String>> errorList;
    private final Date sinceDate;
    private final Date untilDate;
    private final boolean isSinceDateProvided;
    private final boolean isUntilDateProvided;

    public SummaryJson(List<RepoConfiguration> repos, String reportGeneratedTime, Date sinceDate, Date untilDate,
            boolean isSinceDateProvided, boolean isUntilDateProvided, String repoSenseVersion,
            List<Map<String, String>> errorList, String reportGenerationTime) {
        this.repos = repos;
        this.reportGeneratedTime = reportGeneratedTime;
        this.reportGenerationTime = reportGenerationTime;
        this.sinceDate = sinceDate;
        this.untilDate = untilDate;
        this.isSinceDateProvided = isSinceDateProvided;
        this.isUntilDateProvided = isUntilDateProvided;
        this.repoSenseVersion = repoSenseVersion;
        this.errorList = errorList;
    }
}
