package reposense.git;

import static reposense.git.GitVersion.getVersionNumberAndReleaseNumberFromString;
import static reposense.git.GitVersion.isGitVersionOutputAtLeastVersion;

import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;

import reposense.template.GitTestTemplate;

public class GitVersionTest extends GitTestTemplate {
    protected static final Pattern VALID_GIT_VERSION_PATTERN = Pattern.compile("git.* (\\d+.\\d+.\\d+).*");

    @Test
    public void gitVersionRaw_validGitVersion_success() {
        boolean isValidGitVersion = VALID_GIT_VERSION_PATTERN.matcher(GitVersion.getGitVersion()).find();
        Assert.assertTrue(isValidGitVersion);
    }

    @Test
    public void getVersionNumberAndReleaseNumberFromString_validCommandOutput_success() {
        String[] expectedVersionAndReleaseNumbers1 = new String[] {"1", "0"};
        String[] expectedVersionAndReleaseNumbers2 = new String[] {"2", "22"};
        Assert.assertArrayEquals(expectedVersionAndReleaseNumbers1,
                getVersionNumberAndReleaseNumberFromString("git version 1.0.0"));
        Assert.assertArrayEquals(expectedVersionAndReleaseNumbers2,
                getVersionNumberAndReleaseNumberFromString("git version 2.22.5.windows.1"));
    }

    @Test
    public void isGitVersionOutputAtLeastVersion_smallerThanVersions_returnsFalse() {
        Assert.assertFalse(isGitVersionOutputAtLeastVersion("git version 1.0.0", "2.23.0"));
        Assert.assertFalse(isGitVersionOutputAtLeastVersion("git version 2.17.0\n", "2.23"));
        Assert.assertFalse(isGitVersionOutputAtLeastVersion("git version 2.17.0.windows.1\n", "2.23.5"));
        Assert.assertFalse(isGitVersionOutputAtLeastVersion("git version 1.7.1", "2.0"));
    }

    @Test
    public void isGitVersionOutputAtLeastVersion_greaterThanVersions_returnsTrue() {
        Assert.assertTrue(isGitVersionOutputAtLeastVersion("git version 3.0.0", "2.23.0"));
        Assert.assertTrue(isGitVersionOutputAtLeastVersion("git version 2.35.0\n", "2.23"));
        Assert.assertTrue(isGitVersionOutputAtLeastVersion("git version 2.35.1.windows.2\n", "2.23.5"));
        Assert.assertTrue(isGitVersionOutputAtLeastVersion("git version 2.23.1", "2.23.1"));
    }
}
