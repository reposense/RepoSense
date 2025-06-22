package reposense.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents the mapping between the key to the associated blurb.
 */
public abstract class AbstractBlurbMap implements BlurbMap {
    protected final Map<String, String> blurbMap;

    protected AbstractBlurbMap() {
        this.blurbMap = new HashMap<>();
    }

    @Override
    public Map<String, String> getAllMappings() {
        return new HashMap<>(this.blurbMap);
    }

    @Override
    public void withRecord(String key, String value) {
        this.blurbMap.put(key, value);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (obj instanceof AbstractBlurbMap) {
            AbstractBlurbMap bm = (AbstractBlurbMap) obj;
            return bm.blurbMap.equals(this.blurbMap);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return blurbMap.hashCode();
    }

    public String toString() {
        return blurbMap.toString();
    }
}
