package reposense.model;

import org.junit.Assert;
import org.junit.Test;

import reposense.parser.InvalidLocationException;
import reposense.util.AssertUtil;

public class RepoLocationTest {
    @Test
    public void repoLocation_validRepoUrl_success() throws Exception {
        // valid url without specifying branch
        assertValidLocation("https://github.com/reposense/testrepo-Beta.git",
                "testrepo-Beta", "reposense");
        assertValidLocation("https://github.com/reposense/testrepo-Delta.git",
                "testrepo-Delta", "reposense");
    }

    @Test
    public void repoLocation_invalidRepoUrl_throwsInvalidLocationException() {
        // ftp url should be rejected
        assertInvalidLocation("ftp://github.com/reposense/testrepo-Delta.git");
        // non GitHub url should rejected
        assertInvalidLocation("https://gitlab.com/reposense/RepoSense.git");
        // url without organisation name should be rejected
        assertInvalidLocation("https://github.com/reposense.git");
        // url without repo name should be rejected
        assertInvalidLocation("https://github.com/reposense/.git");
        assertInvalidLocation("https://github.com/reposense");
        // url without a .git suffix should be rejected
        assertInvalidLocation("https://github.com/reposense/RepoSensegit");
        assertInvalidLocation("https://github.com/reposense/RepoSense-git");
        assertInvalidLocation("https://github.com/reposense/RepoSense.gi");
        assertInvalidLocation("https://github.com/reposense/RepoSense");
    }

    @Test
    public void repoLocation_repoUrlWithASpecifiedBranch_throwsInvalidLocationException() {
        // reject both repos with and without .git
        assertInvalidLocation("https://github.com/reposense/testrepo-Beta/tree/add-config-json");
        assertInvalidLocation("https://github.com/reposense/testrepo-Beta.git/tree/add-config-json");
    }

    @Test
    public void repoLocationParser_parseEmptyString_success() throws Exception {
        RepoLocation repoLocation = new RepoLocation("");
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

    private void assertInvalidLocation(String rawLocation) {
        AssertUtil.assertThrows(InvalidLocationException.class, () -> new RepoLocation(rawLocation));
    }
}
