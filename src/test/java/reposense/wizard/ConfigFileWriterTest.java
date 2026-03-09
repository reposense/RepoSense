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
}
