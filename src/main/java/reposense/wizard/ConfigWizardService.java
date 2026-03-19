package reposense.wizard;

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;
import java.util.regex.PatternSyntaxException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import reposense.model.RepoLocation;
import reposense.parser.ReportConfigYamlParser;

/**
 * Business logic for the Config Wizard REST API.
 */
public class ConfigWizardService {

    /**
     * Validates a repository location string.
     *
     * @return empty Optional if valid, or Optional containing the error message
     */
    public Optional<String> validateLocation(String location) {
        try {
            new RepoLocation(location);
            return Optional.empty();
        } catch (Exception e) {
            return Optional.of(e.getMessage());
        }
    }

    /**
     * Validates a glob pattern.
     *
     * @return empty Optional if valid, or Optional containing the error message
     */
    public Optional<String> validateGlob(String pattern) {
        try {
            FileSystems.getDefault().getPathMatcher("glob:" + pattern);
            return Optional.empty();
        } catch (PatternSyntaxException e) {
            return Optional.of(e.getMessage());
        }
    }

    /**
     * Validates a config map by writing it to a temp file and parsing it.
     *
     * @return empty Optional if valid, or Optional containing the error message
     * @throws Exception if writing the temp file fails
     */
    public Optional<String> validateConfig(Map<String, Object> config) throws Exception {
        Path tempFile = Files.createTempFile("reposense-wizard-", ".yaml");
        try {
            ConfigFileWriter.writeReportConfig(config, tempFile);
            try {
                new ReportConfigYamlParser().parse(tempFile);
                return Optional.empty();
            } catch (Exception e) {
                String msg = e.getMessage() != null ? e.getMessage() : "Invalid configuration";
                return Optional.of(msg);
            }
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }

    /**
     * Generates a config file from {@code config} and returns its path.
     *
     * @throws Exception if the file cannot be written
     */
    public Path generateConfig(Map<String, Object> config) throws Exception {
        Path outputDir = Paths.get(System.getProperty("user.dir"), "generated-configs");
        Path outputPath = outputDir.resolve("report-config.yaml");
        ConfigFileWriter.writeReportConfig(config, outputPath);
        return outputPath;
    }

    /**
     * Returns a YAML preview string for {@code config}.
     *
     * @throws Exception if YAML serialisation fails
     */
    public String previewConfig(Map<String, Object> config) throws Exception {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        return mapper.writeValueAsString(config);
    }
}
