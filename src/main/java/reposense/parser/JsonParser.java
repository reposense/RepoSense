package reposense.parser;

import java.io.FileNotFoundException;
import java.io.FileReader;
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
     * @throws FileNotFoundException if {@code path} is invalid.
     */
    public abstract T parse(Path path) throws FileNotFoundException;

    protected T fromJson(Path path) throws FileNotFoundException {
        return fromJson(new Gson(), path, getType());
    }

    protected T fromJson(Gson gson, Path path, Type type) throws FileNotFoundException {
        JsonReader jsonReader = new JsonReader(new FileReader(path.toString()));
        return gson.fromJson(jsonReader, type);
    }
}
