package reposense;

import static org.apache.tools.ant.types.Commandline.translateCommandline;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import net.sourceforge.argparse4j.helper.HelpScreenException;
import reposense.model.AuthorConfiguration;
import reposense.model.CliArguments;
import reposense.model.ConfigCliArguments;
import reposense.model.GroupConfiguration;
import reposense.model.RepoConfiguration;
import reposense.parser.ArgsParser;
import reposense.parser.AuthorConfigCsvParser;
import reposense.parser.GroupConfigCsvParser;
import reposense.parser.ParseException;
import reposense.parser.RepoConfigCsvParser;
import reposense.report.ReportGenerator;
import reposense.util.FileUtil;
import reposense.util.InputBuilder;
import reposense.util.TestUtil;

public class ConfigSystemTest {
    private static final String FT_TEMP_DIR = "ft_temp";
    private static final String EXPECTED_FOLDER = "expected";
    private static final List<String> TESTING_FILE_FORMATS = Arrays.asList("java", "adoc");
    private static final String TEST_REPORT_GENERATED_TIME = "Tue Jul 24 17:45:15 SGT 2018";

    @Before
    public void setUp() throws IOException {
        FileUtil.deleteDirectory(FT_TEMP_DIR);
    }

    @Test
    public void testNoDateRange() throws IOException, URISyntaxException, ParseException, HelpScreenException {
        generateReport();
        Path actualFiles = Paths.get(getClass().getClassLoader().getResource("noDateRange/expected").toURI());
        verifyAllJson(actualFiles, FT_TEMP_DIR);
    }

    @Test
    public void testDateRange() throws IOException, URISyntaxException, ParseException, HelpScreenException {
        generateReport(getInputWithDates("1/9/2017", "30/10/2017"));
        Path actualFiles = Paths.get(getClass().getClassLoader().getResource("dateRange/expected").toURI());
        verifyAllJson(actualFiles, FT_TEMP_DIR);
    }

    private String getInputWithDates(String sinceDate, String untilDate) {
        return String.format("--since %s --until %s", sinceDate, untilDate);
    }

    private void generateReport() throws IOException, URISyntaxException, ParseException, HelpScreenException {
        generateReport("--until 02/03/2019");
    }

    /**
     * Generates the testing report to be compared with expected report.
     * @throws IOException if there is error in parsing csv file.
     * @throws URISyntaxException if the path fo config folder cannot be converted to URI.
     * @throws ParseException if the string argument fails to parse to a {@code CliArguments} object.
     * @throws HelpScreenException if given args contain the --help flag. Help message will be printed out
     * by the {@code ArgumentParser} hence this is to signal to the caller that the program is safe to exit.
     */
    private void generateReport(String inputDates)
            throws IOException, URISyntaxException, ParseException, HelpScreenException {
        Path configFolder = Paths.get(getClass().getClassLoader().getResource("repo-config.csv").toURI()).getParent();

        String formats = String.join(" ", TESTING_FILE_FORMATS);
        String input = new InputBuilder().addConfig(configFolder)
                .addFormats(formats)
                .add(inputDates)
                .build();

        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));

        List<RepoConfiguration> repoConfigs =
                new RepoConfigCsvParser(((ConfigCliArguments) cliArguments).getRepoConfigFilePath()).parse();
        List<AuthorConfiguration> authorConfigs =
                new AuthorConfigCsvParser(((ConfigCliArguments) cliArguments).getAuthorConfigFilePath()).parse();
        List<GroupConfiguration> groupConfigs =
                new GroupConfigCsvParser(((ConfigCliArguments) cliArguments).getGroupConfigFilePath()).parse();

        RepoConfiguration.merge(repoConfigs, authorConfigs);
        RepoConfiguration.mergeGroups(repoConfigs, groupConfigs);

        RepoConfiguration.setFormatsToRepoConfigs(repoConfigs, cliArguments.getFormats());
        RepoConfiguration.setDatesToRepoConfigs(
                repoConfigs, cliArguments.getSinceDate(), cliArguments.getUntilDate());

        ReportGenerator.generateReposReport(repoConfigs, FT_TEMP_DIR, TEST_REPORT_GENERATED_TIME,
                cliArguments.getSinceDate().orElse(null), cliArguments.getUntilDate().orElse(null));
    }

    /**
     * Verifies all JSON files in {@code actualDirectory} with {@code expectedDirectory}
     */
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

    /**
     * Asserts the correctness of given JSON file.
     */
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
