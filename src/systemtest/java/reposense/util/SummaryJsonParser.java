package reposense.util;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;

import com.google.gson.reflect.TypeToken;

import reposense.parser.JsonParser;
import reposense.report.SummaryJson;

public class SummaryJsonParser extends JsonParser<SummaryJson> {

    @Override
    public Type getType() {
        return new TypeToken<SummaryJson>() {}.getType();
    }

    @Override
    public SummaryJson parse(Path path) throws IOException {
        return fromJson(path);
    }
}
