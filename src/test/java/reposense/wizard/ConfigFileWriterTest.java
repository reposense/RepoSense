package reposense.wizard;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class ConfigFileWriterTest {

    @TempDir
    Path tempDir;

    @Test
    public void writeRepoConfig_validRepos_generatesCorrectCsv() throws IOException {
        Path outputPath = tempDir.resolve("repo-config.csv");
        List<Map<String, Object>> repos = new ArrayList<>();
        Map<String, Object> repo = new HashMap<>();
        repo.put("location", "https://github.com/reposense/RepoSense.git");
        repo.put("branch", "master");
        repo.put("shallowCloning", true);
        repos.add(repo);

        ConfigFileWriter.writeRepoConfig(repos, outputPath);

        Assertions.assertTrue(Files.exists(outputPath));
        List<String> lines = Files.readAllLines(outputPath);
        Assertions.assertEquals(2, lines.size());
        Assertions.assertTrue(lines.get(0).contains("Repository's Location"));
        Assertions.assertTrue(lines.get(1).contains("https://github.com/reposense/RepoSense.git"));
        Assertions.assertTrue(lines.get(1).contains("yes")); // shallowCloning should be "yes"
    }

    @Test
    public void writeAuthorConfig_validAuthors_generatesCorrectCsv() throws IOException {
        Path outputPath = tempDir.resolve("author-config.csv");
        List<Map<String, Object>> authors = new ArrayList<>();
        Map<String, Object> author = new HashMap<>();
        author.put("gitHostId", "johndoe");
        author.put("emails", "john@example.com");
        authors.add(author);

        ConfigFileWriter.writeAuthorConfig(authors, outputPath);

        Assertions.assertTrue(Files.exists(outputPath));
        List<String> lines = Files.readAllLines(outputPath);
        Assertions.assertEquals(2, lines.size());
        Assertions.assertTrue(lines.get(0).contains("Author's Git Host ID"));
        Assertions.assertTrue(lines.get(1).contains("johndoe"));
    }

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
}
