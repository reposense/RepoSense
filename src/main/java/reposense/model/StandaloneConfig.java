package reposense.model;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents the structure of a config.json in _reposense folder.
 */
public class StandaloneConfig {
    private List<StandaloneAuthor> authors;
    private List<String> ignoreGlobList;
    private List<String> formats;
    private List<String> ignoreCommitList;

    public List<StandaloneAuthor> getAuthors() {
        if (authors == null) {
            return Collections.emptyList();
        }

        authors.removeIf(Objects::isNull);
        return authors;
    }

    public List<String> getIgnoreGlobList() {
        if (ignoreGlobList == null) {
            return Collections.emptyList();
        }

        ignoreGlobList.removeIf(Objects::isNull);
        return ignoreGlobList;
    }

    public List<String> getFormats() {
        if (formats == null) {
            return Format.DEFAULT_FORMAT_STRINGS;
        }

        formats.removeIf(Objects::isNull);
        return formats;
    }

    public List<String> getIgnoreCommitList() {
        if (ignoreCommitList == null) {
            return Collections.emptyList();
        }

        ignoreCommitList.removeIf(Objects::isNull);
        return ignoreCommitList;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof StandaloneConfig)) {
            return false;
        }

        StandaloneConfig otherStandaloneConfig = (StandaloneConfig) other;
        return authors.equals(otherStandaloneConfig.authors)
                && getIgnoreGlobList().equals(otherStandaloneConfig.getIgnoreGlobList())
                && getFormats().equals(otherStandaloneConfig.getFormats())
                && getIgnoreCommitList().equals(otherStandaloneConfig.getIgnoreCommitList());
    }
}
