package reposense.parser;

import static reposense.parser.LocalRepoLocationParser.parseLocalRepoLocation;
import static reposense.util.TestUtil.isWindows;

import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;

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
    public void parseLocalRepoLocation_pathContainsDelimiter_null() throws Exception {
        if (isWindows()) {
            // Windows do not allow pipe as part of directory name
            return;
        }
        // Path contains delimiter but no intended use of delimiter
        try {
            Files.createDirectory(Paths.get("delimiter|in|directory"));
        } catch (FileAlreadyExistsException e) {
            // should ignore exception
        }
        String localLocation = Paths.get("delimiter|in|directory").toString();

        String[] parsedLocationWithBranch = parseLocalRepoLocation(localLocation);
        Assertions.assertNull(parsedLocationWithBranch);

        Files.deleteIfExists(Paths.get("delimiter|in|directory"));
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
