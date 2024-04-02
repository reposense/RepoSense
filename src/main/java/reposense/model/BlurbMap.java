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

    private BlurbMap() {
        this.urlBlurbMap = new HashMap<>();
    }

    /**
     * Builder class to help in the building of a {@code BlurbMap}.
     */
    public static final class Builder {
        private BlurbMap blurbMap;

        public Builder() {
            blurbMap = new BlurbMap();
        }


        /**
         * Adds a key-value record into the {@code BlurbMap}.
         *
         * @param key Key value.
         * @param value Blurb value.
         * @return This {@code Builder}.
         */
        public Builder withRecord(String key, String value) {
            blurbMap.urlBlurbMap.put(key, value);
            return this;
        }

        /**
         * Returns a built instance of {@code BlurbMap}.
         *
         * @return Built {@code BlurbMap}.
         */
        public BlurbMap build() {
            BlurbMap built = this.blurbMap;
            this.blurbMap = new BlurbMap();
            return built;
        }
    }
}
