package reposense;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import reposense.dataobject.RepoConfiguration;
import reposense.report.RepoInfoFileGenerator;
import reposense.system.CsvConfigurationBuilder;
import reposense.util.FileUtil;
import reposense.util.TestUtil;


public class Entry {
    private static final String FT_TEMP_DIR = "ft_temp";
    private static final String EXPECTED_FOLDER = "expected";

    @Before
    public void setUp() throws IOException {
        FileUtil.deleteDirectory(FT_TEMP_DIR);
    }

    @Test
    public void test() throws IOException, URISyntaxException {
        String actualRelativeDir = FT_TEMP_DIR + "/" + generateReport();
        Path actualFiles = Paths.get(getClass().getClassLoader().getResource("expected").toURI());
        verifyAllJson(actualFiles, actualRelativeDir);
        FileUtil.deleteDirectory(FT_TEMP_DIR);
    }

    private String generateReport() throws URISyntaxException {
        Path configFilePath = Paths.get(getClass().getClassLoader().getResource("sample_full.csv").toURI());
        Calendar c = Calendar.getInstance();
        c.set(2017, Calendar.JUNE, 1);
        Date fromDate = c.getTime();
        c.set(2017, Calendar.OCTOBER, 30);
        Date toDate = c.getTime();
        List<RepoConfiguration> configs = CsvConfigurationBuilder.buildConfigs(configFilePath, fromDate, toDate);
        return RepoInfoFileGenerator.generateReposReport(configs, FT_TEMP_DIR);
    }

    private void verifyAllJson(Path expectedDirectory, String actualRelative) {
        try (Stream<Path> pathStream = Files.list(expectedDirectory)) {
            for (Path filePath : pathStream.collect(Collectors.toList())) {
                if (Files.isDirectory(filePath)) {
                    verifyAllJson(filePath, actualRelative);
                }
                if (filePath.toString().endsWith(".json")) {
                    String relativeDirectory = filePath.toAbsolutePath().toString().split(EXPECTED_FOLDER)[1];
                    assertJson(filePath, relativeDirectory, actualRelative);
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void assertJson(Path expectedJson, String expectedPosition, String actualRelative) {
        Path actualJson = Paths.get(actualRelative, expectedPosition);
        Assert.assertTrue(Files.exists(actualJson));
        try {
            Assert.assertTrue(TestUtil.compareFileContents(expectedJson, actualJson));
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }
    }
}
