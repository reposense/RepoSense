package reposense.model.reportconfig;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import reposense.model.BlurbMap;
import reposense.system.LogsManager;

/**
 * Class that contains information on a report's configurations.
 * This class is used mainly for quickly setting up one's personal code portfolio.
 */
public class ReportConfiguration {
    public static final String DEFAULT_TITLE = "RepoSense Report";

    private static final Logger logger = LogsManager.getLogger(ReportConfiguration.class);

    private String title;

    private List<ReportRepoConfiguration> reportRepoConfigurations;

    public ReportConfiguration() {}

    @JsonCreator
    public ReportConfiguration(
            @JsonProperty("title") String title,
            @JsonProperty("repos") List<ReportRepoConfiguration> reportRepoConfigurations) {
        this.title = title == null ? DEFAULT_TITLE : title;
        this.reportRepoConfigurations = reportRepoConfigurations == null ? new ArrayList<>() : reportRepoConfigurations;
    }

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
        return title;
    }

    public List<ReportRepoConfiguration> getReportRepoConfigurations() {
        return reportRepoConfigurations;
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
