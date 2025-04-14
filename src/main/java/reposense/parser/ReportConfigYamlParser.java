package reposense.parser;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.logging.Level;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import reposense.model.reportconfig.ReportConfiguration;

/**
 * YAML Parser for report-config.yaml files.
 */
public class ReportConfigYamlParser extends YamlParser<ReportConfiguration> {
    public static final String REPORT_CONFIG_FILENAME = "report-config.yaml";
    public static final String REPORT_CONFIG_PARSED_SUCCESSFULLY_MESSAGE =
            "report-config.yaml file parsed successfully!";

    @Override
    public Type getType() {
        return ReportConfiguration.class;
    }

    /**
     * Parses the YAML file at {@code path}.
     *
     * @param path Path to the YAML file
     * @return Parsed {@code ReportConfiguration} object
     * @throws IOException if the provided path is invalid
     */
    @Override
    public ReportConfiguration parse(Path path) throws IOException {
        // adapted from https://www.baeldung.com/jackson-yaml
        ReportConfiguration reportConfiguration;

        logger.log(Level.INFO, "Parsing report-config.yaml file...");
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.findAndRegisterModules();
        reportConfiguration = mapper.readValue(new File(path.toString()), ReportConfiguration.class);
        logger.log(Level.INFO, REPORT_CONFIG_PARSED_SUCCESSFULLY_MESSAGE);

        return reportConfiguration;
    }
}
