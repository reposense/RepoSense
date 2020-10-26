package reposense;

import static org.apache.tools.ant.types.Commandline.translateCommandline;

import static reposense.util.TestUtil.loadResource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import reposense.model.AuthorConfiguration;
import reposense.model.CliArguments;
import reposense.model.ConfigCliArguments;
import reposense.model.GroupConfiguration;
import reposense.model.RepoConfiguration;
import reposense.parser.ArgsParser;
import reposense.parser.AuthorConfigCsvParser;
import reposense.parser.GroupConfigCsvParser;
import reposense.parser.RepoConfigCsvParser;
import reposense.parser.SinceDateArgumentType;
import reposense.report.ErrorSummary;
import reposense.report.ReportGenerator;
import reposense.util.FileUtil;
import reposense.util.InputBuilder;
import reposense.util.TestUtil;

public class ConfigSystemTest {
    private static final String FT_TEMP_DIR = "ft_temp";
    private static final String EXPECTED_FOLDER = "expected";
    private static final List<String> TESTING_FILE_FORMATS = Arrays.asList("java", "adoc");
    private static final String TEST_REPORT_GENERATED_TIME = "Tue Jul 24 17:45:15 SGT 2018";
    private static final String TEST_REPORT_GENERATION_TIME = "15 second(s)";

    @Before
    public void setUp() throws Exception {
        FileUtil.deleteDirectory(FT_TEMP_DIR);
        ErrorSummary.getInstance().clearErrorList();
    }

    @After
    public void tearDown() throws Exception {
        FileUtil.deleteDirectory(FT_TEMP_DIR);
    }

    /**
     * System test with a specified until date and a {@link SinceDateArgumentType#FIRST_COMMIT_DATE_SHORTHAND}
     * since date to capture from the first commit.
     */
    @Test
    public void testSinceBeginningDateRange() throws Exception {
        generateReport(getInputWithDates(SinceDateArgumentType.FIRST_COMMIT_DATE_SHORTHAND, "2/3/2019"));
        Path actualFiles = loadResource(getClass(), "sinceBeginningDateRange/expected");
        verifyAllJson(actualFiles, FT_TEMP_DIR);
    }

    @Test
    public void test30DaysFromUntilDate() throws Exception {
        generateReport(getInputWithUntilDate("1/11/2017"));
        Path actualFiles = loadResource(getClass(), "30daysFromUntilDate/expected");
        verifyAllJson(actualFiles, FT_TEMP_DIR);
    }

    /**
     * System test with a specified since date and until date.
     */
    @Test
    public void testDateRange() throws Exception {
        generateReport(getInputWithDates("1/9/2017", "30/10/2017"));
        Path actualFiles = loadResource(getClass(), "dateRange/expected");
        verifyAllJson(actualFiles, FT_TEMP_DIR);
    }

    private String getInputWithUntilDate(String untilDate) {
        return String.format("--until %s", untilDate);
    }

    private String getInputWithDates(String sinceDate, String untilDate) {
        return String.format("--since %s --until %s", sinceDate, untilDate);
    }

    /**
     * Generates the testing report to be compared with expected report.
     */
    private void generateReport(String inputDates) throws Exception {
        Path configFolder = loadResource(getClass(), "repo-config.csv").getParent();

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
        RepoConfiguration.setGroupConfigsToRepos(repoConfigs, groupConfigs);

        RepoConfiguration.setFormatsToRepoConfigs(repoConfigs, cliArguments.getFormats());
        RepoConfiguration.setDatesToRepoConfigs(
                repoConfigs, cliArguments.getSinceDate(), cliArguments.getUntilDate());

        ReportGenerator.generateReposReport(repoConfigs, FT_TEMP_DIR, TEST_REPORT_GENERATED_TIME,
                cliArguments.getZoneId(), cliArguments.getSinceDate(), cliArguments.getUntilDate(),
                cliArguments.isSinceDateProvided(), cliArguments.isUntilDateProvided(), () ->
                        TEST_REPORT_GENERATION_TIME);
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
