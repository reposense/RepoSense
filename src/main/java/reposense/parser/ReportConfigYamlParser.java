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
        ReportConfiguration reportConfigation;

        try {
            logger.log(Level.INFO, "Parsing report-config.yaml file...");
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            mapper.findAndRegisterModules();
            reportConfigation = mapper.readValue(new File(path.toString()), ReportConfiguration.class);
            logger.log(Level.INFO, "report-config.yaml file parsed successfully!");
        } catch (IOException ioe) {
            // if the parse fails for any reason, the default config file is used instead
            logger.log(Level.WARNING, "Error parsing report-config.yaml: " + ioe.getMessage(), ioe);
            reportConfigation = new ReportConfiguration();
        }

        return reportConfigation;
    }
}
