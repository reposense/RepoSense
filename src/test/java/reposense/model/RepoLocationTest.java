package reposense.model;

import org.junit.Assert;
import org.junit.Test;

import reposense.parser.InvalidLocationException;

public class RepoLocationTest {
    private static final String TEST_REPO_BETA = "https://github.com/reposense/testrepo-Beta.git";
    private static final String TEST_REPO_DELTA = "https://github.com/reposense/testrepo-Delta.git";
    private static final String TEST_REPO_INVALID_LOCATION = "ftp://github.com/reposense/testrepo-Delta.git";

    @Test
    public void repoLocation_validLocation_success() throws Exception {
        assertValidLocation(TEST_REPO_BETA, "testrepo-Beta", "reposense");
        assertValidLocation(TEST_REPO_DELTA, "testrepo-Delta", "reposense");
    }

    @Test(expected = InvalidLocationException.class)
    public void repoLocation_invalidLocation_throwsInvalidLocationException() throws Exception {
        RepoLocation repoLocation = new RepoLocation(TEST_REPO_INVALID_LOCATION);
    }


    /**
     * Compares the information parsed by the RepoLocation model with the expected information
     */
    public void assertValidLocation(String rawLocation, String expectedRepoName,
            String expectedOrganization) throws Exception {
        RepoLocation repoLocation = new RepoLocation(rawLocation);
        Assert.assertEquals(repoLocation.getRepoName(), expectedRepoName);
        Assert.assertEquals(repoLocation.getOrganization(), expectedOrganization);

    }
}
