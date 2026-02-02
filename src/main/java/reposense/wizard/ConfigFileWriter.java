package reposense.wizard;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

/**
 * Utility class to write RepoSense configuration files.
 */
public class ConfigFileWriter {

    /**
     * Writes repository configurations to a CSV file.
     *
     * @throws IOException if there is an error writing to the file.
     */
    public static void writeRepoConfig(List<Map<String, Object>> repos, Path outputPath) throws IOException {
        Files.createDirectories(outputPath.getParent());
        try (CSVPrinter printer = new CSVPrinter(new FileWriter(outputPath.toFile()), CSVFormat.DEFAULT.builder()
                .setHeader("Repository's Location", "Branch", "File formats", "Find Previous Authors",
                        "Ignore Glob List",
                        "Ignore standalone config", "Ignore Commits List", "Ignore Authors List", "Shallow Cloning",
                        "File Size Limit", "Ignore File Size Limit", "Skip Ignored File Analysis", "Since Date",
                        "Until Date")
                .build())) {
            for (Map<String, Object> repo : repos) {
                printer.printRecord(
                        repo.getOrDefault("location", ""),
                        repo.getOrDefault("branch", ""),
                        repo.getOrDefault("fileFormats", ""),
                        toYes(repo.get("findPreviousAuthors")),
                        repo.getOrDefault("ignoreGlobList", ""),
                        toYes(repo.get("ignoreStandaloneConfig")),
                        repo.getOrDefault("ignoreCommitsList", ""),
                        repo.getOrDefault("ignoreAuthorsList", ""),
                        toYes(repo.get("shallowCloning")),
                        repo.getOrDefault("fileSizeLimit", ""),
                        toYes(repo.get("ignoreFileSizeLimit")),
                        toYes(repo.get("skipIgnoredFileAnalysis")),
                        repo.getOrDefault("sinceDate", ""),
                        repo.getOrDefault("untilDate", ""));
            }
        }
    }

    /**
     * Writes author configurations to a CSV file.
     *
     * @throws IOException if there is an error writing to the file.
     */
    public static void writeAuthorConfig(List<Map<String, Object>> authors, Path outputPath) throws IOException {
        Files.createDirectories(outputPath.getParent());
        try (CSVPrinter printer = new CSVPrinter(new FileWriter(outputPath.toFile()), CSVFormat.DEFAULT.builder()
                .setHeader("Repository's Location", "Branch", "Author's Git Host ID", "Author's Emails",
                        "Author's Display Name", "Author's Git Author Name", "Ignore Glob List")
                .build())) {
            for (Map<String, Object> author : authors) {
                printer.printRecord(
                        author.getOrDefault("location", ""),
                        author.getOrDefault("branch", ""),
                        author.getOrDefault("gitHostId", ""),
                        author.getOrDefault("emails", ""),
                        author.getOrDefault("displayName", ""),
                        author.getOrDefault("gitAuthorNames", ""),
                        author.getOrDefault("ignoreGlobList", ""));
            }
        }
    }

    /**
     * Writes group configurations to a CSV file.
     *
     * @throws IOException if there is an error writing to the file.
     */
    public static void writeGroupConfig(List<Map<String, Object>> groups, Path outputPath) throws IOException {
        Files.createDirectories(outputPath.getParent());
        try (CSVPrinter printer = new CSVPrinter(new FileWriter(outputPath.toFile()), CSVFormat.DEFAULT.builder()
                .setHeader("Repository's Location", "Group Name", "Globs")
                .build())) {
            for (Map<String, Object> group : groups) {
                printer.printRecord(
                        group.getOrDefault("location", ""),
                        group.getOrDefault("name", ""),
                        group.getOrDefault("globs", ""));
            }
        }
    }

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

    private static String toYes(Object val) {
        if (val instanceof Boolean && (Boolean) val) {
            return "yes";
        }
        if (val instanceof String && ((String) val).equalsIgnoreCase("true")) {
            return "yes";
        }
        return "";
    }
}
