package reposense.model;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents the mapping between the author name to the associated blurb.
 */
public class AuthorBlurbMap implements BlurbMap {
    @JsonProperty("authorBlurbMap")
    private final Map<String, String> authorBlurbMap;

    public AuthorBlurbMap() {
        this.authorBlurbMap = new HashMap<>();
    }

    public Map<String, String> getAllMappings() {
        return new HashMap<>(this.authorBlurbMap);
    }

    /**
     * Adds a key-value record into the {@code BlurbMap}.
     *
     * @param key Key value.
     * @param value Blurb value.
     */
    public void withRecord(String key, String value) {
        this.authorBlurbMap.put(key, value);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (obj instanceof AuthorBlurbMap) {
            AuthorBlurbMap bm = (AuthorBlurbMap) obj;
            return bm.authorBlurbMap.equals(this.authorBlurbMap);
        }

        return false;
    }
}
