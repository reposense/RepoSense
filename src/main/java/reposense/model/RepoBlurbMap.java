package reposense.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.Map;
/**
 * Represents the mapping between the repo URL to the associated blurb.
 */
public class RepoBlurbMap implements BlurbMap{
    @JsonProperty("urlBlurbMap")
    private final Map<String, String> urlBlurbMap;

    public RepoBlurbMap() {
        this.urlBlurbMap = new HashMap<>();
    }

    public Map<String, String> getAllMappings() {
        return new HashMap<>(this.urlBlurbMap);
    }

    /**
     * Adds a key-value record into the {@code BlurbMap}.
     *
     * @param key Key value.
     * @param value Blurb value.
     */
    public void withRecord(String key, String value) {
        this.urlBlurbMap.put(key, value);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (obj instanceof BlurbMap) {
            BlurbMap bm = (BlurbMap) obj;
            return bm.equals(this.urlBlurbMap);
        }

        return false;
    }
}
