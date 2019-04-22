package reposense.report;

import java.io.IOException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import reposense.model.RepoConfiguration;
import reposense.model.RepoLocation;
import reposense.parser.InvalidLocationException;
import reposense.util.FileUtil;

public class RepoClonerTest {

    private static final String TEST_REPO_EMPTY_GIT_LOCATION = "https://github.com/reposense/testrepo-Empty.git";
    private static final String OUTPUT_PATH = "repocloner_test";

    @After
    public void after() throws IOException {
        FileUtil.deleteDirectory(FileUtil.REPOS_ADDRESS);
        FileUtil.deleteDirectory(OUTPUT_PATH);
    }

    @Test
    public void repoCloner_emptyRepo_failsGracefully() throws InvalidLocationException, IOException {
        RepoConfiguration emptyRepositoryRepoConfig =
                new RepoConfiguration(new RepoLocation(TEST_REPO_EMPTY_GIT_LOCATION));

        RepoCloner repoCloner = new RepoCloner();
        repoCloner.clone(OUTPUT_PATH, emptyRepositoryRepoConfig);
        RepoLocation clonedRepoLocation = repoCloner.getClonedRepoLocation(OUTPUT_PATH);

        Assert.assertNull(clonedRepoLocation);
    }
}
