package reposense.wizard;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

/**
 * Utility class to write RepoSense configuration files.
 */
public class ConfigFileWriter {

    /**
     * Writes report configuration to a YAML file.
     *
     * @throws IOException if there is an error writing to the file.
     */
    public static void writeReportConfig(Map<String, Object> config, Path outputPath) throws IOException {
        Files.createDirectories(outputPath.getParent());
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.writeValue(outputPath.toFile(), config);
    }
}
