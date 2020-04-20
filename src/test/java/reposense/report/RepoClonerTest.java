package reposense.report;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import reposense.model.RepoConfiguration;
import reposense.model.RepoLocation;
import reposense.util.FileUtil;

public class RepoClonerTest {

    private static final String TEST_REPO_EMPTY_GIT_LOCATION = "https://github.com/reposense/testrepo-Empty.git";
    private static final String OUTPUT_PATH = "repocloner_test";

    @After
    public void after() throws Exception {
        FileUtil.deleteDirectory(FileUtil.REPOS_ADDRESS);
        FileUtil.deleteDirectory(OUTPUT_PATH);
    }

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
