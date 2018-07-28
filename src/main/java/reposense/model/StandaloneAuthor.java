package reposense.model;

import java.util.Collections;
import java.util.List;

/**
 * Represents an author in {@code StandaloneConfig}.
 */
public class StandaloneAuthor {
    private String githubId;
    private String displayName;
    private String[] authorNames;
    private List<String> ignoreGlobList;

    public StandaloneAuthor(String githubId, String displayName, String[] authorNames, List<String> ignoreGlobList) {
        this.githubId = githubId;
        this.displayName = displayName;
        this.authorNames = authorNames;
        this.ignoreGlobList = ignoreGlobList;
    }

    public String getGithubId() {
        return githubId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String[] getAuthorNames() {
        if (authorNames == null) {
            return new String[]{};
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
