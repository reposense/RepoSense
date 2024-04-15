package reposense.model.reportconfig;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a single repository configuration in the overall report
 * configuration.
 */
public class ReportRepoConfiguration {
    public static final String DEFAULT_REPO = "https://github.com/user/repo";
    public static final List<String> DEFAULT_AUTHOR_EMAIL = List.of(
            "john@john.com", "johny@mail.com", "j@domain.com"
    );
    public static final String DEFAULT_GIT_HOST_ID = "johnDoe";
    public static final String DEFAULT_DISPLAY_NAME = "John Doe";
    public static final String DEFAULT_GIT_AUTHOR_NAME = "my home PC";
    public static final List<ReportBranchData> DEFAULT_BRANCHES = List.of(
            ReportBranchData.DEFAULT_INSTANCE
    );
    public static final ReportRepoConfiguration DEFAULT_INSTANCE = new ReportRepoConfiguration();

    static {
        DEFAULT_INSTANCE.repo = DEFAULT_REPO;
        DEFAULT_INSTANCE.authorEmails = DEFAULT_AUTHOR_EMAIL;
        DEFAULT_INSTANCE.authorGitHostId = DEFAULT_GIT_HOST_ID;
        DEFAULT_INSTANCE.authorDisplayName = DEFAULT_DISPLAY_NAME;
        DEFAULT_INSTANCE.authorGitAuthorName = DEFAULT_GIT_AUTHOR_NAME;
        DEFAULT_INSTANCE.branches = DEFAULT_BRANCHES;
    }

    @JsonProperty("repo")
    private String repo;

    @JsonProperty("author-emails")
    private List<String> authorEmails;

    @JsonProperty("author-git-host-id")
    private String authorGitHostId;

    @JsonProperty("author-display-name")
    private String authorDisplayName;

    @JsonProperty("author-git-author-name")
    private String authorGitAuthorName;

    @JsonProperty("branches")
    private List<ReportBranchData> branches;

    public String getRepo() {
        return repo == null ? DEFAULT_REPO : repo;
    }

    public List<String> getAuthorEmails() {
        return authorEmails == null ? DEFAULT_AUTHOR_EMAIL : authorEmails;
    }

    public String getAuthorGitHostId() {
        return authorGitHostId == null ? DEFAULT_GIT_HOST_ID : authorGitHostId;
    }

    public String getAuthorDisplayName() {
        return authorDisplayName == null ? DEFAULT_DISPLAY_NAME : authorDisplayName;
    }

    public String getAuthorGitAuthorName() {
        return authorGitAuthorName == null ? DEFAULT_GIT_AUTHOR_NAME : authorGitAuthorName;
    }

    public List<ReportBranchData> getBranches() {
        return branches == null ? DEFAULT_BRANCHES : branches;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (obj instanceof ReportRepoConfiguration) {
            ReportRepoConfiguration rrc = (ReportRepoConfiguration) obj;
            return rrc.getRepo().equals(this.getRepo())
                    && rrc.getAuthorEmails().equals(this.getAuthorEmails())
                    && rrc.getAuthorGitHostId().equals(this.getAuthorGitHostId())
                    && rrc.getAuthorDisplayName().equals(this.getAuthorDisplayName())
                    && rrc.getAuthorGitAuthorName().equals(this.getAuthorGitAuthorName())
                    && rrc.getBranches().equals(this.getBranches());
        }

        return false;
    }
}
