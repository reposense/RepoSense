package reposense.model;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import reposense.parser.ArgsParser;

/**
 * Represents the structure of a config.json in _reposense folder.
 */
public class StandaloneConfig {
    private List<StandaloneAuthor> authors;
    private List<String> ignoreGlobList;
    private List<String> formats;
    private List<String> ignoreCommitList;

    /**
     * Sets default values to fields, if they are not provided.
     * Also, removes null elements from given lists.
     */
    public void initialize() {
        if (authors == null) {
            authors = Collections.emptyList();
        }

        if (ignoreGlobList == null) {
            ignoreGlobList = Collections.emptyList();
        }

        if (formats == null) {
            formats = ArgsParser.DEFAULT_FORMATS;
        }

        if (ignoreCommitList == null) {
            ignoreCommitList = Collections.emptyList();
        }

        authors.removeIf(Objects::isNull);
        ignoreGlobList.removeIf(Objects::isNull);
        formats.removeIf(Objects::isNull);
        ignoreCommitList.removeIf(Objects::isNull);
    }

    public List<StandaloneAuthor> getAuthors() {
        return authors;
    }

    public List<String> getIgnoreGlobList() {
        return ignoreGlobList;
    }

    public List<String> getFormats() {
        return formats;
    }

    public List<String> getIgnoreCommitList() {
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
