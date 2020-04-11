package reposense.parser;

import static org.junit.Assert.assertArrayEquals;

import org.junit.Test;

import reposense.util.AssertUtil;

public class RepoLocationParserTest {

    @Test
    public void repoLocationParser_parseRepoUrl_success() throws Exception {
        String repoUrl = "https://github.com/reposense/RepoSense.git";
        assertValidLocation(repoUrl, repoUrl, "RepoSense", "reposense", null);

        String repoUrlWithBranch = "https://github.com/reposense/testrepo-Alpha.git#release";
        assertValidLocation(repoUrlWithBranch, "https://github.com/reposense/testrepo-Alpha.git",
                "testrepo-Alpha", "reposense", "release");
    }

    @Test
    public void repoLocationParser_parseInvalidRepoUrl_throwsInvalidLocationException() {
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
    public void repoLocationParser_parseBranchUrl_success() throws Exception {
        String branchUrl = "https://github.com/reposense/RepoSense/tree/feature_branch_issue#1010";
        assertValidLocation(branchUrl, "https://github.com/reposense/RepoSense.git",
                 "RepoSense", "reposense", "feature_branch_issue#1010");
    }

    @Test
    public void repoLocationParser_parseInvalidBranchUrl_throwsInvalidLocationException() {
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
        RepoLocationParser.parse("");
    }

    private static void assertInvalidLocation(String rawLocation) {
        AssertUtil.assertThrows(InvalidLocationException.class,
                RepoLocationParser.MESSAGE_INVALID_LOCATION, () -> RepoLocationParser.parse(rawLocation));
    }

    private static void assertValidLocation(String rawLocation, String expectedLocation, String expectedRepoName,
            String expectedOrg, String expectedBranch) throws Exception {
        String[] actualRepoLocationDetails = RepoLocationParser.parse(rawLocation);
        assertArrayEquals(new String[] {expectedLocation, expectedRepoName, expectedOrg, expectedBranch },
                actualRepoLocationDetails);
    }

}
