package reposense.model.reportconfig;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.annotation.JsonProperty;

import reposense.model.BlurbMap;
import reposense.system.LogsManager;

/**
 * Class that contains information on a report's configurations.
 * This class is used mainly for quickly setting up one's personal code portfolio.
 */
public class ReportConfiguration {
    public static final String DEFAULT_TITLE = "RepoSense Report";
    public static final List<ReportRepoConfiguration> DEFAULT_REPORT_REPO_CONFIGS = new ArrayList<>();
    public static final ReportConfiguration DEFAULT_INSTANCE = new ReportConfiguration();

    private static final Logger logger = LogsManager.getLogger(ReportConfiguration.class);

    static {
        DEFAULT_REPORT_REPO_CONFIGS.add(ReportRepoConfiguration.DEFAULT_INSTANCE);
        ReportConfiguration.DEFAULT_INSTANCE.title = DEFAULT_TITLE;
        ReportConfiguration.DEFAULT_INSTANCE.reportRepoConfigurations = DEFAULT_REPORT_REPO_CONFIGS;
    }

    @JsonProperty("title")
    private String title;

    @JsonProperty("repos")
    private List<ReportRepoConfiguration> reportRepoConfigurations;

    /**
     * Converts the {@code ReportRepoConfiguration} list into a {@code BlurbMap}.
     *
     * @return {@code BlurbMap} containing the repository name and its associated blurb.
     */
    public BlurbMap getBlurbMap() {
        BlurbMap blurbMap = new BlurbMap();

        for (ReportRepoConfiguration repoConfig : reportRepoConfigurations) {
            try {
                for (ReportRepoConfiguration.MapEntry repoNameBlurbPair
                        : repoConfig.getFullyQualifiedRepoNamesWithBlurbs()) {
                    blurbMap.withRecord(repoNameBlurbPair.getKey(), repoNameBlurbPair.getValue());
                }
            } catch (IllegalArgumentException ex) {
                logger.log(Level.WARNING, ex.getMessage(), ex);
            }
        }

        return blurbMap;
    }

    public String getTitle() {
        return title == null ? DEFAULT_TITLE : title;
    }

    public List<ReportRepoConfiguration> getReportRepoConfigurations() {
        return reportRepoConfigurations == null ? DEFAULT_REPORT_REPO_CONFIGS : reportRepoConfigurations;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (obj instanceof ReportConfiguration) {
            ReportConfiguration rc = (ReportConfiguration) obj;
            return rc.getTitle().equals(this.getTitle())
                    && rc.getReportRepoConfigurations().equals(this.getReportRepoConfigurations());
        }

        return false;
    }
}
