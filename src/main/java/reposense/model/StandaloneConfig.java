package reposense.model;

import java.util.Collections;
import java.util.List;

/**
 * Represents the structure of a config.json in _reposense folder.
 */
public class StandaloneConfig {
    private List<StandaloneAuthor> authors;
    private List<String> ignoreGlobList;
    private List<String> ignoreCommitList;

    public List<StandaloneAuthor> getAuthors() {
        return authors;
    }

    public List<String> getIgnoreGlobList() {
        if (ignoreGlobList == null) {
            return Collections.emptyList();
        }

        return ignoreGlobList;
    }

    public List<String> getIgnoreCommitList() {
        if (ignoreCommitList == null) {
            return Collections.emptyList();
        }

        return ignoreCommitList;
    }
}
