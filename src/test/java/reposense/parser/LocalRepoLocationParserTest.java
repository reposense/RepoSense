package reposense.parser;

import static reposense.parser.LocalRepoLocationParser.parseLocalRepoLocation;
import static reposense.util.TestUtil.loadResource;

import java.io.File;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LocalRepoLocationParserTest {
    @Test
    public void parseLocalRepoLocation_noDelimiter_null() {
        String localLocation = "/Users/sikai/RepoSense";
        Assertions.assertNull(parseLocalRepoLocation(localLocation));
    }

    @Test
    public void parseLocalRepoLocation_singleDelimiter_success() {
        String localLocationWithBranch = "/Users/sikai/RepoSense|master";
        String[] parsedLocationWithBranch = parseLocalRepoLocation(localLocationWithBranch);

        String parsedLocation = parsedLocationWithBranch[0];
        String parsedBranch = parsedLocationWithBranch[1];

        Assertions.assertEquals("/Users/sikai/RepoSense", parsedLocation);
        Assertions.assertEquals("master", parsedBranch);
    }

    @Test
    public void parseLocalRepoLocation_multipleDelimiter_success() {
        String localLocationWithBranch = "/Users/si|kai/Repo|Sense|master";
        String[] parsedLocationWithBranch = parseLocalRepoLocation(localLocationWithBranch);

        String parsedLocation = parsedLocationWithBranch[0];
        String parsedBranch = parsedLocationWithBranch[1];

        Assertions.assertEquals("/Users/si|kai/Repo|Sense", parsedLocation);
        Assertions.assertEquals("master", parsedBranch);
    }

    @Test
    public void parseLocalRepoLocation_consecutiveDelimiter_success() {
        String localLocationWithBranch = "/Users/si|kai/Repo|Sense||||master";
        String[] parsedLocationWithBranch = parseLocalRepoLocation(localLocationWithBranch);

        String parsedLocation = parsedLocationWithBranch[0];
        String parsedBranch = parsedLocationWithBranch[1];

        Assertions.assertEquals("/Users/si|kai/Repo|Sense|||", parsedLocation);
        Assertions.assertEquals("master", parsedBranch);
    }

    @Test
    public void parseLocalRepoLocation_pathContainsDelimiter_null() {
        // Path contains delimiter but no intended use of delimiter
        String localLocation = loadResource(LocalRepoLocationParserTest.class,
                "LocalRepoLocationParserTest/delimiter|in|directory").toString();

        String[] parsedLocationWithBranch = parseLocalRepoLocation(localLocation);
        Assertions.assertNull(parsedLocationWithBranch);
    }

    @Test
    public void parseLocalRepoLocation_emptyStringAfterDelimiter_success() {
        String localLocationWithBranch = "/Users/sikai/RepoSense|";
        String[] parsedLocationWithBranch = parseLocalRepoLocation(localLocationWithBranch);

        String parsedLocation = parsedLocationWithBranch[0];
        String parsedBranch = parsedLocationWithBranch[1];

        Assertions.assertEquals("/Users/sikai/RepoSense", parsedLocation);
        Assertions.assertEquals("", parsedBranch);
    }
}
