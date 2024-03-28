package reposense.git;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import reposense.model.RepoConfiguration;
import reposense.template.GitTestTemplate;

public class GitBlameTest extends GitTestTemplate {
    protected static final Pattern IGNORED_AUTHOR_PATTERN = Pattern.compile("(FH-30)");

    private RepoConfiguration config;

    @BeforeEach
    public void before() throws Exception {
        super.before();
        config = configs.get();
    }

    @Test
    public void blameRaw_validFile_success() {
        String content = GitBlame.blame(config.getRepoRoot(), "blameTest.java");
        Assertions.assertFalse(content.isEmpty());
    }

    @Test
    public void blameRaw_nonExistentFile_throwsRunTimeException() {
        Assertions.assertThrows(RuntimeException.class, () -> GitBlame.blame(config.getRepoRoot(), "nonExistentFile"));
    }

    @Test
    public void blameWithPreviousAuthorsRaw_validFile_success() {
        config.setBranch(TEST_REPO_BLAME_WITH_PREVIOUS_AUTHORS_BRANCH);
        GitCheckout.checkoutBranch(config.getRepoRoot(), TEST_REPO_BLAME_WITH_PREVIOUS_AUTHORS_BRANCH);
        createTestIgnoreRevsFile(AUTHOR_TO_IGNORE_BLAME_COMMIT_LIST_07082021);
        String content = GitBlame.blameWithPreviousAuthors(config.getRepoRoot(),
                "blameTest.java");
        removeTestIgnoreRevsFile();

        Matcher ignoredAuthorMatcher = IGNORED_AUTHOR_PATTERN.matcher(content);
        Assertions.assertFalse(ignoredAuthorMatcher.find());
    }

    @Test
    public void blameWithPreviousAuthorsRaw_nonExistentFile_throwsRunTimeException() {
        Assertions.assertThrows(RuntimeException.class, () ->
                GitBlame.blameWithPreviousAuthors(config.getRepoRoot(), "nonExistentFile"));
    }

    @Test
    public void blameLine_allValidArguments_success() {
        String content = GitBlame.blameLine(config.getRepoRoot(), FAKE_AUTHOR_BLAME_TEST_FILE_COMMIT_08022018_STRING,
                "blameTest.java", 1);
        Assertions.assertFalse(content.isEmpty());
    }

    @Test
    public void blameLine_nonExistentCommit_throwsRunTimeException() {
        Assertions.assertThrows(RuntimeException.class, () -> GitBlame.blameLine(config.getRepoRoot(),
                NONEXISTENT_COMMIT_HASH, "blameTest.java", 1));
    }

    @Test
    public void blameLine_nonExistentFile_throwsRunTimeException() {
        Assertions.assertThrows(RuntimeException.class, () -> GitBlame.blameLine(config.getRepoRoot(),
                FAKE_AUTHOR_BLAME_TEST_FILE_COMMIT_08022018_STRING, "nonExistentFile", 1));
    }

    @Test
    public void blameLine_nonExistentLine_throwsRunTimeException() {
        Assertions.assertThrows(RuntimeException.class, () -> GitBlame.blameLine(config.getRepoRoot(),
                FAKE_AUTHOR_BLAME_TEST_FILE_COMMIT_08022018_STRING, "blameTest.java", 5));
    }
}
