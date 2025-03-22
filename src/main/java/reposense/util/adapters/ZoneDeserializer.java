package reposense.util.adapters;

import java.lang.reflect.Type;
import java.time.ZoneId;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;

/**
 * Deserializes a {@link ZoneId} object from a json {@link JsonElement} object.
 */
public class ZoneDeserializer implements JsonDeserializer<ZoneId> {
    @Override
    public ZoneId deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        return ZoneId.of(json.getAsString());
    }
}
