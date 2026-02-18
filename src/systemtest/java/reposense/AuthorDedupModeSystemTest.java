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

/**
 * System tests for author dedup mode functionality.
 * Tests that author dedup mode correctly includes all commit authors while respecting configured aliases
 * from author-config.csv.
 */
@Execution(ExecutionMode.SAME_THREAD)
public class AuthorDedupModeSystemTest {
    private static final List<String> TESTING_FILE_FORMATS = Arrays.asList("java", "py");
    private static final String TEST_TIME_ZONE = "Asia/Singapore";

    private static final String OUTPUT_DIRECTORY = "adm_temp";
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
     * System test with author dedup mode enabled.
     * Verifies that all commit authors are included in the report while respecting configured aliases
     * from author-config.csv.
     */
    @Test
    public void testAuthorDedupModeEnabled() {
        InputBuilder inputBuilder = initInputBuilder()
                .addSinceDate(SinceDateArgumentType.FIRST_COMMIT_DATE_SHORTHAND)
                .addUntilDate("31/12/2024")
                .add("--author-dedup-mode");

        runTest(inputBuilder, false,
                "AuthorDedupModeSystemTest/authorDedupModeEnabled/expected");
    }

    /**
     * System test with author dedup mode disabled (default behavior).
     * Verifies that only configured authors are included in the report.
     */
    @Test
    public void testAuthorDedupModeDisabled() {
        InputBuilder inputBuilder = initInputBuilder()
                .addSinceDate(SinceDateArgumentType.FIRST_COMMIT_DATE_SHORTHAND)
                .addUntilDate("31/12/2024");

        runTest(inputBuilder, false,
                "AuthorDedupModeSystemTest/authorDedupModeDisabled/expected");
    }

    /**
     * System test with author dedup mode enabled and shallow cloning.
     * Verifies that author dedup mode works correctly with shallow cloning.
     */
    @Test
    public void testAuthorDedupModeWithShallowCloning() {
        InputBuilder inputBuilder = initInputBuilder()
                .addSinceDate(SinceDateArgumentType.FIRST_COMMIT_DATE_SHORTHAND)
                .addUntilDate("31/12/2024")
                .addShallowCloning()
                .add("--author-dedup-mode");

        runTest(inputBuilder, true,
                "AuthorDedupModeSystemTest/authorDedupModeWithShallowCloning/expected");
    }

    /**
     * System test with author dedup mode enabled, portfolio mode, and authorship analysis.
     * Verifies that author dedup mode works with other features enabled.
     */
    @Test
    public void testAuthorDedupModeWithPortfolioAndAuthorship() {
        InputBuilder inputBuilder = initInputBuilder()
                .addSinceDate(SinceDateArgumentType.FIRST_COMMIT_DATE_SHORTHAND)
                .addUntilDate("31/12/2024")
                .addPortfolio()
                .addAnalyzeAuthorship()
                .add("--author-dedup-mode");

        runTest(inputBuilder, false,
                "AuthorDedupModeSystemTest/authorDedupModeWithPortfolioAndAuthorship/expected");
    }

    /**
     * Returns a {@link InputBuilder} that is initialized with some default values.
     * <br>Config Folder Path: {@code AuthorDedupModeSystemTest}
     * <br>Formats: {@link AuthorDedupModeSystemTest#TESTING_FILE_FORMATS TESTING_FILE_FORMATS}
     * <br>Timezone: {@link AuthorDedupModeSystemTest#TEST_TIME_ZONE TEST_TIME_ZONE}
     * <br>Output Folder Path: {@link AuthorDedupModeSystemTest#OUTPUT_DIRECTORY OUTPUT_DIRECTORY}
     * <br>Test Mode: {@code Enabled}
     */
    private InputBuilder initInputBuilder() {
        Path configFolder = loadResource(getClass(), "AuthorDedupModeSystemTest");
        String formats = String.join(" ", TESTING_FILE_FORMATS);

        return new InputBuilder().addConfig(configFolder)
            // Ignore per-repo standalone config so alias mapping from author-config.csv is used for dedup
            .addIgnoreStandaloneConfig()
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
