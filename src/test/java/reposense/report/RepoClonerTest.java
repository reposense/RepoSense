package reposense.report;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import reposense.model.RepoConfiguration;
import reposense.model.RepoLocation;
import reposense.util.FileUtil;
import reposense.util.TestRepoCloner;

public class RepoClonerTest {

    private static final String TEST_REPO_EMPTY_GIT_LOCATION = "https://github.com/reposense/testrepo-Empty.git";
    private static final String TEST_REPO_GIT_LOCATION = "https://github.com/reposense/testrepo-Alpha.git";
    private static final Path REPOCLONE_LOCAL_TEST_PATH =
            Paths.get(FileUtil.REPOS_ADDRESS, "repoclone test/dummy-repo");

    @Test
    public void repoCloner_emptyRepo_failsGracefully() throws Exception {
        RepoConfiguration emptyRepositoryRepoConfig =
                new RepoConfiguration.Builder()
                        .location(new RepoLocation(TEST_REPO_EMPTY_GIT_LOCATION)).build();

        RepoCloner repoCloner = new RepoCloner();
        repoCloner.cloneBare(emptyRepositoryRepoConfig);
        RepoLocation clonedRepoLocation = repoCloner.getClonedRepoLocation();

        Assertions.assertNull(clonedRepoLocation);
    }

    @Test
    public void repoCloner_validRepoLocationWithRelativePathingAndSpaces_success() throws Exception {
        // Clones a test repository into the test directory for testing of relative pathing
        RepoConfiguration tempRemoteConfiguration = new RepoConfiguration.Builder()
                .location(new RepoLocation(TEST_REPO_GIT_LOCATION)).build();
        TestRepoCloner.cloneBare(tempRemoteConfiguration, Paths.get("."), REPOCLONE_LOCAL_TEST_PATH.toString());

        RepoConfiguration repoWithRelativePathingAndSpacesAndEndingBackslash =
                new RepoConfiguration.Builder()
                        .location(new RepoLocation(REPOCLONE_LOCAL_TEST_PATH.toString())).build();
        RepoCloner repoCloner = new RepoCloner();
        repoCloner.cloneBare(repoWithRelativePathingAndSpacesAndEndingBackslash);
        Assertions.assertTrue(Files.exists(REPOCLONE_LOCAL_TEST_PATH));
    }
}
