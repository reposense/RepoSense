package reposense.report;

import java.time.LocalDateTime;
import java.time.ZoneId;
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

    private String repoSenseVersion;
    private String reportGeneratedTime;
    private String reportGenerationTime;
    private ZoneId zoneId;
    private String reportTitle;
    private List<RepoConfiguration> repos;
    private Set<Map<String, String>> errorSet;
    private LocalDateTime sinceDate;
    private LocalDateTime untilDate;
    private boolean isSinceDateProvided;
    private boolean isUntilDateProvided;
    private Map<String, Map<String, String>> supportedDomainUrlMap;

    private SummaryJson() {

    }

    public SummaryJson(List<RepoConfiguration> repos, ReportConfiguration reportConfig, String reportGeneratedTime,
            LocalDateTime sinceDate, LocalDateTime untilDate, boolean isSinceDateProvided, boolean isUntilDateProvided,
            String repoSenseVersion, Set<Map<String, String>> errorSet, String reportGenerationTime, ZoneId zoneId) {
        this.repos = repos;
        this.reportGeneratedTime = reportGeneratedTime;
        this.reportGenerationTime = reportGenerationTime;
        this.reportTitle = reportConfig.getReportTitle();
        this.sinceDate = sinceDate;
        this.untilDate = untilDate;
        this.isSinceDateProvided = isSinceDateProvided;
        this.isUntilDateProvided = isUntilDateProvided;
        this.repoSenseVersion = repoSenseVersion;
        this.errorSet = errorSet;
        this.zoneId = zoneId;
        this.supportedDomainUrlMap = SupportedDomainUrlMap.getDefaultDomainUrlMap();
    }

    /**
     * Builder class to build {@code SummaryJson} objects.
     */
    public static class Builder {
        private SummaryJson summaryJson;

        public Builder() {
            this.summaryJson = new SummaryJson();
        }

        /**
         * Composes this {@code Builder} object with a list of {@code RepoConfiguration}.
         *
         * @param repos List of {@code RepoConfiguration}.
         * @return This {@code Builder} object.
         */
        public Builder repos(List<RepoConfiguration> repos) {
            this.summaryJson.repos = repos;
            return this;
        }

        /**
         * Composes this {@code Builder} object with the {@code ReportConfiguration}.
         *
         * @param reportConfiguration {@code ReportConfiguration} object.
         * @return This {@code Builder} object.
         */
        public Builder reportConfiguration(ReportConfiguration reportConfiguration) {
            this.summaryJson.reportTitle = reportConfiguration.getReportTitle();
            return this;
        }

        /**
         * Composes this {@code Builder} object with the time when the report is generated.
         *
         * @param reportGeneratedTime Time when the report is generated.
         * @return This {@code Builder} object.
         */
        public Builder reportGeneratedTime(String reportGeneratedTime) {
            this.summaryJson.reportGeneratedTime = reportGeneratedTime;
            return this;
        }

        /**
         * Composes this {@code Builder} object with the time of generation for this report.
         *
         * @param reportGenerationTime Time of generation for this report.
         * @return This {@code Builder} object.
         */
        public Builder reportGenerationTime(String reportGenerationTime) {
            this.summaryJson.reportGenerationTime = reportGenerationTime;
            return this;
        }

        /**
         * Composes this {@code Builder} object with the start date of analysis for this report.
         *
         * @param sinceDate start date of analysis.
         * @return This {@code Builder} object.
         */
        public Builder sinceDate(LocalDateTime sinceDate) {
            this.summaryJson.sinceDate = sinceDate;
            return this;
        }

        /**
         * Composes this {@code Builder} object with the end date of analysis for this report.
         *
         * @param untilDate End date of analysis.
         * @return This {@code Builder} object.
         */
        public Builder untilDate(LocalDateTime untilDate) {
            this.summaryJson.untilDate = untilDate;
            return this;
        }

        /**
         * Composes this {@code Builder} object the {@code isSinceDateProvided} flag.
         *
         * @param isSinceDateProvided {@code isSinceDateProvided} flag.
         * @return This {@code Builder} object.
         */
        public Builder isSinceDateProvided(boolean isSinceDateProvided) {
            this.summaryJson.isSinceDateProvided = isSinceDateProvided;
            return this;
        }

        /**
         * Composes this {@code Builder} object the {@code isUntilDateProvided} flag.
         *
         * @param isUntilDateProvided {@code isUntilDateProvided} flag.
         * @return This {@code Builder} object.
         */
        public Builder isUntilDateProvided(boolean isUntilDateProvided) {
            this.summaryJson.isUntilDateProvided = isUntilDateProvided;
            return this;
        }

        /**
         * Composes this {@code Builder} object with the version string of RepoSense.
         *
         * @param repoSenseVersion Version string.
         * @return This {@code Builder} object.
         */
        public Builder repoSenseVersion(String repoSenseVersion) {
            this.summaryJson.repoSenseVersion = repoSenseVersion;
            return this;
        }

        /**
         * Composes this {@code Builder} object with a set of errors.
         *
         * @param errorSet {@code Set} of {@code Map} that maps a {@code String} error to another {@code String} error.
         * @return This {@code Builder} object.
         */
        public Builder errorSet(Set<Map<String, String>> errorSet) {
            this.summaryJson.errorSet = errorSet;
            return this;
        }

        /**
         * Composes this {@code Builder} object with the {@code ZoneId} of the report.
         *
         * @param zoneId {@code ZoneId} object.
         * @return This {@code Builder} object.
         */
        public Builder zoneId(ZoneId zoneId) {
            this.summaryJson.zoneId = zoneId;
            return this;
        }

        /**
         * Builds the instance of {@code SummaryJson} stored in this {@code Builder} object
         * and resets the stored instance.
         *
         * @return Built {@code SummaryJson} object.
         */
        public SummaryJson build() {
            SummaryJson toReturn = this.summaryJson;
            this.summaryJson = new SummaryJson();
            return toReturn;
        }
    }
}
