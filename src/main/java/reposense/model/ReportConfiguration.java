package reposense.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Class that contains information on a report's configurations.
 * This class is used mainly for quickly setting up one's personal code portfolio.
 */
public class ReportConfiguration {
    public static final String DEFAULT_REPO_URL = "github.com/user/repo";
    public static final String DEFAULT_TITLE = "RepoSense Report";
    public static final String DEFAULT_AUTHOR_DISPLAY_NAME = "Sample Author";
    public static final String DEFAULT_AUTHOR_GITHUB_ID = "Sample Author Github ID";
    public static final List<Map<String, String>> DEFAULT_BRANCHES_WITH_BLURBS = new ArrayList<>();
    public static final LocalDate DEFAULT_START_DATE = LocalDate.of(2020, 1, 1);
    public static final LocalDate DEFAULT_END_DATE = LocalDate.of(9999, 12, 31);

    private static final HashMap<String, String> DEFAULT_BRANCH_ONE = new HashMap<>();
    private static final HashMap<String, String> DEFAULT_BRANCH_TWO = new HashMap<>();

    @JsonProperty("repoUrl")
    private String repoUrl;

    @JsonProperty("reportTitle")
    private String reportTitle;

    @JsonProperty("authorDisplayName")
    private String authorDisplayName;

    @JsonProperty("authorGithubId")
    private String authorGithubId;

    @JsonProperty("startDate")
    private LocalDate startDate;

    @JsonProperty("endDate")
    private LocalDate endDate;

    @JsonProperty("branches")
    private List<Map<String, String>> branchesWithBlurbs;

    // SIB to set the default values in the maps
    static {
        DEFAULT_BRANCH_ONE.put("name", "Branch 1");
        DEFAULT_BRANCH_ONE.put("blurb", "Blurb 1");
        DEFAULT_BRANCH_TWO.put("name", "Branch 2");
        DEFAULT_BRANCH_TWO.put("blurb", "Blurb 2");
        DEFAULT_BRANCHES_WITH_BLURBS.add(DEFAULT_BRANCH_ONE);
        DEFAULT_BRANCHES_WITH_BLURBS.add(DEFAULT_BRANCH_TWO);
    }

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

    public List<Map<String, String>> getBranchesWithBlurbs() {
        return branchesWithBlurbs == null ? DEFAULT_BRANCHES_WITH_BLURBS : branchesWithBlurbs;
    }
}
