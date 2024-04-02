package reposense.parser;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.gson.Gson;

import reposense.model.reportconfig.ReportConfiguration;
import reposense.system.LogsManager;

/**
 * YAML Parser for report-config.yaml files.
 */
public class ReportConfigYamlParser extends JsonParser<ReportConfiguration> {
    public static final String REPORT_CONFIG_FILENAME = "report-config.yaml";
    private static final Logger logger = LogsManager.getLogger(ReportConfigYamlParser.class);

    @Override
    public Type getType() {
        return ReportConfiguration.class;
    }

    @Override
    public ReportConfiguration parse(Path path) throws IOException {
        return this.fromJson(null, path, null);
    }

    @Override
    protected ReportConfiguration fromJson(Path path) throws IOException {
        return this.fromJson(null, path, null);
    }

    @Override
    protected ReportConfiguration fromJson(Gson gson, Path path, Type type) throws IOException , JsonMappingException {
        // adapted from https://www.baeldung.com/jackson-yaml
        ReportConfiguration reportConfigation;

        try {
            logger.log(Level.INFO, "Parsing report-config.yaml file...");
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            mapper.findAndRegisterModules();
            reportConfigation = mapper.readValue(new File(path.toString()), ReportConfiguration.class);
            System.out.println(reportConfigation);
            logger.log(Level.INFO, "report-config.yaml file parsed successfully!");
        } catch (IOException ioe) {
            // if the parse fails for any reason, the default config file is used instead
            logger.log(Level.WARNING, "Error parsing report-config.yaml: " + ioe.getMessage(), ioe);
            reportConfigation = new ReportConfiguration();
        }

        return reportConfigation;
    }
}
