package reposense.parser;

import static reposense.parser.AuthorConfigLocationParser.LOCATION_BRANCHES_DELIMITER;
import static reposense.parser.AuthorConfigLocationParser.parseLocation;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AuthorConfigLocationParserTest {
    @Test
    public void parseLocation_localLocationNoSpecialSyntax_success() {
        String location = "/Users/sikai/RepoSense";
        String defaultSpecifiedBranch = "master";
        List<String> parsedLocationWithBranch = parseLocation(location, defaultSpecifiedBranch);

        List<String> expectedResults = new ArrayList<>();
        expectedResults.add(location);
        expectedResults.add(defaultSpecifiedBranch);

        Assertions.assertEquals(expectedResults, parsedLocationWithBranch);
    }

    @Test
    public void parseLocation_remoteLocationNoSpecialSyntax_success() {
        String location = "https://github.com/reposense/RepoSense.git";
        String defaultSpecifiedBranch = "release";
        List<String> parsedLocationWithBranch = parseLocation(location, defaultSpecifiedBranch);

        List<String> expectedResults = new ArrayList<>();
        expectedResults.add(location);
        expectedResults.add(defaultSpecifiedBranch);

        Assertions.assertEquals(expectedResults, parsedLocationWithBranch);
    }

    @Test
    public void parseLocation_localLocationDelimiter_success() {
        String defaultSpecifiedBranch = "master";
        String branch = "release";
        String location = "/Users/sikai/RepoSense" + LOCATION_BRANCHES_DELIMITER + branch;
        List<String> parsedLocationWithBranch = parseLocation(location, defaultSpecifiedBranch);

        List<String> expectedResults = new ArrayList<>();
        String expectedLocation = "/Users/sikai/RepoSense";
        String expectedBranch = "release";
        expectedResults.add(expectedLocation);
        expectedResults.add(expectedBranch);

        Assertions.assertEquals(expectedResults, parsedLocationWithBranch);
    }

    @Test
    public void parseLocation_remoteLocationDelimiter_success() {
        String defaultSpecifiedBranch = "master";
        String branch = "release";
        String location = "https://github.com/reposense/RepoSense.git" + LOCATION_BRANCHES_DELIMITER + branch;
        List<String> parsedLocationWithBranch = parseLocation(location, defaultSpecifiedBranch);

        List<String> expectedResults = new ArrayList<>();
        String expectedLocation = "https://github.com/reposense/RepoSense.git";
        String expectedBranch = "release";
        expectedResults.add(expectedLocation);
        expectedResults.add(expectedBranch);

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
}
