package reposense.git;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;

import reposense.template.GitTestTemplate;

public class GitBlameTest extends GitTestTemplate {

    protected static final Pattern IGNORED_AUTHOR_PATTERN = Pattern.compile("(FH-30)");

    @Test
    public void blameRaw_validFile_success() {
        String content = GitBlame.blame(config.getRepoRoot(), "blameTest.java");
        Assert.assertFalse(content.isEmpty());
    }

    @Test(expected = RuntimeException.class)
    public void blameRaw_nonExistentFile_throwsRunTimeException() {
        GitBlame.blame(config.getRepoRoot(), "nonExistentFile");
    }

    @Test
    public void blameWithPreviousAuthorsRaw_validFile_success() {
        config.setBranch(TEST_REPO_BLAME_WITH_PREVIOUS_AUTHORS_BRANCH);
        GitCheckout.checkout(config.getRepoRoot(), TEST_REPO_BLAME_WITH_PREVIOUS_AUTHORS_BRANCH);
        createTestIgnoreRevsFile(AUTHOR_TO_IGNORE_BLAME_COMMIT_LIST_07082021);
        String content = GitBlame.blameWithPreviousAuthors(config.getRepoRoot(),
                "blameTest.java");
        removeTestIgnoreRevsFile();

        Matcher ignoredAuthorMatcher = IGNORED_AUTHOR_PATTERN.matcher(content);
        Assert.assertFalse(ignoredAuthorMatcher.find());
    }

    @Test(expected = RuntimeException.class)
    public void blameWithPreviousAuthorsRaw_nonExistentFile_throwsRunTimeException() {
        GitBlame.blameWithPreviousAuthors(config.getRepoRoot(), "nonExistentFile");
    }
}
