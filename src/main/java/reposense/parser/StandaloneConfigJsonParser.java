package reposense.parser;

import java.io.FileNotFoundException;
import java.lang.reflect.Type;
import java.nio.file.Path;

import com.google.gson.reflect.TypeToken;

import reposense.model.StandaloneConfig;

/**
 * Parses json file from {@code Path} and creates a new {@code StandaloneConfig} object.
 */
public class StandaloneConfigJsonParser extends JsonParser<StandaloneConfig> {

    /**
     * Gets the type of StandaloneConfig for json conversion.
     */
    @Override
    public Type getType() {
        return new TypeToken<StandaloneConfig>(){}.getType();
    }

    /**
     * Converts json file from the given {@code path} and returns a {@code StandaloneConfig} object.
     * @throws FileNotFoundException if {@code path} is invalid.
     */
    @Override
    public StandaloneConfig parse(Path path) throws FileNotFoundException {
        return fromJson(path);
    }
}
