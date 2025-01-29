package reposense.util.adapters;

import java.lang.reflect.Type;
import java.time.ZoneId;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * Overrides the custom gson serializer for ZoneId type object.
 */
public class ZoneSerializer implements JsonSerializer<ZoneId> {
    @Override
    public JsonElement serialize(ZoneId z, Type t, JsonSerializationContext ctx) {
        return new JsonPrimitive(z.toString());
    }
}
