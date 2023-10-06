package reposense.model;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Represents an author in {@link StandaloneConfig}.
 */
public class StandaloneAuthor {

    private String gitId;
    // This is for backward compatibility with older standalone author configs that utilize the 'githubId' keyword.
    private String githubId;

    private List<String> emails;
    private String displayName;
    private List<String> authorNames;
    private List<String> ignoreGlobList;

    public String getGitId() {
        // This supports a standalone json file containing either keywords, prioritizing 'gitId'.
        // To remove backward compatibility support, need to update the standalone json configs in test repositories.
        return Optional.ofNullable(gitId).orElse(githubId);
    }

    public List<String> getEmails() {
        return (emails == null) ? Collections.emptyList() : emails;
    }

    public String getDisplayName() {
        return (displayName == null) ? "" : displayName;
    }

    public List<String> getAuthorNames() {
        return (authorNames == null) ? Collections.emptyList() : authorNames;
    }

    public List<String> getIgnoreGlobList() {
        return (ignoreGlobList == null) ? Collections.emptyList() : ignoreGlobList;
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
        return gitId.equals(otherStandaloneAuthor.gitId)
                && getEmails().equals(otherStandaloneAuthor.getEmails())
                && getDisplayName().equals(otherStandaloneAuthor.getDisplayName())
                && getAuthorNames().equals(otherStandaloneAuthor.getAuthorNames())
                && getIgnoreGlobList().equals(otherStandaloneAuthor.getIgnoreGlobList());
    }
}
