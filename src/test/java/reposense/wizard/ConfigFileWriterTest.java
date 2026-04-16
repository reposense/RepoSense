package reposense.wizard;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class ConfigFileWriterTest {

    @TempDir
    private Path tempDir;

    @Test
    public void writeReportConfig_validConfig_generatesCorrectYaml() throws IOException {
        Path outputPath = tempDir.resolve("report-config.yaml");
        Map<String, Object> config = new HashMap<>();
        config.put("title", "Test Report");

        ConfigFileWriter.writeReportConfig(config, outputPath);

        Assertions.assertTrue(Files.exists(outputPath));
        List<String> lines = Files.readAllLines(outputPath);
        Assertions.assertTrue(lines.stream().anyMatch(l -> l.contains("title: \"Test Report\"")));
    }

    @Test
    public void writeReportConfig_withReposAndAuthors_generatesCorrectYaml() throws IOException {
        Path outputPath = tempDir.resolve("report-config.yaml");

        Map<String, Object> author = new HashMap<>();
        author.put("gitId", "alice");
        author.put("displayName", "Alice Thompson");

        Map<String, Object> branch = new HashMap<>();
        branch.put("branch", "main");
        branch.put("authors", List.of(author));

        Map<String, Object> repo = new HashMap<>();
        repo.put("repo", "https://github.com/user/repo.git");
        repo.put("branches", List.of(branch));

        Map<String, Object> config = new HashMap<>();
        config.put("title", "Test Report");
        config.put("repos", List.of(repo));

        ConfigFileWriter.writeReportConfig(config, outputPath);

        Assertions.assertTrue(Files.exists(outputPath));
        String content = Files.readString(outputPath);
        Assertions.assertTrue(content.contains("title:"));
        Assertions.assertTrue(content.contains("https://github.com/user/repo.git"));
        Assertions.assertTrue(content.contains("alice"));
        Assertions.assertTrue(content.contains("main"));
    }

    @Test
    public void writeReportConfig_createsParentDirectories() throws IOException {
        Path outputPath = tempDir.resolve("nested/dir/report-config.yaml");
        Map<String, Object> config = new HashMap<>();
        config.put("title", "Test");

        ConfigFileWriter.writeReportConfig(config, outputPath);

        Assertions.assertTrue(Files.exists(outputPath));
    }

    @Test
    public void writeReportConfig_nullValues_omittedFromYaml() throws IOException {
        Path outputPath = tempDir.resolve("report-config.yaml");
        Map<String, Object> config = new HashMap<>();
        config.put("title", "Test Report");
        config.put("nullField", null);

        ConfigFileWriter.writeReportConfig(config, outputPath);

        String content = Files.readString(outputPath);
        Assertions.assertFalse(content.contains("nullField"), "Null fields should be omitted from YAML output");
        Assertions.assertTrue(content.contains("title"));
    }

    @Test
    public void writeReportConfig_correctAuthorYamlKeys_presentInOutput() throws IOException {
        Path outputPath = tempDir.resolve("report-config.yaml");

        Map<String, Object> author = new HashMap<>();
        author.put("author-git-host-id", "alice");
        author.put("author-display-name", "Alice Thompson");
        author.put("author-emails", List.of("alice@example.com"));
        author.put("author-git-author-name", List.of("Alice T."));

        Map<String, Object> branch = new HashMap<>();
        branch.put("branch", "main");
        branch.put("authors", List.of(author));
        branch.put("ignore-glob-list", List.of());
        branch.put("ignore-authors-list", List.of());

        Map<String, Object> repo = new HashMap<>();
        repo.put("repo", "https://github.com/user/repo.git");
        repo.put("branches", List.of(branch));
        repo.put("groups", List.of());

        Map<String, Object> config = new HashMap<>();
        config.put("title", "Test Report");
        config.put("repos", List.of(repo));

        ConfigFileWriter.writeReportConfig(config, outputPath);

        String content = Files.readString(outputPath);
        Assertions.assertTrue(content.contains("author-git-host-id"),
                "YAML should use 'author-git-host-id' key");
        Assertions.assertTrue(content.contains("author-display-name"),
                "YAML should use 'author-display-name' key");
        Assertions.assertTrue(content.contains("author-emails"),
                "YAML should use 'author-emails' key");
        Assertions.assertTrue(content.contains("author-git-author-name"),
                "YAML should use 'author-git-author-name' key");
    }
}
