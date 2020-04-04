package reposense.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

import reposense.parser.InvalidLocationException;
import reposense.util.AssertUtil;

public class RepoLocationTest {

    @Test
    public void repoLocation_parseRepoUrl_success() throws Exception {
        String repoUrl = "https://github.com/reposense/RepoSense.git";
        RepoLocation repoLocation = new RepoLocation(repoUrl);
        assertEquals(repoUrl, repoLocation.toString());
        assertEquals("RepoSense", repoLocation.getRepoName());
        assertEquals("reposense", repoLocation.getOrganization());
        assertFalse(repoLocation.getParsedBranch().isPresent());

        String repoUrlWithBranch = "https://github.com/reposense/testrepo-Alpha.git#release";
        repoLocation = new RepoLocation(repoUrlWithBranch);
        assertEquals("https://github.com/reposense/testrepo-Alpha.git", repoLocation.toString());
        assertEquals("testrepo-Alpha", repoLocation.getRepoName());
        assertEquals("reposense", repoLocation.getOrganization());
        assertEquals("release", repoLocation.getParsedBranch().get());
    }

    @Test
    public void repoLocation_parseInvalidRepoUrl_throwsInvalidLocationException() {
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
    public void repoLocation_parseBranchUrl_success() throws Exception {
        String branchUrl = "https://github.com/reposense/RepoSense/tree/feature_branch_issue#1010";
        RepoLocation repoLocation = new RepoLocation(branchUrl);
        assertEquals("https://github.com/reposense/RepoSense.git", repoLocation.toString());
        assertEquals("RepoSense", repoLocation.getRepoName());
        assertEquals("reposense", repoLocation.getOrganization());
        assertEquals("feature_branch_issue#1010", repoLocation.getParsedBranch().get());
    }

    @Test
    public void repoLocation_parseInvalidBranchUrl_throwsInvalidLocationException() {
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

    private static void assertInvalidLocation(String rawLocation) {
        AssertUtil.assertThrows(InvalidLocationException.class,
                RepoLocation.MESSAGE_INVALID_LOCATION, () -> new RepoLocation(rawLocation));
    }

}
