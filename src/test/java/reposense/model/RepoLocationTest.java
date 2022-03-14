package reposense.model;

import static reposense.model.RepoLocation.UNSUPPORTED_DOMAIN_NAME;
import static reposense.model.RepoLocation.isLocalRepo;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;

import reposense.parser.InvalidLocationException;
import reposense.util.AssertUtil;
import reposense.util.SystemUtil;

public class RepoLocationTest {

    private static final String LOCAL_REPO_VALID_WITHOUT_DOT_GIT_ONE = "repo";
    private static final String LOCAL_REPO_VALID_WITHOUT_DOT_GIT_TWO = "../path/to/repo";
    private static final String LOCAL_REPO_VALID_WITHOUT_DOT_GIT_THREE = "/path/to/repo";
    private static final String LOCAL_REPO_VALID_WITH_DOT_GIT_ONE = "path/to/repo/.git";
    private static final String LOCAL_REPO_VALID_WITH_DOT_GIT_TWO = "file://path/to/repo.git";
    private static final String LOCAL_REPO_FILE_URL_ONE = "file://path/to/repo";
    private static final String LOCAL_REPO_FILE_URL_TWO = "file:///path/to/repo";

    private static final String LOCAL_REPO_WINDOWS_VALID_WITHOUT_DOT_GIT_ONE = "path\\to\\repo\\";
    private static final String LOCAL_REPO_WINDOWS_VALID_WITHOUT_DOT_GIT_TWO = "..\\path\\to\\repo";
    private static final String LOCAL_REPO_WINDOWS_VALID_WITH_DOT_GIT_ONE = "path\\to\\repo\\.git";
    private static final String LOCAL_REPO_WINDOWS_DISK_DRIVE = "C:\\path\\to\\repo.git";

    private static final String LOCAL_REPO_WINDOWS_VALID_MIXED_ONE = "..\\path/to\\repo";
    private static final String LOCAL_REPO_WINDOWS_VALID_MIXED_TWO = "file://path\\to\\repo.git";
    private static final String LOCAL_REPO_WINDOWS_DISK_DRIVE_MIXED = "C:\\path/to/repo.git";

    private static final String EXPECTED_REPO_NAME = "repo";
    private static final String EXPECTED_ORGANIZATION = "path-to";
    private static final String EXPECTED_DOMAIN_NAME = "github";
    private static final String EXPECTED_UNRECOGNISED_DOMAIN_NAME = UNSUPPORTED_DOMAIN_NAME;

    @Test
    public void repoLocation_parseEmptyString_success() throws Exception {
        RepoLocation repoLocation = new RepoLocation("");
    }

    @Test
    public void isLocalRepo_validLocalRepos_success() throws Exception {
        Assert.assertTrue(isLocalRepo(LOCAL_REPO_VALID_WITH_DOT_GIT_ONE));
        Assert.assertTrue(isLocalRepo(LOCAL_REPO_WINDOWS_VALID_MIXED_ONE));
        Assert.assertTrue(isLocalRepo("./abc:def"));
    }

    @Test
    public void isLocalRepo_remoteRepos_returnsFalse() throws Exception {
        Assert.assertFalse(isLocalRepo("https://github.com/reposense/RepoSense.git/"));
        Assert.assertFalse(isLocalRepo("git@github.com:reposense/RepoSense.git/"));
    }

    @Test
    public void repoLocation_parseLocalRepoLocation_success() throws Exception {
        // local paths not containing ".git" should be valid
        assertParsableLocation(LOCAL_REPO_VALID_WITHOUT_DOT_GIT_ONE,
                EXPECTED_REPO_NAME, "", EXPECTED_UNRECOGNISED_DOMAIN_NAME);
        // relative pathing should be considered part of the 'organization' for differentiation
        assertParsableLocation(LOCAL_REPO_VALID_WITHOUT_DOT_GIT_TWO,
                EXPECTED_REPO_NAME, "..-" + EXPECTED_ORGANIZATION, EXPECTED_UNRECOGNISED_DOMAIN_NAME);
        assertParsableLocation(LOCAL_REPO_VALID_WITHOUT_DOT_GIT_THREE,
                EXPECTED_REPO_NAME, "-" + EXPECTED_ORGANIZATION, EXPECTED_UNRECOGNISED_DOMAIN_NAME);

        // local paths containing ".git" should also be valid
        assertParsableLocation(LOCAL_REPO_VALID_WITH_DOT_GIT_ONE,
                EXPECTED_REPO_NAME, EXPECTED_ORGANIZATION, EXPECTED_UNRECOGNISED_DOMAIN_NAME);
        assertParsableLocation(LOCAL_REPO_VALID_WITH_DOT_GIT_TWO,
                EXPECTED_REPO_NAME, EXPECTED_ORGANIZATION, EXPECTED_UNRECOGNISED_DOMAIN_NAME);

        // file-type url protocol (file://) is accepted by git clone
        assertParsableLocation(LOCAL_REPO_FILE_URL_ONE,
                EXPECTED_REPO_NAME, EXPECTED_ORGANIZATION, EXPECTED_UNRECOGNISED_DOMAIN_NAME);
        assertParsableLocation(LOCAL_REPO_FILE_URL_TWO,
                EXPECTED_REPO_NAME, "-" + EXPECTED_ORGANIZATION, EXPECTED_UNRECOGNISED_DOMAIN_NAME);
    }

    @Test
    public void repoLocation_parseWindowsLocalRepoLocation_success() throws Exception {
        Assume.assumeTrue(SystemUtil.isWindows());
        // repeated tests but with windows file separators
        assertParsableLocation(LOCAL_REPO_WINDOWS_VALID_WITHOUT_DOT_GIT_ONE,
                EXPECTED_REPO_NAME, EXPECTED_ORGANIZATION, EXPECTED_UNRECOGNISED_DOMAIN_NAME);
        assertParsableLocation(LOCAL_REPO_WINDOWS_VALID_WITHOUT_DOT_GIT_TWO,
                EXPECTED_REPO_NAME, "..-" + EXPECTED_ORGANIZATION, EXPECTED_UNRECOGNISED_DOMAIN_NAME);
        assertParsableLocation(LOCAL_REPO_WINDOWS_VALID_WITH_DOT_GIT_ONE,
                EXPECTED_REPO_NAME, EXPECTED_ORGANIZATION, EXPECTED_UNRECOGNISED_DOMAIN_NAME);
        assertParsableLocation(LOCAL_REPO_WINDOWS_DISK_DRIVE,
                EXPECTED_REPO_NAME, "C--" + EXPECTED_ORGANIZATION, EXPECTED_UNRECOGNISED_DOMAIN_NAME);

        assertParsableLocation(LOCAL_REPO_WINDOWS_VALID_MIXED_ONE,
                EXPECTED_REPO_NAME, "..-" + EXPECTED_ORGANIZATION, EXPECTED_UNRECOGNISED_DOMAIN_NAME);
        assertParsableLocation(LOCAL_REPO_WINDOWS_VALID_MIXED_TWO,
                EXPECTED_REPO_NAME, EXPECTED_ORGANIZATION, EXPECTED_UNRECOGNISED_DOMAIN_NAME);
        assertParsableLocation(LOCAL_REPO_WINDOWS_DISK_DRIVE_MIXED,
                EXPECTED_REPO_NAME, "C--" + EXPECTED_ORGANIZATION, EXPECTED_UNRECOGNISED_DOMAIN_NAME);

    }

    @Test
    public void repoLocation_parseValidRemoteRepoUrl_success() throws Exception {
        // valid url without specifying branch
        assertParsableLocation("https://github.com/reposense/testrepo-Beta.git",
                "testrepo-Beta", "reposense", EXPECTED_DOMAIN_NAME);
        assertParsableLocation("https://github.com/reposense/testrepo-Delta.git",
                "testrepo-Delta", "reposense", EXPECTED_DOMAIN_NAME);
        assertParsableLocation("https://gitlab.com/reposense/RepoSense.git",
                "RepoSense", "reposense", "gitlab");
        assertParsableLocation("https://github.com/reposense.git",
                "reposense", "", EXPECTED_DOMAIN_NAME);
        // valid url to parse for obtaining repo and organization, just not a valid git clone target
        assertParsableLocation("https://github.com/reposense/.git",
                "reposense", "", EXPECTED_DOMAIN_NAME);

        // valid url from other domains
        assertParsableLocation("https://bitbucket.org/reposense/RepoSense.git",
                "RepoSense", "reposense", "bitbucket");
        // valid url from unsupported domain with longer path to git directory than the standard organization/reponame
        assertParsableLocation("https://opensource.ncsa.illinois.edu/bitbucket/scm/u3d/3dutilities.git",
                "3dutilities", "bitbucket-scm-u3d", EXPECTED_UNRECOGNISED_DOMAIN_NAME);

        // treated as valid but will be caught when git clone fails
        assertParsableLocation("https://github.com/reposense/testrepo-Beta/tree/add-config-json",
                "add-config-json", "reposense-testrepo-Beta-tree", EXPECTED_DOMAIN_NAME);
        assertParsableLocation("https://github.com/reposense/testrepo-Beta.git/tree/add-config-json",
                "add-config-json", "reposense-testrepo-Beta.git-tree", EXPECTED_DOMAIN_NAME);

        // URLs without ".git" should be accepted as git clone works even without it
        assertParsableLocation("https://github.com/reposense",
                "reposense", "", EXPECTED_DOMAIN_NAME);
        assertParsableLocation("https://github.com/reposense/RepoSense",
                "RepoSense", "reposense", EXPECTED_DOMAIN_NAME);

        // Test against other types of URL protocols that are valid for git clone
        assertParsableLocation("ssh://git@github.com/path/to/repo.git/",
                EXPECTED_REPO_NAME, EXPECTED_ORGANIZATION, EXPECTED_DOMAIN_NAME);
        assertParsableLocation("git://github.com/path/to/repo.git",
                EXPECTED_REPO_NAME, EXPECTED_ORGANIZATION, EXPECTED_DOMAIN_NAME);
        assertParsableLocation("https://localhost:9000/path/to/repo.git",
                EXPECTED_REPO_NAME, EXPECTED_ORGANIZATION, EXPECTED_UNRECOGNISED_DOMAIN_NAME);

        // Test against the conventional ssh protocol used for GitHub, e.g. git@github.com:reposense/RepoSense.git
        assertParsableLocation("repo@organization.com:path/to/repo.git/",
                EXPECTED_REPO_NAME, EXPECTED_ORGANIZATION, EXPECTED_UNRECOGNISED_DOMAIN_NAME);
        assertParsableLocation("git@github.com:reposense/RepoSense.git",
                "RepoSense", "reposense", EXPECTED_DOMAIN_NAME);
    }

    @Test
    public void repoLocation_parseNormalizableRepoLocations_success() throws Exception {
        assertParsableLocation("https://github.com/reposense/redundant/directories/../../RepoSense.git",
                "RepoSense", "reposense", EXPECTED_DOMAIN_NAME);
        assertParsableLocation("/path/with/redundant/directories/../.././../to/repo",
                EXPECTED_REPO_NAME, "-" + EXPECTED_ORGANIZATION, EXPECTED_UNRECOGNISED_DOMAIN_NAME);

        // Tests if there is an additional '../' it is not consumed by accident
        assertParsableLocation("path/with/redundant/directories/../../../../../to/repo",
                EXPECTED_REPO_NAME, "..-to", EXPECTED_UNRECOGNISED_DOMAIN_NAME);
    }

    @Test
    public void repoLocation_parseInvalidRemoteRepo_throwsInvalidLocationException() throws Exception {
        // Invalid URL protocol
        assertUnparsableLocation("ttp://github.com/reposense.RepoSense.git");
        assertUnparsableLocation("not-valid-protocol://abc.com/reposense/RepoSense.git");
        // URL contains illegal characters
        assertUnparsableLocation("https://github.com/contains-illegal-chars/^\\/");
    }

    /**
     * Compares the information parsed by the RepoLocation model with the expected information
     */
    public void assertParsableLocation(String rawLocation, String expectedRepoName,
            String expectedOrganization, String expectedDomainName) throws Exception {
        RepoLocation repoLocation = new RepoLocation(rawLocation);
        Assert.assertEquals(expectedRepoName, repoLocation.getRepoName());
        Assert.assertEquals(expectedOrganization, repoLocation.getOrganization());
        Assert.assertEquals(expectedDomainName, repoLocation.getDomainName());
    }

    private void assertUnparsableLocation(String rawLocation) {
        AssertUtil.assertThrows(InvalidLocationException.class, () -> new RepoLocation(rawLocation));
    }
}
