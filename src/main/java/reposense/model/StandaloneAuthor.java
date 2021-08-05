package reposense.model;

import java.util.Collections;
import java.util.List;

/**
 * Represents an author in {@code StandaloneConfig}.
 */
public class StandaloneAuthor {
    private String githubId;
    private List<String> emails;
    private String displayName;
    private List<String> authorNames;
    private List<String> ignoreGlobList;

    public String getGithubId() {
        return githubId;
    }

    public List<String> getEmails() {
        return emails == null ? Collections.emptyList() : emails;
    }

    public String getDisplayName() {
        return displayName == null ? "" : displayName;
    }

    public List<String> getAuthorNames() {
        return authorNames == null ? Collections.emptyList() : authorNames;
    }

    public List<String> getIgnoreGlobList() {
        return ignoreGlobList == null ? Collections.emptyList() : ignoreGlobList;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof StandaloneAuthor)) {
            return false;
        }

        StandaloneAuthor otherStandaloneAuthor = (StandaloneAuthor) other;
        return githubId.equals(otherStandaloneAuthor.githubId)
                && getEmails().equals(otherStandaloneAuthor.getEmails())
                && getDisplayName().equals(otherStandaloneAuthor.getDisplayName())
                && getAuthorNames().equals(otherStandaloneAuthor.getAuthorNames())
                && getIgnoreGlobList().equals(otherStandaloneAuthor.getIgnoreGlobList());
    }
}
