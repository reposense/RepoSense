package reposense.model;

import java.util.Map;

/**
 * Represents the mapping between the key to the associated blurb.
 */

public interface BlurbMap {

    public Map<String, String> getAllMappings();

    /**
     * Adds a key-value record into the {@code BlurbMap}.
     *
     * @param key Key value.
     * @param value Blurb value.
     */
    void withRecord(String key, String value);
}
