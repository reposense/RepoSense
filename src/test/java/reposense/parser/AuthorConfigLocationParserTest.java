package reposense.parser;

import static reposense.parser.AuthorConfigLocationParser.parseLocation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AuthorConfigLocationParserTest {
    @Test
    public void parseLocation_localLocationNoSpecialSyntax_success() {
        String location = "/Users/sikai/RepoSense";
        String defaultSpecifiedBranch = "master";
        String[] parsedLocationWithBranch = parseLocation(location, defaultSpecifiedBranch);

        String parsedLocation = parsedLocationWithBranch[0];
        String parsedBranch = parsedLocationWithBranch[1];

        Assertions.assertEquals("/Users/sikai/RepoSense", parsedLocation);
        Assertions.assertEquals("master", parsedBranch);
    }

    @Test
    public void parseLocation_remoteLocationNoSpecialSyntax_success() {
        String location = "https://github.com/reposense/RepoSense.git";
        String defaultSpecifiedBranch = "release";
        String[] parsedLocationWithBranch = parseLocation(location, defaultSpecifiedBranch);

        String parsedLocation = parsedLocationWithBranch[0];
        String parsedBranch = parsedLocationWithBranch[1];

        Assertions.assertEquals("https://github.com/reposense/RepoSense.git", parsedLocation);
        Assertions.assertEquals("release", parsedBranch);
    }

    @Test
    public void parseLocation_localLocationDelimiter_success() {
        String location = "/Users/sikai/RepoSense|release";
        String defaultSpecifiedBranch = "master";
        String[] parsedLocationWithBranch = parseLocation(location, defaultSpecifiedBranch);

        String parsedLocation = parsedLocationWithBranch[0];
        String parsedBranch = parsedLocationWithBranch[1];

        Assertions.assertEquals("/Users/sikai/RepoSense", parsedLocation);
        Assertions.assertEquals("release", parsedBranch);
    }

    @Test
    public void parseLocation_remoteLocationDelimiter_success() {
        String location = "https://github.com/reposense/RepoSense.git|release";
        String defaultSpecifiedBranch = "master";
        String[] parsedLocationWithBranch = parseLocation(location, defaultSpecifiedBranch);

        String parsedLocation = parsedLocationWithBranch[0];
        String parsedBranch = parsedLocationWithBranch[1];

        Assertions.assertEquals("https://github.com/reposense/RepoSense.git", parsedLocation);
        Assertions.assertEquals("release", parsedBranch);
    }

    @Test
    public void parseLocation_githubBranchUrl_success() {
        String githubBranchUrl = "https://github.com/reposense/RepoSense/tree/release";
        String defaultSpecifiedBranch = "master";
        String[] parsedLocationWithBranch = parseLocation(githubBranchUrl, defaultSpecifiedBranch);

        String parsedLocation = parsedLocationWithBranch[0];
        String parsedBranch = parsedLocationWithBranch[1];

        Assertions.assertEquals("https://github.com/reposense/RepoSense.git", parsedLocation);
        Assertions.assertEquals("release", parsedBranch);
    }

    @Test
    public void parseLocation_githubBranchUrl2_success() {
        String githubBranchUrl = "https://github.com/reposense/RepoSense/tree/master";
        String defaultSpecifiedBranch = "release";
        String[] parsedLocationWithBranch = parseLocation(githubBranchUrl, defaultSpecifiedBranch);

        String parsedLocation = parsedLocationWithBranch[0];
        String parsedBranch = parsedLocationWithBranch[1];

        Assertions.assertEquals("https://github.com/reposense/RepoSense.git", parsedLocation);
        Assertions.assertEquals("master", parsedBranch);
    }
}
