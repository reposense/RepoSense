package reposense.parser;

import static reposense.parser.AuthorConfigLocationParser.BRANCHES_DELIMITER;
import static reposense.parser.AuthorConfigLocationParser.LOCATION_BRANCHES_DELIMITER;
import static reposense.parser.LocalRepoLocationParser.parseLocalRepoLocation;

import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LocalRepoLocationParserTest {
    @Test
    public void parseLocalRepoLocation_noDelimiter_success() {
        String localLocation = "/Users/sikai/RepoSense";
        String defaultBranch = "master";
        List<String> parsedLocationWithBranch = parseLocalRepoLocation(localLocation, defaultBranch);

        List<String> expectedResult = new ArrayList<>();
        expectedResult.add(localLocation);
        expectedResult.add(defaultBranch);

        Assertions.assertEquals(expectedResult, parsedLocationWithBranch);
    }

    @Test
    public void parseLocalRepoLocation_singleDelimiter_success() {
        String localLocationWithBranch = "/Users/sikai/RepoSense" + LOCATION_BRANCHES_DELIMITER + "master";
        // defaultSpecifiedBranch can be set to null as there is no need be it to be used.
        List<String> parsedLocationWithBranch = parseLocalRepoLocation(localLocationWithBranch, null);

        List<String> expectedResult = new ArrayList<>();
        expectedResult.add("/Users/sikai/RepoSense");
        expectedResult.add("master");

        Assertions.assertEquals(expectedResult, parsedLocationWithBranch);
    }

    @Test
    public void parseLocalRepoLocation_multipleDelimiter_success() {
        String localLocationWithBranch = "/Users/si~kai/Repo~Sense" + LOCATION_BRANCHES_DELIMITER + "master";
        List<String> parsedLocationWithBranch = parseLocalRepoLocation(localLocationWithBranch, null);

        List<String> expectedResult = new ArrayList<>();
        expectedResult.add("/Users/si~kai/Repo~Sense");
        expectedResult.add("master");

        Assertions.assertEquals(expectedResult, parsedLocationWithBranch);
    }

    @Test
    public void parseLocalRepoLocation_consecutiveDelimiter_success() {
        String localLocationWithBranch = "/Users/si~kai/Repo~Sense~~~" + LOCATION_BRANCHES_DELIMITER + "master";
        List<String> parsedLocationWithBranch = parseLocalRepoLocation(localLocationWithBranch, null);

        List<String> expectedResult = new ArrayList<>();
        expectedResult.add("/Users/si~kai/Repo~Sense~~~");
        expectedResult.add("master");

        Assertions.assertEquals(expectedResult, parsedLocationWithBranch);
    }

    @Test
    public void parseLocalRepoLocation_pathContainsDelimiter_null() throws Exception {
        // Path contains delimiter but no intended use of delimiter
        try {
            Files.createDirectory(Paths.get("delimiter~in~directory"));
        } catch (FileAlreadyExistsException e) {
            // should ignore exception
        }
        String localLocation = Paths.get("delimiter~in~directory").toString();
        String defaultBranch = "master";

        List<String> parsedLocationWithBranch = parseLocalRepoLocation(localLocation, defaultBranch);

        List<String> expectedResult = new ArrayList<>();
        expectedResult.add(localLocation);
        expectedResult.add(defaultBranch);

        Assertions.assertEquals(expectedResult, parsedLocationWithBranch);

        Files.deleteIfExists(Paths.get("delimiter~in~directory"));
    }

    @Test
    public void parseLocalRepoLocation_emptyStringAfterDelimiter_success() {
        String localLocationWithBranch = "/Users/sikai/RepoSense" + LOCATION_BRANCHES_DELIMITER;
        List<String> parsedLocationWithBranch = parseLocalRepoLocation(localLocationWithBranch, null);

        List<String> expectedResult = new ArrayList<>();
        expectedResult.add("/Users/sikai/RepoSense");
        expectedResult.add("");

        Assertions.assertEquals(expectedResult, parsedLocationWithBranch);
    }

    @Test
    public void parseLocalRepoLocation_multipleBranches_success() {
        String localLocationWithBranches = "/Users/sikai/RepoSense"
                + LOCATION_BRANCHES_DELIMITER + "master"
                + BRANCHES_DELIMITER + "test"
                + BRANCHES_DELIMITER + "release";
        List<String> parsedLocationWithBranch = parseLocalRepoLocation(localLocationWithBranches, null);

        List<String> expectedResult = new ArrayList<>();
        expectedResult.add("/Users/sikai/RepoSense");
        expectedResult.add("master");
        expectedResult.add("test");
        expectedResult.add("release");

        Assertions.assertEquals(expectedResult, parsedLocationWithBranch);
    }

    @Test
    public void parseLocalRepoLocation_multipleBranchSomeEmptyString_success() {
        String remoteLocationWithBranch = "/Users/sikai/RepoSense"
                + LOCATION_BRANCHES_DELIMITER + "release"
                + BRANCHES_DELIMITER
                + BRANCHES_DELIMITER;
        List<String> parsedLocation = parseLocalRepoLocation(remoteLocationWithBranch, null);

        List<String> expectedResult = new ArrayList<>();
        expectedResult.add("/Users/sikai/RepoSense");
        expectedResult.add("release");

        Assertions.assertEquals(expectedResult, parsedLocation);
    }

    @Test
    public void parseRemoteLocation_multipleBranchSomeEmptyString2_success() {
        String remoteLocationWithBranch = "/Users/sikai/RepoSense"
                + LOCATION_BRANCHES_DELIMITER
                + BRANCHES_DELIMITER + "release"
                + BRANCHES_DELIMITER; // last delimiter is ignored due to semantics of the delimiter
        List<String> parsedLocation = parseLocalRepoLocation(remoteLocationWithBranch, null);

        List<String> expectedResult = new ArrayList<>();
        expectedResult.add("/Users/sikai/RepoSense");
        expectedResult.add("");
        expectedResult.add("release");

        Assertions.assertEquals(expectedResult, parsedLocation);
    }

    @Test
    public void parseLocalLocation_remoteLocation_assertionError() {
        String remoteLocationWithBranch = "https://github.com/reposense/RepoSense.git"
                + LOCATION_BRANCHES_DELIMITER + "master";

        Assertions.assertThrows(AssertionError.class, () ->
                parseLocalRepoLocation(remoteLocationWithBranch, null));
    }
}
