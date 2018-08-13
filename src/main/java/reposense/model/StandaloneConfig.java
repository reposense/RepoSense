package reposense.model;

import java.util.Collections;
import java.util.List;

/**
 * Represents the structure of a config.json in _reposense folder.
 */
public class StandaloneConfig {
    private List<StandaloneAuthor> authors;
    private List<String> ignoreGlobList;
    private List<String> formats;

    public List<StandaloneAuthor> getAuthors() {
        return authors;
    }

    public List<String> getIgnoreGlobList() {
        if (ignoreGlobList == null) {
            return Collections.emptyList();
        }

        return ignoreGlobList;
    }

    public List<String> getFormats() {
        if (formats == null) {
            return Collections.emptyList();
        }

        return formats;
    }
}
