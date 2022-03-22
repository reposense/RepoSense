package reposense.git;

import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;

import reposense.template.GitTestTemplate;

import static reposense.git.GitVersion.isGitVersionOutputAtLeastVersion;

public class GitVersionTest extends GitTestTemplate {
    protected static final Pattern VALID_GIT_VERSION_PATTERN = Pattern.compile("git.* (\\d+.\\d+.\\d+).*");

    @Test
    public void gitVersionRaw_validGitVersion_success() {
        boolean isValidGitVersion = VALID_GIT_VERSION_PATTERN.matcher(GitVersion.getGitVersion()).find();
        Assert.assertTrue(isValidGitVersion);
    }

    @Test
    public void isGitVersionOutputAtLeastVersion_smallerThanVersions_returnsFalse() {
        Assert.assertFalse(isGitVersionOutputAtLeastVersion("git version 1.0.0", "2.23.0"));
        Assert.assertFalse(isGitVersionOutputAtLeastVersion("git version 2.22.5", "2.23"));
        Assert.assertFalse(isGitVersionOutputAtLeastVersion("git version 2.22.5.windows.1", "2.23.5"));
        Assert.assertFalse(isGitVersionOutputAtLeastVersion("git version 1.7.1", "2.0"));
    }

    @Test
    public void isGitVersionOutputAtLeastVersion_greaterThanVersions_returnsTrue() {
        Assert.assertFalse(isGitVersionOutputAtLeastVersion("git version 3.0.0", "2.23.0"));
        Assert.assertFalse(isGitVersionOutputAtLeastVersion("git version 2.35", "2.23"));
        Assert.assertFalse(isGitVersionOutputAtLeastVersion("git version 2.35.1.windows.2", "2.23.5"));
        Assert.assertFalse(isGitVersionOutputAtLeastVersion("git version 2.23.1", "2.23.1"));
    }
}
