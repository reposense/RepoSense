package reposense.parser;

import java.io.FileNotFoundException;
import java.nio.file.Path;

import reposense.model.StandaloneConfig;

public class StandaloneConfigJsonParser extends JsonParser<StandaloneConfig> {
    @Override
    public StandaloneConfig parse(Path path) throws FileNotFoundException {
        return fromJson(path);
    }
}
