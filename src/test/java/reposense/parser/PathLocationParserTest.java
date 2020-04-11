package reposense.parser;

import static org.junit.Assert.assertArrayEquals;

import java.nio.file.Paths;

import org.junit.Test;

public class PathLocationParserTest {

    @Test
    public void pathLocationParser_parsePath_success() throws Exception {
        String pathToGitDirectory = Paths.get("home", "users", "tom", "Desktop",
                "reposense.git").toString();
        assertValidLocation(pathToGitDirectory, pathToGitDirectory, "reposense", null);

        String pathToGitDirectoryWithBranch = Paths.get("home", "users", "tom",
                "Desktop", "reposense.git#feature-1035_branch").toString();
        assertValidLocation(pathToGitDirectoryWithBranch, pathToGitDirectory, "reposense", "feature-1035_branch");

        String pathToRepo = "test-repo_with$special&chars";
        assertValidLocation(pathToRepo, pathToRepo, "test-repo_with$special&chars", null);
    }

    private static void assertValidLocation(String path, String expectedLocation, String expectedRepoName,
            String expectedBranch) {
        String[] actualRepoLocationDetails = PathLocationParser.tryParsingAsPath(path, false);
        assertArrayEquals(new String[] {expectedLocation, expectedRepoName, null, expectedBranch },
                actualRepoLocationDetails);
    }
}
