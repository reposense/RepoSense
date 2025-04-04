package reposense.util.adapters;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * Overrides the custom gson serializer for LocalDateTine type object.
 */
public class DateSerializer implements JsonSerializer<LocalDateTime> {
    private static final String GITHUB_API_DATE_FORMAT = "yyyy-MM-dd";
    @Override
    public JsonElement serialize(LocalDateTime date, Type typeofDate, JsonSerializationContext ctx) {
        return new JsonPrimitive(date.format(DateTimeFormatter.ofPattern(GITHUB_API_DATE_FORMAT)));
    }
}
