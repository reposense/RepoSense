package reposense.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

import reposense.parser.InvalidLocationException;
import reposense.util.AssertUtil;

public class RepoLocationTest {

    @Test
    public void repoLocation_parseGitUrl_success() throws Exception {
        String gitUrl = "https://github.com/reposense/RepoSense.git";
        RepoLocation repoLocation = new RepoLocation(gitUrl);
        assertEquals(gitUrl, repoLocation.toString());
        assertEquals("RepoSense", repoLocation.getRepoName());
        assertEquals("reposense", repoLocation.getOrganization());
        assertFalse(repoLocation.getParsedBranch().isPresent());

        String gitUrlWithBranch = "https://github.com/reposense/testrepo-Alpha.git#release";
        repoLocation = new RepoLocation(gitUrlWithBranch);
        assertEquals("https://github.com/reposense/testrepo-Alpha.git", repoLocation.toString());
        assertEquals("testrepo-Alpha", repoLocation.getRepoName());
        assertEquals("reposense", repoLocation.getOrganization());
        assertEquals("release", repoLocation.getParsedBranch().get());
    }

    @Test
    public void repoLocation_parseInvalidGitUrl_throwsInvalidLocationException() {
        // non GitHub url should rejected
        AssertUtil.assertThrows(InvalidLocationException.class, () ->
                new RepoLocation("git.com/reposense/RepoSense.git"));
        // url without organisation name should be rejected
        AssertUtil.assertThrows(InvalidLocationException.class, () ->
                new RepoLocation("https://github.com/RepoSense.git"));
        // url with a typo should be rejected
        AssertUtil.assertThrows(InvalidLocationException.class, () ->
                new RepoLocation("https://github.com/RepoSense.gi"));
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
    public void repoLocation_parseInvalidBranchUrl_throwsInvalidLocationException() throws Exception {
        // ftp url should be rejected
        AssertUtil.assertThrows(InvalidLocationException.class, () ->
                new RepoLocation("ftp://github.com/reposense/RepoSense/tree/feature_branch_issue#1010"));
        // url without branch name should be rejected
        AssertUtil.assertThrows(InvalidLocationException.class, () ->
                new RepoLocation("https://github.com/reposense/RepoSense/tree/"));
    }

}
