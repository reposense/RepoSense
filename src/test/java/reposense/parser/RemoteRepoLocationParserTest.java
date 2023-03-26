package reposense.parser;

import static reposense.parser.AuthorConfigLocationParser.BRANCHES_DELIMITER;
import static reposense.parser.AuthorConfigLocationParser.LOCATION_BRANCHES_DELIMITER;
import static reposense.parser.RemoteRepoLocationParser.parseRemoteRepoLocation;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class RemoteRepoLocationParserTest {
    @Test
    public void parseRemoteLocation_noDelimiterNotBranchUrl_null() {
        String remoteLocation = "https://github.com/reposense/RepoSense.git";
        String defaultBranch = "master";
        List<String> parsedLocationWithBranch = parseRemoteRepoLocation(remoteLocation, defaultBranch);

        List<String> expectedResult = new ArrayList<>();
        expectedResult.add(remoteLocation);
        expectedResult.add(defaultBranch);

        Assertions.assertEquals(expectedResult, parsedLocationWithBranch);
    }

    @Test
    public void parseRemoteLocation_delimiterNotBranchUrl_success() {
        String remoteLocationWithBranch = "https://github.com/reposense/RepoSense.git"
                + LOCATION_BRANCHES_DELIMITER + "master";
        // defaultSpecifiedBranch can be set to null as there is no need be it to be used.
        List<String> parsedLocationWithBranch = parseRemoteRepoLocation(remoteLocationWithBranch, null);

        List<String> expectedResult = new ArrayList<>();
        expectedResult.add("https://github.com/reposense/RepoSense.git");
        expectedResult.add("master");

        Assertions.assertEquals(expectedResult, parsedLocationWithBranch);

        String remoteLocationWithDifferentBranch = "https://github.com/reposense/RepoSense.git"
                + LOCATION_BRANCHES_DELIMITER + "release";
        List<String> parsedLocationWithDifferentBranch = parseRemoteRepoLocation(
                remoteLocationWithDifferentBranch, null);

        List<String> expectedResult2 = new ArrayList<>();
        expectedResult2.add("https://github.com/reposense/RepoSense.git");
        expectedResult2.add("release");

        Assertions.assertEquals(expectedResult2, parsedLocationWithDifferentBranch);
    }

    @Test
    public void parseRemoteLocation_emptyStringAfterDelimiter_success() {
        String remoteLocationWithBranch = "https://github.com/reposense/RepoSense.git" + LOCATION_BRANCHES_DELIMITER;
        List<String> parsedLocation = parseRemoteRepoLocation(remoteLocationWithBranch, null);

        List<String> expectedResult = new ArrayList<>();
        expectedResult.add("https://github.com/reposense/RepoSense.git");
        expectedResult.add("");

        Assertions.assertEquals(expectedResult, parsedLocation);
    }

    @Test
    public void parseRemoteLocation_multipleBranch_success() {
        String remoteLocationWithBranch = "https://github.com/reposense/RepoSense.git"
                + LOCATION_BRANCHES_DELIMITER + "master"
                + BRANCHES_DELIMITER + "release"
                + BRANCHES_DELIMITER + "test";
        List<String> parsedLocation = parseRemoteRepoLocation(remoteLocationWithBranch, null);

        List<String> expectedResult = new ArrayList<>();
        expectedResult.add("https://github.com/reposense/RepoSense.git");
        expectedResult.add("master");
        expectedResult.add("release");
        expectedResult.add("test");

        Assertions.assertEquals(expectedResult, parsedLocation);
    }

    @Test
    public void parseRemoteLocation_multipleBranchEmptyString_success() {
        String remoteLocationWithBranch = "https://github.com/reposense/RepoSense.git"
                + LOCATION_BRANCHES_DELIMITER
                + BRANCHES_DELIMITER;
        List<String> parsedLocation = parseRemoteRepoLocation(remoteLocationWithBranch, null);

        List<String> expectedResult = new ArrayList<>();
        expectedResult.add("https://github.com/reposense/RepoSense.git");
        expectedResult.add("");

        Assertions.assertEquals(expectedResult, parsedLocation);
    }

    @Test
    public void parseRemoteLocation_multipleBranchSomeEmptyString_success() {
        String remoteLocationWithBranch = "https://github.com/reposense/RepoSense.git"
                + LOCATION_BRANCHES_DELIMITER + "release"
                + BRANCHES_DELIMITER
                + BRANCHES_DELIMITER;
        List<String> parsedLocation = parseRemoteRepoLocation(remoteLocationWithBranch, null);

        List<String> expectedResult = new ArrayList<>();
        expectedResult.add("https://github.com/reposense/RepoSense.git");
        expectedResult.add("release");

        Assertions.assertEquals(expectedResult, parsedLocation);
    }

    @Test
    public void parseRemoteLocation_multipleBranchSomeEmptyString2_success() {
        String remoteLocationWithBranch = "https://github.com/reposense/RepoSense.git"
                + LOCATION_BRANCHES_DELIMITER
                + BRANCHES_DELIMITER + "release"
                + BRANCHES_DELIMITER; // last delimiter is ignored due to semantics of the delimiter
        List<String> parsedLocation = parseRemoteRepoLocation(remoteLocationWithBranch, null);

        List<String> expectedResult = new ArrayList<>();
        expectedResult.add("https://github.com/reposense/RepoSense.git");
        expectedResult.add("");
        expectedResult.add("release");

        Assertions.assertEquals(expectedResult, parsedLocation);
    }

    @Test
    public void parseRemoteLocation_githubBranchUrl_success() {
        String githubBranchUrl = "https://github.com/reposense/RepoSense/tree/release";
        List<String> parsedLocationWithBranch = parseRemoteRepoLocation(githubBranchUrl, null);

        List<String> expectedResult = new ArrayList<>();
        expectedResult.add("https://github.com/reposense/RepoSense.git");
        expectedResult.add("release");

        Assertions.assertEquals(expectedResult, parsedLocationWithBranch);

        githubBranchUrl = "https://github.com/reposense/RepoSense/tree/master";
        parsedLocationWithBranch = parseRemoteRepoLocation(githubBranchUrl, null);

        expectedResult = new ArrayList<>();
        expectedResult.add("https://github.com/reposense/RepoSense.git");
        expectedResult.add("master");

        Assertions.assertEquals(expectedResult, parsedLocationWithBranch);
    }

    @Test
    public void parseRemoteLocation_faultyGithubBranchUrl_null() {
        String githubBranchUrl = "https://github.com/reposense/RepoSense/threeeee/release";
        String defaultBranch = "master";
        List<String> parsedLocationWithBranch = parseRemoteRepoLocation(githubBranchUrl, defaultBranch);

        List<String> expectedResult = new ArrayList<>();
        expectedResult.add(githubBranchUrl);
        expectedResult.add(defaultBranch);

        Assertions.assertEquals(expectedResult, parsedLocationWithBranch);
    }

    @Test
    public void parseRemoteLocation_localLocation_assertionError() {
        String remoteLocationWithBranch = "/Users/sikai/RepoSense"
                + LOCATION_BRANCHES_DELIMITER + "master";

        Assertions.assertThrows(AssertionError.class, () ->
                parseRemoteRepoLocation(remoteLocationWithBranch, null));
    }
}
