package reposense;

import static org.apache.tools.ant.types.Commandline.translateCommandline;
import static reposense.util.TestUtil.loadResource;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import reposense.git.GitVersion;
import reposense.model.SupportedDomainUrlMap;
import reposense.parser.SinceDateArgumentType;
import reposense.util.FileUtil;
import reposense.util.InputBuilder;
import reposense.util.SystemTestUtil;

@Execution(ExecutionMode.CONCURRENT)
public class ConfigSystemTest {
    private static final List<String> TESTING_FILE_FORMATS = Arrays.asList("java", "adoc");
    private static final String TEST_TIME_ZONE = "Asia/Singapore";

    private static final String OUTPUT_DIRECTORY = "ft_temp";
    private static final String DEFAULT_OUTPUT_FOLDER_NAME = "reposense-report";

    private static final String SINCE_BEGINNING_DATE_RANGE_REPORT_DIRECTORY =
            String.join("/", OUTPUT_DIRECTORY, "since_beginning_date_range");
    private static final Path SINCE_BEGINNING_DATE_RANGE_REPORT_DIRECTORY_PATH =
            Paths.get(SINCE_BEGINNING_DATE_RANGE_REPORT_DIRECTORY, DEFAULT_OUTPUT_FOLDER_NAME);

    private static final String THIRTY_DAYS_FROM_UNTIL_DATE_REPORT_DIRECTORY =
            String.join("/", OUTPUT_DIRECTORY, "thirty_days_from_until_date");
    private static final Path THIRTY_DAYS_FROM_UNTIL_DATE_REPORT_DIRECTORY_PATH =
            Paths.get(THIRTY_DAYS_FROM_UNTIL_DATE_REPORT_DIRECTORY, DEFAULT_OUTPUT_FOLDER_NAME);

    private static final String DATE_RANGE_WITH_MODIFIED_DATETIME_IN_LINES_REPORT_DIRECTORY =
            String.join("/", OUTPUT_DIRECTORY, "date_range_with_modified_datetime_in_lines");
    private static final Path DATE_RANGE_WITH_MODIFIED_DATETIME_IN_LINES_REPORT_DIRECTORY_PATH =
            Paths.get(DATE_RANGE_WITH_MODIFIED_DATETIME_IN_LINES_REPORT_DIRECTORY, DEFAULT_OUTPUT_FOLDER_NAME);

    private static final String SINCE_BEGINNING_DATE_RANGE_WITH_SHALLOW_CLONING_REPORT_DIRECTORY =
            String.join("/", OUTPUT_DIRECTORY, "since_beginning_date_range_with_shallow_cloning");
    private static final Path SINCE_BEGINNING_DATE_RANGE_WITH_SHALLOW_CLONING_REPORT_DIRECTORY_PATH =
            Paths.get(SINCE_BEGINNING_DATE_RANGE_WITH_SHALLOW_CLONING_REPORT_DIRECTORY, DEFAULT_OUTPUT_FOLDER_NAME);

    private static final String THIRTY_DAYS_FROM_UNTIL_DATE_WITH_SHALLOW_CLONING_REPORT_DIRECTORY =
            String.join("/", OUTPUT_DIRECTORY, "thirty_days_from_until_date_with_shallow_cloning");
    private static final Path THIRTY_DAYS_FROM_UNTIL_DATE_WITH_SHALLOW_CLONING_REPORT_DIRECTORY_PATH =
            Paths.get(THIRTY_DAYS_FROM_UNTIL_DATE_WITH_SHALLOW_CLONING_REPORT_DIRECTORY, DEFAULT_OUTPUT_FOLDER_NAME);

    private static final String SINCE_BEGINNING_DATE_RANGE_WITH_FIND_PREVIOUS_AUTHOR_REPORT_DIRECTORY =
            String.join("/", OUTPUT_DIRECTORY, "since_beginning_date_range_with_find_previous_author");
    private static final Path SINCE_BEGINNING_DATE_RANGE_WITH_FIND_PREVIOUS_AUTHOR_REPORT_DIRECTORY_PATH =
            Paths.get(SINCE_BEGINNING_DATE_RANGE_WITH_FIND_PREVIOUS_AUTHOR_REPORT_DIRECTORY,
                    DEFAULT_OUTPUT_FOLDER_NAME);

    private static final String THIRTY_DAYS_FROM_UNTIL_DATE_WITH_FIND_PREVIOUS_AUTHOR_REPORT_DIRECTORY =
            String.join("/", OUTPUT_DIRECTORY,
                    "thirty_days_from_until_date_with_find_previous_author");
    private static final Path THIRTY_DAYS_FROM_UNTIL_DATE_WITH_FIND_PREVIOUS_AUTHOR_REPORT_DIRECTORY_PATH =
            Paths.get(THIRTY_DAYS_FROM_UNTIL_DATE_WITH_FIND_PREVIOUS_AUTHOR_REPORT_DIRECTORY,
                    DEFAULT_OUTPUT_FOLDER_NAME);

    private static final String GIT_VERSION_INSUFFICIENT_MESSAGE = "Git version 2.23.0 and above necessary to run test";

    private static boolean didNotCloneRepoNormally = true;

    @BeforeAll
    public static void clearReportDirectory() throws Exception {
        FileUtil.deleteDirectory(OUTPUT_DIRECTORY);
    }

    @BeforeEach
    public void setUp() throws Exception {
        SupportedDomainUrlMap.clearAccessedSet();
    }

    @AfterAll
    public static void tearDown() throws Exception {
        FileUtil.deleteDirectory(OUTPUT_DIRECTORY);
    }

    public void deleteReportDirectory(String reportDirectory) throws Exception {
        FileUtil.deleteDirectory(reportDirectory);
    }

    /**
     * System test with a specified until date and a {@link SinceDateArgumentType#FIRST_COMMIT_DATE_SHORTHAND}
     * since date to capture from the first commit.
     */
    @Test
    public void testSinceBeginningDateRange() throws Exception {
        InputBuilder inputBuilder = initInputBuilder()
                .addSinceDate(SinceDateArgumentType.FIRST_COMMIT_DATE_SHORTHAND)
                .addUntilDate("2/3/2019")
                .addOutput(SINCE_BEGINNING_DATE_RANGE_REPORT_DIRECTORY)
                .addClonedRepoParentFolder("test");

        runTest(inputBuilder, false,
                "ConfigSystemTest/sinceBeginningDateRange/expected",
                SINCE_BEGINNING_DATE_RANGE_REPORT_DIRECTORY_PATH);

        deleteReportDirectory(SINCE_BEGINNING_DATE_RANGE_REPORT_DIRECTORY);
    }

    @Test
    public void test30DaysFromUntilDate() throws Exception {
        InputBuilder inputBuilder = initInputBuilder()
                .addUntilDate("1/11/2017")
                .addOutput(THIRTY_DAYS_FROM_UNTIL_DATE_REPORT_DIRECTORY)
                .addClonedRepoParentFolder("test2");

        runTest(inputBuilder, false,
                "ConfigSystemTest/30daysFromUntilDate/expected",
                THIRTY_DAYS_FROM_UNTIL_DATE_REPORT_DIRECTORY_PATH);

        deleteReportDirectory(THIRTY_DAYS_FROM_UNTIL_DATE_REPORT_DIRECTORY);
    }

    /**
     * System test with a specified since date and until date, with the last modified date time in each
     * line of code.
     */
    @Test
    public void testDateRangeWithModifiedDateTimeInLines() throws Exception {
        InputBuilder inputBuilder = initInputBuilder()
                .addSinceDate("1/9/2017")
                .addUntilDate("30/10/2017")
                .addLastModifiedDateFlags()
                .addOutput(DATE_RANGE_WITH_MODIFIED_DATETIME_IN_LINES_REPORT_DIRECTORY)
                .addClonedRepoParentFolder("test3");

        runTest(inputBuilder, false,
                "ConfigSystemTest/dateRangeWithModifiedDateTimeInLines/expected",
                DATE_RANGE_WITH_MODIFIED_DATETIME_IN_LINES_REPORT_DIRECTORY_PATH);

        deleteReportDirectory(DATE_RANGE_WITH_MODIFIED_DATETIME_IN_LINES_REPORT_DIRECTORY);
    }

    /**
     * System test with a specified until date and a {@link SinceDateArgumentType#FIRST_COMMIT_DATE_SHORTHAND}
     * since date to capture from the first commit, using shallow cloning.
     */
    @Test
    public void testSinceBeginningDateRangeWithShallowCloning() throws Exception {
        InputBuilder inputBuilder = initInputBuilder()
                .addSinceDate(SinceDateArgumentType.FIRST_COMMIT_DATE_SHORTHAND)
                .addUntilDate("2/3/2019")
                .addShallowCloning()
                .addOutput(SINCE_BEGINNING_DATE_RANGE_WITH_SHALLOW_CLONING_REPORT_DIRECTORY)
                .addClonedRepoParentFolder("test4");

        runTest(inputBuilder, true,
                "ConfigSystemTest/sinceBeginningDateRangeWithShallowCloning/expected",
                SINCE_BEGINNING_DATE_RANGE_WITH_SHALLOW_CLONING_REPORT_DIRECTORY_PATH);

        deleteReportDirectory(SINCE_BEGINNING_DATE_RANGE_WITH_SHALLOW_CLONING_REPORT_DIRECTORY);
    }

    @Test
    public void test30DaysFromUntilDateWithShallowCloning() throws Exception {
        InputBuilder inputBuilder = initInputBuilder()
                .addUntilDate("1/11/2017")
                .addShallowCloning()
                .addOutput(THIRTY_DAYS_FROM_UNTIL_DATE_WITH_SHALLOW_CLONING_REPORT_DIRECTORY)
                .addClonedRepoParentFolder("test5");

        runTest(inputBuilder, true,
                "ConfigSystemTest/30daysFromUntilDateWithShallowCloning/expected",
                THIRTY_DAYS_FROM_UNTIL_DATE_WITH_SHALLOW_CLONING_REPORT_DIRECTORY_PATH);

        deleteReportDirectory(THIRTY_DAYS_FROM_UNTIL_DATE_WITH_SHALLOW_CLONING_REPORT_DIRECTORY);
    }

    /**
     * System test with a specified until date and a {@link SinceDateArgumentType#FIRST_COMMIT_DATE_SHORTHAND}
     * since date to capture from the first commit, using find previous authors.
     */
    @Test
    public void testSinceBeginningDateRangeWithFindPreviousAuthors() throws Exception {
        Assumptions.assumeTrue(GitVersion.isGitVersionSufficientForFindingPreviousAuthors(),
                GIT_VERSION_INSUFFICIENT_MESSAGE);

        InputBuilder inputBuilder = initInputBuilder()
                .addSinceDate(SinceDateArgumentType.FIRST_COMMIT_DATE_SHORTHAND)
                .addUntilDate("2/3/2019")
                .addFindPreviousAuthors()
                .addOutput(SINCE_BEGINNING_DATE_RANGE_WITH_FIND_PREVIOUS_AUTHOR_REPORT_DIRECTORY)
                .addClonedRepoParentFolder("test6");

        runTest(inputBuilder, true,
                "ConfigSystemTest/sinceBeginningDateRangeFindPreviousAuthors/expected",
                SINCE_BEGINNING_DATE_RANGE_WITH_FIND_PREVIOUS_AUTHOR_REPORT_DIRECTORY_PATH);

        deleteReportDirectory(SINCE_BEGINNING_DATE_RANGE_WITH_FIND_PREVIOUS_AUTHOR_REPORT_DIRECTORY);
    }

    @Test
    public void test30DaysFromUntilDateWithFindPreviousAuthors() throws Exception {
        Assumptions.assumeTrue(GitVersion.isGitVersionSufficientForFindingPreviousAuthors(),
                GIT_VERSION_INSUFFICIENT_MESSAGE);

        InputBuilder inputBuilder = initInputBuilder()
                .addUntilDate("1/11/2017")
                .addFindPreviousAuthors()
                .addOutput(THIRTY_DAYS_FROM_UNTIL_DATE_WITH_FIND_PREVIOUS_AUTHOR_REPORT_DIRECTORY)
                .addClonedRepoParentFolder("test7");

        runTest(inputBuilder, true,
                "ConfigSystemTest/30daysFromUntilDateFindPreviousAuthors/expected",
                THIRTY_DAYS_FROM_UNTIL_DATE_WITH_FIND_PREVIOUS_AUTHOR_REPORT_DIRECTORY_PATH);

        deleteReportDirectory(THIRTY_DAYS_FROM_UNTIL_DATE_WITH_FIND_PREVIOUS_AUTHOR_REPORT_DIRECTORY);
    }

    /**
     * Returns a {@link InputBuilder} that is initialized with some default values.
     * <br>Config Folder Path: {@code ConfigSystemTest}
     * <br>Formats: {@link ConfigSystemTest#TESTING_FILE_FORMATS TESTING_FILE_FORMATS}
     * <br>Timezone: {@link ConfigSystemTest#TEST_TIME_ZONE TEST_TIME_ZONE}
     * <br>Output Folder Path: {@link ConfigSystemTest#OUTPUT_DIRECTORY OUTPUT_DIRECTORY}
     * <br>Test Mode: {@code Enabled}
     */
    private InputBuilder initInputBuilder() {
        Path configFolder = loadResource(getClass(), "ConfigSystemTest");
        String formats = String.join(" ", TESTING_FILE_FORMATS);

        return new InputBuilder().addConfig(configFolder)
                .addFormats(formats)
                .addTimezone(TEST_TIME_ZONE)
                .addTestMode();
    }

    /**
     * Generates the testing report and compares it with the expected report.
     * Re-generates a normal report after the testing finished if the first report is shallow-cloned.
     *
     * @param inputBuilder The input builder that contains the command line input specified by user.
     * @param shouldFreshClone Boolean for whether to clone repo again if it has been cloned before.
     * @param pathToResource The location at which files generated during the test are stored.
     */
    private void runTest(InputBuilder inputBuilder, boolean shouldFreshClone,
                         String pathToResource, Path reportDirectoryPath) {
        if (shouldFreshClone || didNotCloneRepoNormally) {
            inputBuilder = inputBuilder.addFreshCloning();
        }

        RepoSense.main(translateCommandline(inputBuilder.build()));

        Path actualFiles = loadResource(getClass(), pathToResource);
        SystemTestUtil.verifyReportJsonFiles(actualFiles, reportDirectoryPath);

        didNotCloneRepoNormally = inputBuilder.isShallowCloning();
    }
}
