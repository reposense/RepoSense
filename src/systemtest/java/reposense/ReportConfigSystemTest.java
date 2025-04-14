package reposense;

import static org.apache.tools.ant.types.Commandline.translateCommandline;
import static reposense.util.TestUtil.loadResource;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import reposense.model.SupportedDomainUrlMap;
import reposense.parser.types.SinceDateArgumentType;
import reposense.report.ErrorSummary;
import reposense.util.FileUtil;
import reposense.util.InputBuilder;
import reposense.util.SystemTestUtil;

@Execution(ExecutionMode.SAME_THREAD)
public class ReportConfigSystemTest {
    private static final List<String> TESTING_FILE_FORMATS = Arrays.asList("py", "sh");
    private static final String TEST_TIME_ZONE = "Asia/Singapore";

    private static final String OUTPUT_DIRECTORY = "rc_temp";
    private static final Path REPORT_DIRECTORY_PATH = Paths.get(OUTPUT_DIRECTORY, "reposense-report");

    private static boolean didNotCloneRepoNormally = true;

    @BeforeEach
    public void setUp() throws Exception {
        SupportedDomainUrlMap.clearAccessedSet();
        FileUtil.deleteDirectory(OUTPUT_DIRECTORY);
        ErrorSummary.getInstance().clearErrorSet();
    }

    @AfterEach
    public void tearDown() throws Exception {
        FileUtil.deleteDirectory(OUTPUT_DIRECTORY);
    }

    /**
     * System test with a specified until date and a {@link SinceDateArgumentType#FIRST_COMMIT_DATE_SHORTHAND}
     * since date to capture from the first commit.
     */
    @Test
    public void testSinceBeginningDateRange() {
        InputBuilder inputBuilder = initInputBuilder()
                .addSinceDate(SinceDateArgumentType.FIRST_COMMIT_DATE_SHORTHAND)
                .addUntilDate("31/12/2024");

        runTest(inputBuilder, false,
                "ReportConfigSystemTest/sinceBeginningDateRange/expected");
    }

    @Test
    public void test30DaysFromUntilDate() {
        InputBuilder inputBuilder = initInputBuilder().addUntilDate("31/8/2024");

        runTest(inputBuilder, false,
                "ReportConfigSystemTest/30daysFromUntilDate/expected");
    }


    /**
     * System test with a specified until date and a {@link SinceDateArgumentType#FIRST_COMMIT_DATE_SHORTHAND}
     * since date to capture from the first commit, using shallow cloning.
     */
    @Test
    public void testSinceBeginningDateRangeWithShallowCloning() {
        InputBuilder inputBuilder = initInputBuilder()
                .addSinceDate(SinceDateArgumentType.FIRST_COMMIT_DATE_SHORTHAND)
                .addUntilDate("31/8/2024")
                .addShallowCloning();

        runTest(inputBuilder, true,
                "ReportConfigSystemTest/sinceBeginningDateRangeWithShallowCloning/expected");
    }

    @Test
    public void test30DaysFromUntilDateWithShallowCloning() {
        InputBuilder inputBuilder = initInputBuilder()
                .addUntilDate("31/8/2024")
                .addShallowCloning();

        runTest(inputBuilder, true,
                "ReportConfigSystemTest/30daysFromUntilDateWithShallowCloning/expected");
    }

    /**
     * System test with a specified until date and a {@link SinceDateArgumentType#FIRST_COMMIT_DATE_SHORTHAND}
     * since date to capture from the first commit.
     */
    @Test
    public void testSinceBeginningDateRangeWithPortfolio() {
        InputBuilder inputBuilder = initInputBuilder()
                .addSinceDate(SinceDateArgumentType.FIRST_COMMIT_DATE_SHORTHAND)
                .addUntilDate("31/8/2024")
                .addPortfolio();

        runTest(inputBuilder, false,
                "ReportConfigSystemTest/sinceBeginningDateRangeWithPortfolio/expected");
    }


    /**
     * System test with a specified since date and until date, with the last modified date time in each
     * line of code.
     */
    @Test
    public void testDateRangeWithModifiedDateTimeInLines() {
        InputBuilder inputBuilder = initInputBuilder()
                .addSinceDate("1/1/2024")
                .addUntilDate("31/12/2024")
                .addLastModifiedDateFlags();

        runTest(inputBuilder, false,
                "ReportConfigSystemTest/dateRangeWithModifiedDateTimeInLines/expected");
    }

    /**
     * Returns a {@link InputBuilder} that is initialized with some default values.
     * <br>Config Folder Path: {@code ReportConfigSystemTest}
     * <br>Formats: {@link ReportConfigSystemTest#TESTING_FILE_FORMATS TESTING_FILE_FORMATS}
     * <br>Timezone: {@link ReportConfigSystemTest#TEST_TIME_ZONE TEST_TIME_ZONE}
     * <br>Output Folder Path: {@link ReportConfigSystemTest#OUTPUT_DIRECTORY OUTPUT_DIRECTORY}
     * <br>Test Mode: {@code Enabled}
     */
    private InputBuilder initInputBuilder() {
        Path configFolder = loadResource(getClass(), "ReportConfigSystemTest");
        String formats = String.join(" ", TESTING_FILE_FORMATS);

        return new InputBuilder().addConfig(configFolder)
                .addFormats(formats)
                .addTimezone(TEST_TIME_ZONE)
                .addOutput(OUTPUT_DIRECTORY);
    }

    /**
     * Generates the testing report and compares it with the expected report.
     * Re-generates a normal report after the testing finished if the first report is shallow-cloned.
     *
     * @param inputBuilder The input builder that contains the command line input specified by user.
     * @param shouldFreshClone Boolean for whether to clone repo again if it has been cloned before.
     * @param pathToResource The location at which files generated during the test are stored.
     */
    private void runTest(InputBuilder inputBuilder, boolean shouldFreshClone, String pathToResource) {
        if (shouldFreshClone || didNotCloneRepoNormally) {
            inputBuilder = inputBuilder.addFreshCloning();
        }

        RepoSense.main(translateCommandline(inputBuilder.build()));

        Path actualFiles = loadResource(getClass(), pathToResource);
        SystemTestUtil.verifyReportJsonFiles(actualFiles, REPORT_DIRECTORY_PATH);

        didNotCloneRepoNormally = inputBuilder.isShallowCloning();
    }
}
