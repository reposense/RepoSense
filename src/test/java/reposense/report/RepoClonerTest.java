package reposense.report;

import org.junit.Assert;
import org.junit.Test;

import reposense.model.RepoConfiguration;
import reposense.model.RepoLocation;

public class RepoClonerTest {

    private static final String TEST_REPO_EMPTY_GIT_LOCATION = "https://github.com/reposense/testrepo-Empty.git";
    private static final String OUTPUT_PATH = "repocloner_test";

    @Test
    public void repoCloner_emptyRepo_failsGracefully() throws Exception {
        RepoConfiguration emptyRepositoryRepoConfig =
                new RepoConfiguration(new RepoLocation(TEST_REPO_EMPTY_GIT_LOCATION));

        RepoCloner repoCloner = new RepoCloner();
        repoCloner.cloneBare(emptyRepositoryRepoConfig);
        RepoLocation clonedRepoLocation = repoCloner.getClonedRepoLocation();

        Assert.assertNull(clonedRepoLocation);
    }
}
