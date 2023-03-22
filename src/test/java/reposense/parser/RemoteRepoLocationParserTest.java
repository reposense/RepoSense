package reposense.parser;

import static reposense.parser.RemoteRepoLocationParser.parseRemoteRepoLocation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class RemoteRepoLocationParserTest {
    @Test
    public void parseRemoteLocation_noDelimiterNotBranchUrl_null() {
        String remoteLocation = "https://github.com/reposense/RepoSense.git";
        Assertions.assertNull(parseRemoteRepoLocation(remoteLocation));
    }

    @Test
    public void parseRemoteLocation_delimiterNotBranchUrl_success() {
        String remoteLocationWithBranch = "https://github.com/reposense/RepoSense.git|master";
        String[] parsedLocationWithBranch = parseRemoteRepoLocation(remoteLocationWithBranch);

        String parsedLocation = parsedLocationWithBranch[0];
        String parsedBranch = parsedLocationWithBranch[1];

        Assertions.assertEquals("https://github.com/reposense/RepoSense.git", parsedLocation);
        Assertions.assertEquals("master", parsedBranch);

        String remoteLocationWithDifferentBranch = "https://github.com/reposense/RepoSense.git|release";
        String[] parsedLocationWithDifferentBranch = parseRemoteRepoLocation(remoteLocationWithDifferentBranch);

        String parsedSameLocation = parsedLocationWithDifferentBranch[0];
        String parsedDifferentBranch = parsedLocationWithDifferentBranch[1];

        Assertions.assertEquals("https://github.com/reposense/RepoSense.git", parsedSameLocation);
        Assertions.assertEquals("release", parsedDifferentBranch);
    }

    @Test
    public void parseRemoteLocation_emptyStringAfterDelimiter_success() {
        String remoteLocationWithBranch = "https://github.com/reposense/RepoSense.git|";
        String[] parsedLocationWithBranch = parseRemoteRepoLocation(remoteLocationWithBranch);

        String parsedLocation = parsedLocationWithBranch[0];
        String parsedBranch = parsedLocationWithBranch[1];

        Assertions.assertEquals("https://github.com/reposense/RepoSense.git", parsedLocation);
        Assertions.assertEquals("", parsedBranch);
    }

    @Test
    public void parseRemoteLocation_githubBranchUrl_success() {
        String githubBranchUrl = "https://github.com/reposense/RepoSense/tree/release";
        String[] parsedLocationWithBranch = parseRemoteRepoLocation(githubBranchUrl);

        String parsedLocation = parsedLocationWithBranch[0];
        String parsedBranch = parsedLocationWithBranch[1];

        Assertions.assertEquals("https://github.com/reposense/RepoSense.git", parsedLocation);
        Assertions.assertEquals("release", parsedBranch);
    }

    @Test
    public void parseRemoteLocation_githubBranchUrl2_success() {
        String githubBranchUrl = "https://github.com/reposense/RepoSense/tree/master";
        String[] parsedLocationWithBranch = parseRemoteRepoLocation(githubBranchUrl);

        String parsedLocation = parsedLocationWithBranch[0];
        String parsedBranch = parsedLocationWithBranch[1];

        Assertions.assertEquals("https://github.com/reposense/RepoSense.git", parsedLocation);
        Assertions.assertEquals("master", parsedBranch);
    }

    @Test
    public void parseRemoteLocation_faultyGithubBranchUrl_null() {
        String githubBranchUrl = "https://github.com/reposense/RepoSense/threeeee/release";
        Assertions.assertNull(parseRemoteRepoLocation(githubBranchUrl));
    }
}
