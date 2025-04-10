package reposense.util.adapters;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;

/**
 * Deserializes a {@link LocalDateTime} object from a json {@link JsonElement} object.
 */
public class DateDeserializer implements JsonDeserializer<LocalDateTime> {
    private static final String GITHUB_API_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    @Override
    public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        return LocalDateTime.parse(json.getAsString(), DateTimeFormatter.ofPattern(GITHUB_API_DATE_FORMAT));
    }

}
