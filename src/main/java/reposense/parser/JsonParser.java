package reposense.parser;

import static reposense.util.StringsUtil.decodeString;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

/**
 * Represents a {@code JsonParser} that is able to parse json file from a {@code Path} into an object of type {@code T}.
 */
public abstract class JsonParser<T> {

    /**
     * Gets the type of {@code T} for json conversion.
     */
    public abstract Type getType();

    /**
     * Converts json file from the given {@code path} into an object and returns it.
     * @throws IOException if {@code path} is invalid.
     */
    public abstract T parse(Path path) throws IOException;

    protected T fromJson(Path path) throws IOException {
        return fromJson(new Gson(), path, getType());
    }

    protected T fromJson(Gson gson, Path path, Type type) throws IOException {
        try (JsonReader jsonReader = new JsonReader(new FileReader(decodeString(path)))) {
            return gson.fromJson(jsonReader, type);
        }
    }
}
