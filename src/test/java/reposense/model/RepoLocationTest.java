package reposense.model;

import org.junit.Assert;
import org.junit.Test;

import reposense.parser.InvalidLocationException;
import reposense.util.AssertUtil;

public class RepoLocationTest {
    private static final String TEST_REPO_BETA = "https://github.com/reposense/testrepo-Beta.git";
    private static final String TEST_REPO_DELTA = "https://github.com/reposense/testrepo-Delta.git";

    @Test
    public void repoLocation_validRepoUrl_success() throws Exception {
        assertValidLocation(TEST_REPO_BETA, "testrepo-Beta", "reposense");
        assertValidLocation(TEST_REPO_DELTA, "testrepo-Delta", "reposense");
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
    public void repoLocation_invalidBranchUrl_throwsInvalidLocationException() {
        // ftp url should be rejected
        assertInvalidLocation("ftp://github.com/reposense/RepoSense/tree/feature_branch_issue#1010");
        // url without branch name should be rejected
        assertInvalidLocation("https://github.com/reposense/RepoSense/tree/");
        assertInvalidLocation("https://github.com/reposense/RepoSense/tree");
        // url without the 'tree' keyword should be rejected
        assertInvalidLocation("https://github.com/reposense/RepoSense/feature_branch_issue#1010");
        assertInvalidLocation("https://github.com/reposense/RepoSense/TREE/feature_branch_issue#1010");
        assertInvalidLocation("https://github.com/reposense/RepoSense/tre/feature_branch_issue#1010");
        // non GitHub url should be rejected
        assertInvalidLocation("https://gitlab.com/reposense/RepoSense/tree/feature_branch_issue#1010");
    }

    @Test
    public void repoLocationParser_parseEmptyString_noInvalidLocationException() throws Exception {
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
