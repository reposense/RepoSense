package reposense;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import reposense.git.GitClone;
import reposense.model.RepoConfiguration;
import reposense.model.RepoLocation;

import java.nio.file.Paths;

public class LocalRepoSystemTest {

    private static final String LOCAL_DIRECTORY_ONE = "parent1/test-repo";
    private static final String LOCAL_DIRECTORY_TWO = "parent2/test-repo";

    private static final String OUTPUT_DIRECTORY = "local-test";
    private static final String REPORT_DIRECTORY = "local-test/reposense-report";


    @BeforeAll
    public static void setupLocalRepos() throws Exception {
        GitClone.clone(new RepoConfiguration(new RepoLocation("https://github.com/reposense/testrepo-Alpha")),
                Paths.get("."),
                LOCAL_DIRECTORY_ONE);
        GitClone.clone(new RepoConfiguration(new RepoLocation(LOCAL_DIRECTORY_ONE)),
                Paths.get("."),
                LOCAL_DIRECTORY_TWO);
    }

    @Test
    public static void testSameFinalDirectory() throws Exception {

    }
    // write one dual local repo with same final directory test
    // write one for relative path test
}
