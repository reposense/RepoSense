package reposense.model;

import java.util.List;

/**
 * Represents the structure of a config.json in _reposense folder.
 */
public class StandaloneConfig {
    private List<StandaloneAuthor> authors;

    public StandaloneConfig(List<StandaloneAuthor> authors) {
        this.authors = authors;
    }

    public List<StandaloneAuthor> getAuthors() {
        return authors;
    }
}
