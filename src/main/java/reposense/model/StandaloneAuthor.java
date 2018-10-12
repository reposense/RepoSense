package reposense.model;

import java.util.Collections;
import java.util.List;

/**
 * Represents an author in {@code StandaloneConfig}.
 */
public class StandaloneAuthor {
    private String githubId;
    private String displayName;
    private List<String> authorNames;
    private List<String> ignoreGlobList;

    public String getGithubId() {
        return githubId;
    }

    public String getDisplayName() {
        if (displayName == null) {
            return "";
        }

        return displayName;
    }

    public List<String> getAuthorNames() {
        if (authorNames == null) {
            return Collections.emptyList();
        }

        return authorNames;
    }

    public List<String> getIgnoreGlobList() {
        if (ignoreGlobList == null) {
            return Collections.emptyList();
        }

        return ignoreGlobList;
    }
}
