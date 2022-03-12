package reposense.report;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

import reposense.model.RepoConfiguration;
import reposense.model.ReportConfiguration;
import reposense.model.SupportedDomainUrlMap;

/**
 * Represents the structure of summary.json file in reposense-report folder.
 */
public class SummaryJson {
    public static final String SUMMARY_JSON_FILE_NAME = "summary.json";

    private final String repoSenseVersion;
    private final String reportGeneratedTime;
    private final String reportGenerationTime;
    private final String zoneId;
    private final String reportTitle;
    private final List<RepoConfiguration> repos;
    private final Set<Map<String, String>> errorSet;
    private final LocalDateTime sinceDate;
    private final LocalDateTime untilDate;
    private final boolean isSinceDateProvided;
    private final boolean isUntilDateProvided;
    private final Map<String, Map<String, String>> supportedDomainUrlMap;

    public SummaryJson(List<RepoConfiguration> repos, ReportConfiguration reportConfig, String reportGeneratedTime,
            LocalDateTime sinceDate, LocalDateTime untilDate, boolean isSinceDateProvided, boolean isUntilDateProvided,
            String repoSenseVersion, Set<Map<String, String>> errorSet, String reportGenerationTime, String zoneId) {
        this.repos = repos;
        this.reportGeneratedTime = reportGeneratedTime;
        this.reportGenerationTime = reportGenerationTime;
        this.reportTitle = reportConfig.getTitle();
        this.sinceDate = sinceDate;
        this.untilDate = untilDate;
        this.isSinceDateProvided = isSinceDateProvided;
        this.isUntilDateProvided = isUntilDateProvided;
        this.repoSenseVersion = repoSenseVersion;
        this.errorSet = errorSet;
        this.zoneId = zoneId;
        this.supportedDomainUrlMap = SupportedDomainUrlMap.getDefaultDomainUrlMap();
    }
}
