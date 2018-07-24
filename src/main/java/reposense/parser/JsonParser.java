package reposense.parser;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.nio.file.Path;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

public abstract class JsonParser<T> {
    public abstract T parse(Path path) throws FileNotFoundException;

    protected T fromJson(Path path) throws FileNotFoundException {
        Type type = new TypeToken<T>(){}.getType();
        return fromJson(new Gson(), path, type);
    }

    protected T fromJson(Gson gson, Path path, Type type) throws FileNotFoundException {
        JsonReader jsonReader = new JsonReader(new FileReader(path.toString()));
        return gson.fromJson(jsonReader, type);
    }
}
