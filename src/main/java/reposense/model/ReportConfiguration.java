package reposense.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Class that contains information on a report's configurations.
 * This class is used mainly for quickly setting up one's personal code portfolio.
 */
public class ReportConfiguration {
    public static final String DEFAULT_TITLE = "RepoSense Report";
    public static final List<ReportRepoConfiguration> DEFAULT_REPORT_REPO_CONFIGS = new ArrayList<>();

    static {
        DEFAULT_REPORT_REPO_CONFIGS.add(
                ReportRepoConfiguration.DEFAULT_INSTANCE
        );
    }

    @JsonProperty("title")
    private String title;

    @JsonProperty("repos")
    private List<ReportRepoConfiguration> reportRepoConfigurations;

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
