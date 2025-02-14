package reposense.model.reportconfig;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents an author's details in the report-config.yaml file.
 */
public class ReportAuthorDetails {
    public static final List<ReportAuthorDetails> DEFAULT_INSTANCES = new ArrayList<>();

    public static final List<String> DEFAULT_AUTHOR_EMAIL = List.of();
    public static final String DEFAULT_GIT_HOST_ID = "";
    public static final String DEFAULT_DISPLAY_NAME = "";
    public static final String DEFAULT_GIT_AUTHOR_NAME = "";

    private static final List<String> DEFAULT_AUTHOR_EMAIL_1 = List.of("1229983126@qq.com");
    private static final String DEFAULT_GIT_HOST_ID_1 = "fzdy1914";
    private static final String DEFAULT_DISPLAY_NAME_1 = "WANG CHAO";
    private static final String DEFAULT_GIT_AUTHOR_NAME_1 = "WANG CHAO";
    private static final List<String> DEFAULT_AUTHOR_EMAIL_2 = List.of("123@gmail.com");
    private static final String DEFAULT_GIT_HOST_ID_2 = "FH-30";
    private static final String DEFAULT_DISPLAY_NAME_2 = "Francis Hodianto";
    private static final String DEFAULT_GIT_AUTHOR_NAME_2 = "Francis Hodianto";

    static {
        ReportAuthorDetails rad1 = new ReportAuthorDetails();
        ReportAuthorDetails rad2 = new ReportAuthorDetails();

        rad1.authorEmails = DEFAULT_AUTHOR_EMAIL_1;
        rad1.authorGitHostId = DEFAULT_GIT_HOST_ID_1;
        rad1.authorGitAuthorName = DEFAULT_GIT_AUTHOR_NAME_1;
        rad1.authorDisplayName = DEFAULT_DISPLAY_NAME_1;

        rad2.authorEmails = DEFAULT_AUTHOR_EMAIL_2;
        rad2.authorGitHostId = DEFAULT_GIT_HOST_ID_2;
        rad2.authorGitAuthorName = DEFAULT_GIT_AUTHOR_NAME_2;
        rad2.authorDisplayName = DEFAULT_DISPLAY_NAME_2;
    }

    @JsonProperty("author-emails")
    private List<String> authorEmails;

    @JsonProperty("author-git-host-id")
    private String authorGitHostId;

    @JsonProperty("author-display-name")
    private String authorDisplayName;

    @JsonProperty("author-git-author-name")
    private String authorGitAuthorName;

    public ReportAuthorDetails() {

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

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (obj instanceof ReportAuthorDetails) {
            ReportAuthorDetails rad = (ReportAuthorDetails) obj;
            return rad.getAuthorEmails().equals(this.getAuthorEmails())
                    && rad.getAuthorGitHostId().equals(this.getAuthorGitHostId())
                    && rad.getAuthorDisplayName().equals(this.getAuthorDisplayName())
                    && rad.getAuthorGitAuthorName().equals(this.getAuthorGitAuthorName());
        }

        return false;
    }
}
