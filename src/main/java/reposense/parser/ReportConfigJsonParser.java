package reposense.parser;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;

import com.google.gson.reflect.TypeToken;

import reposense.model.ReportConfiguration;

/**
 * Parses json file from {@code Path} and creates a new {@code ReportConfiguration} object.
 */
public class ReportConfigJsonParser extends JsonParser<ReportConfiguration> {
    public static final String REPORT_CONFIG_FILENAME = "report-config.json";

    /**
     * Gets the type of {@code ReportConfiguration} for json conversion.
     */
    @Override
    public Type getType() {
        return new TypeToken<ReportConfiguration>(){}.getType();
    }

    /**
     * Converts json file from the given {@code path} and returns a {@code ReportConfiguration} object.
     * @throws IOException if {@code path} is invalid.
     */
    @Override
    public ReportConfiguration parse(Path path) throws IOException {
        return fromJson(path);
    }
}
