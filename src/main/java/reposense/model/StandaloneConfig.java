package reposense.model;

import java.util.List;

public class StandaloneConfig {
    private List<StandaloneAuthor> authors;

    public StandaloneConfig(List<StandaloneAuthor> authors) {
        this.authors = authors;
    }

    public List<StandaloneAuthor> getAuthors() {
        return authors;
    }
}
