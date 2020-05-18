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

        String pathToRepo = "test-repo#with#multiple#hash#symbols#hotfix_branch";
        assertValidLocation(pathToRepo, "test-repo#with#multiple#hash#symbols",
                "test-repo#with#multiple#hash#symbols", "hotfix_branch");
    }

    private static void assertValidLocation(String path, String expectedLocation, String expectedRepoName,
            String expectedBranch) {
        String[] actualRepoLocationDetails = PathLocationParser.parseAsPath(path, false);
        assertArrayEquals(new String[] {expectedLocation, expectedRepoName, null, expectedBranch },
                actualRepoLocationDetails);
    }
}
