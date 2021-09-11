package reposense.git;

import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;

import reposense.template.GitTestTemplate;

public class GitVersionTest extends GitTestTemplate {
    protected static final Pattern VALID_GIT_VERSION_PATTERN = Pattern.compile("git.* (\\d+.\\d+.\\d+).*");
    protected static final String FINDING_PREVIOUS_AUTHORS_VALID_GIT_VERSION = "2.23.0";
    protected static final String FINDING_PREVIOUS_AUTHORS_INVALID_GIT_VERSION = "2.22.0";

    @Test
    public void gitVersionRaw_validGitVersion_success() {
        boolean isValidGitVersion = VALID_GIT_VERSION_PATTERN.matcher(GitVersion.getGitVersion()).find();
        Assert.assertTrue(isValidGitVersion);
    }

    @Test
    public void findingPreviousAuthorsValidGitVersionRegex_validGitVersion_success() {
        boolean isAbleToRunFindingPreviousAuthors = GitVersion.FINDING_PREVIOUS_AUTHORS_VALID_GIT_VERSION_PATTERN
                .matcher(FINDING_PREVIOUS_AUTHORS_VALID_GIT_VERSION).find();
        Assert.assertTrue(isAbleToRunFindingPreviousAuthors);
    }

    @Test
    public void findingPreviousAuthorsValidGitVersionRegex_invalidGitVersion_failure() {
        boolean isAbleToRunFindingPreviousAuthors = GitVersion.FINDING_PREVIOUS_AUTHORS_VALID_GIT_VERSION_PATTERN
                .matcher(FINDING_PREVIOUS_AUTHORS_INVALID_GIT_VERSION).find();
        Assert.assertFalse(isAbleToRunFindingPreviousAuthors);
    }

    @Test
    public void gitVersionValidForFindingPreviousAuthorsMethod_sameResultAsRegex_success() {
        boolean isAbleToRunFindingPreviousAuthors = GitVersion.FINDING_PREVIOUS_AUTHORS_VALID_GIT_VERSION_PATTERN
                .matcher(GitVersion.getGitVersion()).find();
        Assert.assertEquals(isAbleToRunFindingPreviousAuthors,
                GitVersion.isGitVersionSufficientForFindingPreviousAuthors());
    }
}
