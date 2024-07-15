package reposense.model;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents the mapping between the repo URL to the associated blurb.
 */
public class BlurbMap {
    @JsonProperty("urlBlurbMap")
    private final Map<String, String> urlBlurbMap;

    public BlurbMap() {
        this.urlBlurbMap = new HashMap<>();
    }

    /**
     * Return a copy of the mapping between the repo URL to the associated blurb.
     *
     * @return a {@code Map<String, String>} containing the mapping between the repo URL to the associated blurb.
     */
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
            return bm.urlBlurbMap.equals(this.urlBlurbMap);
        }

        return false;
    }
}
