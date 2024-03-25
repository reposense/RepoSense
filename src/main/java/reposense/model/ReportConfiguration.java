package reposense.model;

import java.time.LocalDate;

/**
 * Class that contains information on a report's configurations.
 * This class is used mainly for quickly setting up one's personal code portfolio.
 */
public class ReportConfiguration {
    public static final String DEFAULT_REPO_URL = "github.com/user/repo";
    public static final String DEFAULT_TITLE = "RepoSense Report";
    public static final String DEFAULT_AUTHOR_DISPLAY_NAME = "Sample Author";
    public static final String DEFAULT_AUTHOR_GITHUB_ID = "Sample Author Github ID";
    public static final LocalDate DEFAULT_START_DATE = LocalDate.of(2020, 1, 1);
    public static final LocalDate DEFAULT_END_DATE = LocalDate.of(9999, 12, 31);

    private String repoUrl;
    private String reportTitle;
    private String authorDisplayName;
    private String authorGithubId;
    private LocalDate startDate;
    private LocalDate endDate;

    public String getReportTitle() {
        return reportTitle == null ? DEFAULT_TITLE : reportTitle;
    }

    public String getAuthorDisplayName() {
        return authorDisplayName == null ? DEFAULT_AUTHOR_DISPLAY_NAME : authorDisplayName;
    }

    public String getAuthorGithubId() {
        return authorGithubId == null ? DEFAULT_AUTHOR_GITHUB_ID : authorGithubId;
    }

    public LocalDate getStartDate() {
        return startDate == null ? DEFAULT_START_DATE : startDate;
    }

    public LocalDate getEndDate() {
        return endDate == null ? DEFAULT_END_DATE : endDate;
    }

    public String getRepoUrl() {
        return repoUrl == null ? DEFAULT_REPO_URL : repoUrl;
    }
}
