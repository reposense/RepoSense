package reposense.model;

import org.junit.Assert;
import org.junit.Test;

import java.util.regex.Matcher;

public class RepoLocationTest {

    @Test
    public void testLocationRegex() {
        Matcher matcher = RepoLocation.GIT_REPOSITORY_LOCATION_PATTERN.matcher(
                "https://github.com/reposense/testrepo-Alpha.git");
        Assert.assertTrue(matcher.find());
        Assert.assertEquals(matcher.group("org"), "reposense");
        Assert.assertEquals(matcher.group("repoName"), "testrepo-Alpha");

        matcher = RepoLocation.GIT_REPOSITORY_LOCATION_PATTERN.matcher(
                "https://github.com/reposense/testrepo-Alpha.git/");
        Assert.assertTrue(matcher.find());
        Assert.assertEquals(matcher.group("org"), "reposense");
        Assert.assertEquals(matcher.group("repoName"), "testrepo-Alpha");
        Assert.assertEquals(matcher.group("branch"), "");

        matcher = RepoLocation.GIT_REPOSITORY_LOCATION_PATTERN.matcher(
                "https://github.com/reposense/testrepo-Alpha.git/release");
        Assert.assertTrue(matcher.find());
        Assert.assertEquals(matcher.group("org"), "reposense");
        Assert.assertEquals(matcher.group("repoName"), "testrepo-Alpha");
        Assert.assertEquals(matcher.group("branch"), "release");
    }
}
