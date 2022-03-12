package reposense.parser;

import reposense.model.SupportedDomainUrlMap;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;

public class SupportedDomainUrlMapJsonParser extends JsonParser<SupportedDomainUrlMap> {

    @Override
    public Type getType() {
        return SupportedDomainUrlMap.class;
    }

    @Override
    public SupportedDomainUrlMap parse(Path path) throws IOException {
        return fromJson(path);
    }
}
