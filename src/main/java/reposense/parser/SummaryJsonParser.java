package reposense.parser;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneId;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import reposense.report.SummaryJson;
import reposense.util.adapters.DateDeserializer;
import reposense.util.adapters.ZoneDeserializer;

/**
 * Parses json file from {@link Path} and creates a new {@link SummaryJson} object.
 */
public class SummaryJsonParser extends JsonParser<SummaryJson> {

    /**
     * Gets the type of {@link SummaryJson} for json conversion.
     */
    @Override
    public Type getType() {
        return new TypeToken<SummaryJson>(){}.getType();
    }

    /**
     * Converts json file from the given {@code path} and returns a {@link SummaryJson} object.
     *
     * @throws IOException if {@code path} is invalid.
     */
    @Override
    public SummaryJson parse(Path path) throws IOException {
        Gson gson = new GsonBuilder()
                .registerTypeHierarchyAdapter(LocalDateTime.class, new DateDeserializer())
                .registerTypeHierarchyAdapter(ZoneId.class, new ZoneDeserializer())
                .create();

        try (JsonReader jsonReader = new JsonReader(new FileReader(path.toString()))) {
            return gson.fromJson(jsonReader, getType());
        }
    }
}
