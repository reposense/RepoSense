package reposense;

import static reposense.util.TestUtil.loadResource;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import reposense.model.RepoConfiguration;
import reposense.model.RepoLocation;
import reposense.model.SupportedDomainUrlMap;
import reposense.report.ErrorSummary;
import reposense.util.FileUtil;
import reposense.util.SystemTestUtil;
import reposense.util.TestRepoCloner;

@Execution(ExecutionMode.SAME_THREAD)
public class LocalRepoSystemTest {
    private static final String LOCAL_DIRECTORY_ONE_PARENT = "parent1";
    private static final String LOCAL_DIRECTORY_TWO_PARENT = "parent2";
    private static final String LOCAL_DIRECTORY_ONE = "parent1/test-repo";
    private static final String LOCAL_DIRECTORY_TWO = "parent2/test-repo";

    private static final String LAST_COMMIT_DATE = "01/04/2022";

    private static final String OUTPUT_DIRECTORY = "local-test";
    private static final String TIME_ZONE = "UTC+08";
    private static final Path REPORT_DIRECTORY_PATH = Paths.get(OUTPUT_DIRECTORY, "reposense-report");


    @BeforeAll
    public static void setupLocalRepos() throws Exception {
        TestRepoCloner.clone(new RepoConfiguration(new RepoLocation("https://github.com/reposense/testrepo-Alpha")),
                Paths.get("."), LOCAL_DIRECTORY_ONE);
        TestRepoCloner.clone(new RepoConfiguration(new RepoLocation("https://github.com/reposense/testrepo-Alpha")),
                Paths.get("."), LOCAL_DIRECTORY_TWO);
    }

    @BeforeEach
    public void setupLocalTest() throws Exception {
        SupportedDomainUrlMap.clearAccessedSet();
        FileUtil.deleteDirectory(OUTPUT_DIRECTORY);
        ErrorSummary.getInstance().clearErrorSet();
    }

    @AfterEach
    public void deleteReportDirectory() throws Exception {
        FileUtil.deleteDirectory(OUTPUT_DIRECTORY);
    }

    @AfterAll
    public static void deleteClonedLocalRepos() throws Exception {
        FileUtil.deleteDirectory(LOCAL_DIRECTORY_ONE_PARENT);
        FileUtil.deleteDirectory(LOCAL_DIRECTORY_TWO_PARENT);
    }

    @Test
    public void testSameFinalDirectory() {
        String cliInput = String.format("-r %s %s -s d1 -u %s -o %s -t %s",
                LOCAL_DIRECTORY_ONE, LOCAL_DIRECTORY_TWO, LAST_COMMIT_DATE, OUTPUT_DIRECTORY, TIME_ZONE);
        runTest(cliInput, "LocalRepoSystemTest/testSameFinalDirectory");
    }

    @Test
    public void testRelativePathing() {
        String relativePathForTesting = "parent1/../parent1/./test-repo";
        String cliInput = String.format("-r %s -s d1 -u %s -o %s -t %s",
                relativePathForTesting, LAST_COMMIT_DATE, OUTPUT_DIRECTORY, TIME_ZONE);
        runTest(cliInput, "LocalRepoSystemTest/testRelativePathing");
    }

    /**
     * Runs RepoSense with {@code commandArgs} and tests it against the expected
     * files in {@code expectedFilesPathString}.
     */
    private void runTest(String commandArgs, String expectedFilesPathString) {
        String[] args = commandArgs.split(" ");
        RepoSense.main(args);
        Path expectedFilesPath = loadResource(getClass(), expectedFilesPathString);
        SystemTestUtil.verifyReportJsonFiles(expectedFilesPath, REPORT_DIRECTORY_PATH);
    }
}
