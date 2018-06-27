package reposense;

import static org.apache.tools.ant.types.Commandline.translateCommandline;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import reposense.model.CliArguments;
import reposense.model.RepoConfiguration;
import reposense.parser.ArgsParser;
import reposense.parser.CsvParser;
import reposense.parser.ParseException;
import reposense.report.ReportGenerator;
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
    public void testNoDateRange() throws IOException, URISyntaxException, ParseException {
        String actualRelativeDir = generateReport();
        Path actualFiles = Paths.get(getClass().getClassLoader().getResource("noDateRange/expected").toURI());
        verifyAllJson(actualFiles, actualRelativeDir);
    }

    @Test
    public void testDateRange() throws IOException, URISyntaxException, ParseException {
        String actualRelativeDir = generateReport(getInputWithDates("1/9/2017", "30/10/2017"));
        Path actualFiles = Paths.get(getClass().getClassLoader().getResource("dateRange/expected").toURI());
        verifyAllJson(actualFiles, actualRelativeDir);
    }

    private String getInputWithDates(String sinceDate, String untilDate) {
        return String.format("-since %s -until %s", sinceDate, untilDate);
    }

    private String generateReport() throws IOException, URISyntaxException, ParseException {
        return generateReport("");
    }

    private String generateReport(String inputDates) throws IOException, URISyntaxException, ParseException {
        Path configFilePath = Paths.get(getClass().getClassLoader().getResource("sample.csv").toURI());
        String input = String.format("-config %s ", configFilePath) + inputDates;

        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));
        List<RepoConfiguration> configs = CsvParser.parse(cliArguments.getConfigFilePath());
        RepoConfiguration.setDatesToRepoConfigs(configs, cliArguments.getSinceDate(), cliArguments.getUntilDate());

        ReportGenerator.generateReposReport(configs, FT_TEMP_DIR);
        return FT_TEMP_DIR;
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
