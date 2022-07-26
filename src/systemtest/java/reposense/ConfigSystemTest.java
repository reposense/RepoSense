package reposense;

import static org.apache.tools.ant.types.Commandline.translateCommandline;
import static reposense.util.TestUtil.loadResource;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import reposense.git.GitVersion;
import reposense.model.AuthorConfiguration;
import reposense.model.CliArguments;
import reposense.model.ConfigCliArguments;
import reposense.model.RepoConfiguration;
import reposense.model.ReportConfiguration;
import reposense.parser.ArgsParser;
import reposense.parser.SinceDateArgumentType;
import reposense.report.ErrorSummary;
import reposense.report.ReportGenerator;
import reposense.util.FileUtil;
import reposense.util.InputBuilder;
import reposense.util.SystemTestUtil;

public class ConfigSystemTest {
    private static final String FT_TEMP_DIR = "ft_temp";
    private static final String DUMMY_ASSETS_DIR = "dummy";
    private static final List<String> TESTING_FILE_FORMATS = Arrays.asList("java", "adoc");
    private static final String TEST_REPORT_GENERATED_TIME = "Tue Jul 24 17:45:15 SGT 2018";
    private static final String TEST_REPORT_GENERATION_TIME = "15 second(s)";
    private static final String TEST_TIME_ZONE = "Asia/Singapore";

    private static boolean haveNormallyClonedRepo = false;

    @BeforeEach
    public void setUp() throws Exception {
        FileUtil.deleteDirectory(FT_TEMP_DIR);
        ErrorSummary.getInstance().clearErrorSet();
        AuthorConfiguration.setHasAuthorConfigFile(AuthorConfiguration.DEFAULT_HAS_AUTHOR_CONFIG_FILE);
    }

    @AfterEach
    public void tearDown() throws Exception {
        FileUtil.deleteDirectory(FT_TEMP_DIR);
    }

    /**
     * System test with a specified until date and a {@link SinceDateArgumentType#FIRST_COMMIT_DATE_SHORTHAND}
     * since date to capture from the first commit.
     */
    @Test
    public void testSinceBeginningDateRange() throws Exception {
        runTest(getInputWithDates(SinceDateArgumentType.FIRST_COMMIT_DATE_SHORTHAND, "2/3/2019"),
                false, false, false, false,
                "ConfigSystemTest/sinceBeginningDateRange/expected");
    }

    @Test
    public void test30DaysFromUntilDate() throws Exception {
        runTest(getInputWithUntilDate("1/11/2017"), false,
                false, false, false,
                "ConfigSystemTest/30daysFromUntilDate/expected");
    }

    /**
     * System test with a specified since date and until date, with the last modified date time in each
     * line of code.
     */
    @Test
    public void testDateRangeWithModifiedDateTimeInLines() throws Exception {
        runTest(getInputWithDates("1/9/2017", "30/10/2017"),
                true, false, false, false,
                "ConfigSystemTest/dateRangeWithModifiedDateTimeInLines/expected");
    }

    /**
     * System test with a specified until date and a {@link SinceDateArgumentType#FIRST_COMMIT_DATE_SHORTHAND}
     * since date to capture from the first commit, using shallow cloning.
     */
    @Test
    public void testSinceBeginningDateRangeWithShallowCloning() throws Exception {
        runTest(getInputWithDates(SinceDateArgumentType.FIRST_COMMIT_DATE_SHORTHAND, "2/3/2019"),
                false, true, true, false,
                "ConfigSystemTest/sinceBeginningDateRangeWithShallowCloning/expected");
    }

    @Test
    public void test30DaysFromUntilDateWithShallowCloning() throws Exception {
        runTest(getInputWithUntilDate("1/11/2017"), false,
                true, true, false,
                "ConfigSystemTest/30daysFromUntilDateWithShallowCloning/expected");
    }

    /**
     * System test with a specified until date and a {@link SinceDateArgumentType#FIRST_COMMIT_DATE_SHORTHAND}
     * since date to capture from the first commit, using find previous authors.
     */
    @Test
    public void testSinceBeginningDateRangeWithFindPreviousAuthors() throws Exception {
        runTest(getInputWithDates(SinceDateArgumentType.FIRST_COMMIT_DATE_SHORTHAND, "2/3/2019"),
                false, false, true, true,
                "ConfigSystemTest/sinceBeginningDateRangeFindPreviousAuthors/expected");
    }

    @Test
    public void test30DaysFromUntilDateWithFindPreviousAuthors() throws Exception {
        runTest(getInputWithUntilDate("1/11/2017"), false, false, true,
                true, "ConfigSystemTest/30daysFromUntilDateFindPreviousAuthors/expected");
    }

    private String getInputWithUntilDate(String untilDate) {
        return String.format("--until %s", untilDate);
    }

    private String getInputWithDates(String sinceDate, String untilDate) {
        return String.format("--since %s --until %s", sinceDate, untilDate);
    }

    /**
     * Generates the testing report and compares it with the expected report.
     * Re-generates a normal report after the testing finished if the first report is shallow-cloned.
     *
     * @param inputDates The date range for analysis.
     * @param shouldIncludeModifiedDateInLines Boolean for whether to include last modified date for authorship.
     * @param shallowCloning Boolean for whether to perform shallow cloning.
     * @param shouldFreshClone Boolean for whether to clone repo again if it has been cloned before.
     * @param findPreviousAuthors Boolean for whether to find and blame previous authors for ignored commits.
     * @param pathToResource The location at which files generated during the test are stored.
     * @throws Exception if any occur during testing.
     */
    private void runTest(String inputDates, boolean shouldIncludeModifiedDateInLines, boolean shallowCloning,
            boolean shouldFreshClone, boolean findPreviousAuthors, String pathToResource) throws Exception {
        generateReport(inputDates, shouldIncludeModifiedDateInLines, shallowCloning,
                shouldFreshClone || !haveNormallyClonedRepo, findPreviousAuthors);
        Path actualFiles = loadResource(getClass(), pathToResource);
        SystemTestUtil.verifyReportJsonFiles(actualFiles, Paths.get(FT_TEMP_DIR));
        haveNormallyClonedRepo = !shallowCloning;
    }

    /**
     * Generates the testing report to be compared with expected report.
     *
     * @param inputDates The date range for analysis.
     * @param shouldIncludeModifiedDateInLines Boolean for whether to include last modified date for authorship.
     * @param shallowCloning Boolean for whether to perform shallow cloning.
     * @param shouldFreshClone Boolean for whether to clone repo again if it has been cloned before.
     * @param findPreviousAuthors Boolean for whether to find and blame previous authors for ignored commits.
     * @throws Exception if any errors occur during testing.
     */
    private void generateReport(String inputDates, boolean shouldIncludeModifiedDateInLines, boolean shallowCloning,
            boolean shouldFreshClone, boolean findPreviousAuthors) throws Exception {
        Path configFolder = loadResource(getClass(), "ConfigSystemTest");

        String formats = String.join(" ", TESTING_FILE_FORMATS);

        InputBuilder inputBuilder = new InputBuilder().addConfig(configFolder)
                .addFormats(formats)
                .addTimezone(TEST_TIME_ZONE)
                .add(inputDates)
                .addTestMode();

        if (shallowCloning) {
            inputBuilder = inputBuilder.addShallowCloning();
        }
        if (findPreviousAuthors) {
            inputBuilder = inputBuilder.addFindPreviousAuthors();
        }
        if (shouldIncludeModifiedDateInLines) {
            inputBuilder = inputBuilder.addLastModifiedDateFlags();
        }
        if (shouldFreshClone) {
            inputBuilder = inputBuilder.addFreshCloning();
        }

        String input = inputBuilder.build();
        String[] args = translateCommandline(input);

//        RepoSense.main(args);

        // Change as close to RepoSense.main() as possible
        CliArguments cliArguments = ArgsParser.parse(args);
        List<RepoConfiguration> configs = RepoSense.getRepoConfigurations((ConfigCliArguments) cliArguments);
        ReportConfiguration reportConfig = ((ConfigCliArguments) cliArguments).getReportConfiguration();

        RepoConfiguration.setFormatsToRepoConfigs(configs, cliArguments.getFormats());
        RepoConfiguration.setDatesToRepoConfigs(configs, cliArguments.getSinceDate(), cliArguments.getUntilDate());
        RepoConfiguration.setZoneIdToRepoConfigs(configs, cliArguments.getZoneId());
        RepoConfiguration.setStandaloneConfigIgnoredToRepoConfigs(configs,
                cliArguments.isStandaloneConfigIgnored());
        RepoConfiguration.setFileSizeLimitIgnoredToRepoConfigs(configs,
                cliArguments.isFileSizeLimitIgnored());
        RepoConfiguration.setIsLastModifiedDateIncludedToRepoConfigs(configs,
                cliArguments.isLastModifiedDateIncluded());
        RepoConfiguration.setIsShallowCloningPerformedToRepoConfigs(configs,
                cliArguments.isShallowCloningPerformed());
        RepoConfiguration.setIsFindingPreviousAuthorsPerformedToRepoConfigs(configs,
                cliArguments.isFindingPreviousAuthorsPerformed());

        if (RepoConfiguration.isAnyRepoFindingPreviousAuthors(configs)
                && !GitVersion.isGitVersionSufficientForFindingPreviousAuthors()) {
            Assumptions.assumeFalse(false, "Git version 2.23.0 and above necessary to run test");
            RepoConfiguration.setToFalseIsFindingPreviousAuthorsPerformedToRepoConfigs(configs);
        }

        // Only diff between this method and main(), to be added to main
        AuthorConfiguration.setHasAuthorConfigFile(false);

        ReportGenerator.generateReposReport(configs, FT_TEMP_DIR, DUMMY_ASSETS_DIR, reportConfig,
                TEST_REPORT_GENERATED_TIME, cliArguments.getSinceDate(), cliArguments.getUntilDate(),
                cliArguments.isSinceDateProvided(), cliArguments.isUntilDateProvided(),
                cliArguments.getNumCloningThreads(), cliArguments.getNumAnalysisThreads(), () ->
                        TEST_REPORT_GENERATION_TIME, cliArguments.getZoneId(), shouldFreshClone);
    }
}
