package reposense.model.reportconfig;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents an author's details in the report-config.yaml file.
 */
public class ReportAuthorDetails {
    private final List<String> authorEmails;
    private final String authorGitHostId;
    private final String authorDisplayName;
    private final String authorGitAuthorName;

    @JsonCreator
    public ReportAuthorDetails(
            @JsonProperty("author-emails") List<String> authorEmails,
            @JsonProperty("author-git-host-id") String authorGitHostId,
            @JsonProperty("author-display-name") String authorDisplayName,
            @JsonProperty("author-git-author-name") String authorGitAuthorName) {
        if (authorGitHostId == null) {
            throw new IllegalArgumentException("Author Git Host ID cannot be empty.");
        }
        this.authorGitHostId = authorGitHostId;
        this.authorEmails = authorEmails == null ? new ArrayList<>() : authorEmails;
        this.authorDisplayName = authorDisplayName == null ? "" : authorDisplayName;
        this.authorGitAuthorName = authorGitAuthorName == null ? "" : authorGitAuthorName;
    }

    public List<String> getAuthorEmails() {
        return authorEmails;
    }

    public String getAuthorGitHostId() {
        return authorGitHostId;
    }

    public String getAuthorDisplayName() {
        return authorDisplayName;
    }

    public String getAuthorGitAuthorName() {
        return authorGitAuthorName;
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

