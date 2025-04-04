package reposense.model;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents the mapping between the author to the associated blurb.
 */
public class AuthorBlurbMap extends AbstractBlurbMap {
    @JsonProperty("authorBlurbMap")
    private final Map<String, String> authorBlurbMap;

    public AuthorBlurbMap() {
        super();
        this.authorBlurbMap = new HashMap<>();
    }
}
