package reposense;

import static reposense.util.TestUtil.loadResource;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import reposense.git.GitClone;
import reposense.model.RepoConfiguration;
import reposense.model.RepoLocation;
import reposense.util.FileUtil;
import reposense.util.SystemTestUtil;

public class LocalRepoSystemTest {

    private static final String LOCAL_DIRECTORY_ONE = "parent1/test-repo";
    private static final String LOCAL_DIRECTORY_TWO = "parent2/test-repo";

    private static final String OUTPUT_DIRECTORY = "local-test";
    private static final Path REPORT_DIRECTORY_PATH = Paths.get(OUTPUT_DIRECTORY, "reposense-report");


    @BeforeAll
    public static void setupLocalRepos() throws Exception {
        GitClone.clone(new RepoConfiguration(new RepoLocation("https://github.com/reposense/testrepo-Alpha")),
                Paths.get("."),
                LOCAL_DIRECTORY_ONE);
        GitClone.clone(new RepoConfiguration(new RepoLocation(LOCAL_DIRECTORY_ONE)),
                Paths.get("."),
                LOCAL_DIRECTORY_TWO);
    }

    @AfterEach
    public static void deleteReport() throws Exception {
        FileUtil.deleteDirectory(OUTPUT_DIRECTORY);
    }

    @AfterAll
    public static void deleteClonedLocalRepos() throws Exception {
        FileUtil.deleteDirectory(LOCAL_DIRECTORY_ONE);
        FileUtil.deleteDirectory(LOCAL_DIRECTORY_TWO);
    }

    @Test
    public void testSameFinalDirectory() throws Exception {
        String cliInput = String.format("-r %s %s -s d1 -u 01/04/2022 -o local-test",
                LOCAL_DIRECTORY_ONE, LOCAL_DIRECTORY_TWO);
        String[] args = cliInput.split(" ");
        RepoSense.main(args);
        Path expectedFilePath = loadResource(getClass(), "LocalRepoSystemTest/testSameFinalDirectory");
        SystemTestUtil.verifyAllJson(expectedFilePath, REPORT_DIRECTORY_PATH);
    }

    @Test
    public void testRelativePathing() throws Exception {
        String relativePathForTesting = "parent1/../parent1/./test-repo";
        String cliInput = String.format("-r %s -s d1 -u 01/04/2022 -o local-test",
                relativePathForTesting);
        String[] args = cliInput.split(" ");
        RepoSense.main(args);
        Path expectedFilePath = loadResource(getClass(), "LocalRepoSystemTest/testRelativePathing");
        SystemTestUtil.verifyAllJson(expectedFilePath, REPORT_DIRECTORY_PATH);
    }
}
