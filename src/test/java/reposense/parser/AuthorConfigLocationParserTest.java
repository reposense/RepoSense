package reposense.parser;

import static reposense.parser.AuthorConfigLocationParser.BRANCHES_DELIMITER;
import static reposense.parser.AuthorConfigLocationParser.LOCATION_BRANCHES_DELIMITER;
import static reposense.parser.AuthorConfigLocationParser.parseLocation;
import static reposense.util.TestUtil.isWindows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

public class AuthorConfigLocationParserTest {
    @Test
    public void parseLocation_localLocationDefault_success() {
        String location = "/Users/sikai/RepoSense";
        String defaultSpecifiedBranch = "master";
        List<String> parsedLocationWithBranch = parseLocation(location, defaultSpecifiedBranch);

        List<String> expectedResults = new ArrayList<>();
        expectedResults.add(location);
        expectedResults.add(defaultSpecifiedBranch);

        Assertions.assertEquals(expectedResults, parsedLocationWithBranch);
    }

    @Test
    public void parseLocation_remoteLocationDefault_success() {
        String location = "https://github.com/reposense/RepoSense.git";
        String defaultSpecifiedBranch = "release";
        List<String> parsedLocationWithBranch = parseLocation(location, defaultSpecifiedBranch);

        List<String> expectedResults = new ArrayList<>();
        expectedResults.add(location);
        expectedResults.add(defaultSpecifiedBranch);

        Assertions.assertEquals(expectedResults, parsedLocationWithBranch);
    }

    @Test
    public void parseLocation_localLocationSingleBranch_success() {
        String defaultSpecifiedBranch = "master";
        String branch = "release";
        String localPath = "/Users/sikai/RepoSense";
        String location = localPath + LOCATION_BRANCHES_DELIMITER + branch;
        List<String> parsedLocationWithBranch = parseLocation(location, defaultSpecifiedBranch);

        List<String> expectedResults = new ArrayList<>();
        expectedResults.add(localPath);
        expectedResults.add(branch);

        Assertions.assertEquals(expectedResults, parsedLocationWithBranch);
    }

    @Test
    public void parseLocation_remoteLocationSingleBranch_success() {
        String defaultSpecifiedBranch = "master";
        String branch = "release";
        String remoteUrl = "https://github.com/reposense/RepoSense.git";
        String location = remoteUrl + LOCATION_BRANCHES_DELIMITER + branch;
        List<String> parsedLocationWithBranch = parseLocation(location, defaultSpecifiedBranch);

        List<String> expectedResults = new ArrayList<>();
        expectedResults.add(remoteUrl);
        expectedResults.add(branch);

        Assertions.assertEquals(expectedResults, parsedLocationWithBranch);
    }

    @Test
    public void parseLocation_localLocationSingleBranchDelimiterInPath_success() {
        String defaultSpecifiedBranch = "master";
        String branch = "release";
        String localLocation = "/Users/si" + LOCATION_BRANCHES_DELIMITER
                + "kai/Repo" + LOCATION_BRANCHES_DELIMITER
                + "Sense";
        String location = localLocation + LOCATION_BRANCHES_DELIMITER + branch;
        List<String> parsedLocationWithBranch = parseLocation(location, defaultSpecifiedBranch);

        List<String> expectedResults = new ArrayList<>();
        expectedResults.add(localLocation);
        expectedResults.add(branch);

        Assertions.assertEquals(expectedResults, parsedLocationWithBranch);
    }

    @Test
    public void parseLocation_actualLocalLocationContainingDelimiter_success() throws Exception {
        // Location contains ~ delimiter but no intended use of delimiter
        String localLocation = "delimiter" + LOCATION_BRANCHES_DELIMITER
                + "in" + LOCATION_BRANCHES_DELIMITER
                + "directory";
        Path localPath = Paths.get(localLocation);
        Files.createDirectories(localPath);
        String defaultBranch = "master";

        List<String> parsedLocationWithBranch = parseLocation(localLocation, defaultBranch);

        List<String> expectedResult = new ArrayList<>();
        expectedResult.add(localLocation);
        expectedResult.add(defaultBranch);

        Assertions.assertEquals(expectedResult, parsedLocationWithBranch);

        Files.deleteIfExists(localPath);
    }

    @Test
    public void parseLocation_actualLocalLocationContainingBothDelimiter_success() throws Exception {
        // Windows do not allow pipe as part of directory name
        Assumptions.assumeFalse(isWindows());
        // Location contains ~ and | delimiter but no intended use of delimiter
        String localLocation = "delimiter" + LOCATION_BRANCHES_DELIMITER
                + "in" + LOCATION_BRANCHES_DELIMITER
                + "directory" + BRANCHES_DELIMITER
                + "fakebranch";
        Path localPath = Paths.get(localLocation);
        Files.createDirectory(localPath);

        String defaultBranch = "master";

        List<String> parsedLocationWithBranch = parseLocation(localLocation, defaultBranch);

        List<String> expectedResult = new ArrayList<>();
        expectedResult.add(localLocation);
        expectedResult.add(defaultBranch);

        Assertions.assertEquals(expectedResult, parsedLocationWithBranch);

        Files.deleteIfExists(localPath);
    }

    @Test
    public void parseLocation_emptyStringAfterDelimiter_success() {
        String localPath = "/Users/sikai/RepoSense";
        String location = localPath + LOCATION_BRANCHES_DELIMITER;
        // defaultSpecifiedBranch is not used and is thus null
        List<String> parsedLocationWithBranch = parseLocation(location, null);

        List<String> expectedResult = new ArrayList<>();
        expectedResult.add("/Users/sikai/RepoSense");
        expectedResult.add("");

        Assertions.assertEquals(expectedResult, parsedLocationWithBranch);
    }

    @Test
    public void parseLocation_localLocationMultipleBranch_success() {
        String defaultSpecifiedBranch = "master";
        String localPath = "/Users/sikai/RepoSense";
        String branch0 = "release";
        String branch1 = "master";
        String branch2 = "cypress";
        String location = localPath + LOCATION_BRANCHES_DELIMITER + branch0
                + BRANCHES_DELIMITER + branch1 + BRANCHES_DELIMITER + branch2;
        List<String> parsedLocationWithBranch = parseLocation(location, defaultSpecifiedBranch);

        List<String> expectedResults = new ArrayList<>();
        expectedResults.add(localPath);
        expectedResults.add(branch0);
        expectedResults.add(branch1);
        expectedResults.add(branch2);

        Assertions.assertEquals(expectedResults, parsedLocationWithBranch);
    }

    @Test
    public void parseLocation_remoteLocationMultipleBranch_success() {
        String defaultSpecifiedBranch = "master";
        String remoteUrl = "https://github.com/reposense/RepoSense.git";
        String branch0 = "release";
        String branch1 = "master";
        String branch2 = "cypress";
        String location = remoteUrl + LOCATION_BRANCHES_DELIMITER + branch0
                + BRANCHES_DELIMITER + branch1 + BRANCHES_DELIMITER + branch2;
        List<String> parsedLocationWithBranch = parseLocation(location, defaultSpecifiedBranch);

        List<String> expectedResults = new ArrayList<>();
        expectedResults.add(remoteUrl);
        expectedResults.add(branch0);
        expectedResults.add(branch1);
        expectedResults.add(branch2);

        Assertions.assertEquals(expectedResults, parsedLocationWithBranch);
    }

    @Test
    public void parseLocation_githubBranchUrl_success() {
        String githubBranchUrl = "https://github.com/reposense/RepoSense/tree/release";
        String defaultSpecifiedBranch = "master";
        List<String> parsedLocationWithBranch = parseLocation(githubBranchUrl, defaultSpecifiedBranch);

        String location = "https://github.com/reposense/RepoSense.git";
        String branch = "release";
        List<String> expectedResults = new ArrayList<>();
        expectedResults.add(location);
        expectedResults.add(branch);

        Assertions.assertEquals(expectedResults, parsedLocationWithBranch);

        githubBranchUrl = "https://github.com/reposense/RepoSense/tree/master";
        defaultSpecifiedBranch = "release";
        parsedLocationWithBranch = parseLocation(githubBranchUrl, defaultSpecifiedBranch);

        location = "https://github.com/reposense/RepoSense.git";
        branch = "master";
        expectedResults = new ArrayList<>();
        expectedResults.add(location);
        expectedResults.add(branch);

        Assertions.assertEquals(expectedResults, parsedLocationWithBranch);
    }

    @Test
    public void parseLocation_faultyGithubBranchUrl_null() {
        String githubBranchUrl = "https://github.com/reposense/RepoSense/threeeee/release";
        String defaultBranch = "master";
        List<String> parsedLocationWithBranch = parseLocation(githubBranchUrl, defaultBranch);

        List<String> expectedResult = new ArrayList<>();
        expectedResult.add(githubBranchUrl);
        expectedResult.add(defaultBranch);

        Assertions.assertEquals(expectedResult, parsedLocationWithBranch);
    }
}
