package reposense.report;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Set;

import reposense.model.BlurbMap;
import reposense.model.RepoConfiguration;
import reposense.model.SupportedDomainUrlMap;
import reposense.model.reportconfig.ReportConfiguration;

/**
 * Represents the structure of summary.json file in reposense-report folder.
 */
public class SummaryJson {
    public static final String SUMMARY_JSON_FILE_NAME = "summary.json";

    private final String repoSenseVersion;
    private final String reportGeneratedTime;
    private final String reportGenerationTime;
    private final ZoneId zoneId;
    private final String reportTitle;
    private final List<RepoConfiguration> repos;
    private final Set<Map<String, String>> errorSet;
    private final LocalDateTime sinceDate;
    private final LocalDateTime untilDate;
    private final boolean isSinceDateProvided;
    private final boolean isUntilDateProvided;
    private final Map<String, Map<String, String>> supportedDomainUrlMap;
    private final boolean isAuthorshipAnalyzed;
    private final BlurbMap blurbs;

    public SummaryJson(List<RepoConfiguration> repos, ReportConfiguration reportConfig, String reportGeneratedTime,
                       LocalDateTime sinceDate, LocalDateTime untilDate, boolean isSinceDateProvided,
                       boolean isUntilDateProvided, String repoSenseVersion, Set<Map<String, String>> errorSet,
                       String reportGenerationTime, ZoneId zoneId,
                       boolean isAuthorshipAnalyzed, BlurbMap blurbs) {
        this.repos = repos;
        this.reportGeneratedTime = reportGeneratedTime;
        this.reportGenerationTime = reportGenerationTime;
        this.reportTitle = reportConfig == null ? ReportConfiguration.DEFAULT_TITLE : reportConfig.getTitle();
        this.sinceDate = sinceDate;
        this.untilDate = untilDate;
        this.isSinceDateProvided = isSinceDateProvided;
        this.isUntilDateProvided = isUntilDateProvided;
        this.repoSenseVersion = repoSenseVersion;
        this.errorSet = errorSet;
        this.zoneId = zoneId;
        this.supportedDomainUrlMap = SupportedDomainUrlMap.getDefaultDomainUrlMap();
        this.isAuthorshipAnalyzed = isAuthorshipAnalyzed;
        this.blurbs = blurbs;
    }
}
