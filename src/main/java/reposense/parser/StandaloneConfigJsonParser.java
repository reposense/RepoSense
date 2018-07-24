package reposense.parser;

import java.io.FileNotFoundException;
import java.lang.reflect.Type;
import java.nio.file.Path;

import com.google.gson.reflect.TypeToken;

import reposense.model.StandaloneConfig;

public class StandaloneConfigJsonParser extends JsonParser<StandaloneConfig> {
    @Override
    public Type getType() {
        return new TypeToken<StandaloneConfig>(){}.getType();
    }

    @Override
    public StandaloneConfig parse(Path path) throws FileNotFoundException {
        return fromJson(path);
    }
}
