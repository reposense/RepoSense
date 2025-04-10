package reposense.report;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Set;

import reposense.model.AuthorBlurbMap;
import reposense.model.RepoBlurbMap;
import reposense.model.RepoConfiguration;
import reposense.model.SupportedDomainUrlMap;
import reposense.model.reportconfig.ReportConfiguration;
import reposense.parser.SummaryJsonParser;

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
    private final RepoBlurbMap repoBlurbs;
    private final AuthorBlurbMap authorBlurbs;
    private final boolean isPortfolio;

    public SummaryJson(List<RepoConfiguration> repos, String reportTitle, String reportGeneratedTime,
                       LocalDateTime sinceDate, LocalDateTime untilDate, boolean isSinceDateProvided,
                       boolean isUntilDateProvided, String repoSenseVersion, Set<Map<String, String>> errorSet,
                       String reportGenerationTime, ZoneId zoneId, boolean isAuthorshipAnalyzed,
                       RepoBlurbMap repoBlurbs, AuthorBlurbMap authorBlurbs, boolean isPortfolio) {
        this.repos = repos;
        this.reportGeneratedTime = reportGeneratedTime;
        this.reportGenerationTime = reportGenerationTime;
        this.reportTitle = reportTitle;
        this.sinceDate = sinceDate;
        this.untilDate = untilDate;
        this.isSinceDateProvided = isSinceDateProvided;
        this.isUntilDateProvided = isUntilDateProvided;
        this.repoSenseVersion = repoSenseVersion;
        this.errorSet = errorSet;
        this.zoneId = zoneId;
        this.supportedDomainUrlMap = SupportedDomainUrlMap.getDefaultDomainUrlMap();
        this.isAuthorshipAnalyzed = isAuthorshipAnalyzed;
        this.repoBlurbs = repoBlurbs;
        this.authorBlurbs = authorBlurbs;
        this.isPortfolio = isPortfolio;
    }

    public SummaryJson(List<RepoConfiguration> repos, ReportConfiguration reportConfig, String reportGeneratedTime,
                       LocalDateTime sinceDate, LocalDateTime untilDate, boolean isSinceDateProvided,
                       boolean isUntilDateProvided, String repoSenseVersion, Set<Map<String, String>> errorSet,
                       String reportGenerationTime, ZoneId zoneId, boolean isAuthorshipAnalyzed,
                       RepoBlurbMap repoBlurbs, AuthorBlurbMap authorBlurbs, boolean isPortfolio) {

        this(repos, reportConfig == null ? ReportConfiguration.DEFAULT_TITLE : reportConfig.getTitle(),
                reportGeneratedTime, sinceDate, untilDate, isSinceDateProvided, isUntilDateProvided, repoSenseVersion,
                errorSet, reportGenerationTime, zoneId, isAuthorshipAnalyzed, repoBlurbs, authorBlurbs, isPortfolio);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SummaryJson) {
            SummaryJson other = (SummaryJson) obj;
            return repoSenseVersion.equals(other.repoSenseVersion)
                    && reportGeneratedTime.equals(other.reportGeneratedTime)
                    && reportGenerationTime.equals(other.reportGenerationTime)
                    && zoneId.equals(other.zoneId)
                    && reportTitle.equals(other.reportTitle)
                    && repos.equals(other.repos)
                    && errorSet.equals(other.errorSet)
                    && sinceDate.equals(other.sinceDate)
                    && untilDate.equals(other.untilDate)
                    && isSinceDateProvided == other.isSinceDateProvided
                    && isUntilDateProvided == other.isUntilDateProvided
                    && supportedDomainUrlMap.equals(other.supportedDomainUrlMap)
                    && isAuthorshipAnalyzed == other.isAuthorshipAnalyzed
                    && repoBlurbs.equals(other.repoBlurbs)
                    && authorBlurbs.equals(other.authorBlurbs)
                    && isPortfolio == other.isPortfolio;
        }
        return false;
    }

    /**
     * Parses and updates an existing summary.json file.
     * This method reads the file from the given {@link Path}, parses it into a {@link SummaryJson} object,
     * replaces the blurbs, reportGeneratedTime, and reportGenerationTime fields with the provided values,
     * and returns the updated object.
     *
     * @throws IOException if there is an error reading the file.
     */
    public static SummaryJson updateSummaryJson(Path path, RepoBlurbMap newRepoBlurbMap,
                                                AuthorBlurbMap newAuthorBlurbMap, String newReportGeneratedTime,
                                                String newReportGenerationTime) throws IOException {

        SummaryJson oldSummaryJson = new SummaryJsonParser().parse(path);

        return new SummaryJson(oldSummaryJson.repos, oldSummaryJson.reportTitle, newReportGeneratedTime,
                oldSummaryJson.sinceDate, oldSummaryJson.untilDate, oldSummaryJson.isSinceDateProvided,
                oldSummaryJson.isUntilDateProvided, oldSummaryJson.repoSenseVersion, oldSummaryJson.errorSet,
                newReportGenerationTime, oldSummaryJson.zoneId, oldSummaryJson.isAuthorshipAnalyzed, newRepoBlurbMap,
                newAuthorBlurbMap, oldSummaryJson.isPortfolio);
    }

    public List<RepoConfiguration> getRepos() {
        return repos;
    }

    public String getReportGeneratedTime() {
        return reportGeneratedTime;
    }

    public String getReportGenerationTime() {
        return reportGenerationTime;
    }

    public String getReportTitle() {
        return reportTitle;
    }

    public LocalDateTime getSinceDate() {
        return sinceDate;
    }

    public LocalDateTime getUntilDate() {
        return untilDate;
    }

    public boolean isSinceDateProvided() {
        return isSinceDateProvided;
    }

    public boolean isUntilDateProvided() {
        return isUntilDateProvided;
    }

    public String getRepoSenseVersion() {
        return repoSenseVersion;
    }

    public Set<Map<String, String>> getErrorSet() {
        return errorSet;
    }

    public ZoneId getZoneId() {
        return zoneId;
    }

    public Map<String, Map<String, String>> getSupportedDomainUrlMap() {
        return supportedDomainUrlMap;
    }

    public boolean isAuthorshipAnalyzed() {
        return isAuthorshipAnalyzed;
    }

    public RepoBlurbMap getRepoBlurbs() {
        return repoBlurbs;
    }

    public AuthorBlurbMap getAuthorBlurbs() {
        return authorBlurbs;
    }

    public boolean isPortfolio() {
        return isPortfolio;
    }

}
