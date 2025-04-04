package reposense.model;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents the mapping between the repo URL to the associated blurb.
 */
public class RepoBlurbMap extends AbstractBlurbMap {
    @JsonProperty("urlBlurbMap")
    private final Map<String, String> urlBlurbMap;

    public RepoBlurbMap() {
        super();
        this.urlBlurbMap = new HashMap<>();
    }
}
