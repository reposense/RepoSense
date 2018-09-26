package reposense.system;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import reposense.model.Author;
import reposense.template.GitTestTemplate;
import reposense.util.TestUtil;

public class CommandRunnerTest extends GitTestTemplate {

    @Test
    public void cloneTest() {
        Path dir = Paths.get(config.getRepoRoot());
        Assert.assertTrue(Files.exists(dir));
    }

    @Test
    public void checkoutTest() {
        CommandRunner.checkout(config.getRepoRoot(), "test");
        Path branchFile = Paths.get(config.getRepoRoot(), "inTestBranch.java");
        Assert.assertTrue(Files.exists(branchFile));
    }

    @Test
    public void gitLog_existingFormats_hasContent() {
        String content = CommandRunner.gitLog(config, getAlphaAllAliasAuthor());
        Assert.assertFalse(content.isEmpty());
    }

    @Test
    public void gitLog_nonExistingFormats_noContent() {
        config.setFormats(Collections.singletonList("py"));
        String content = CommandRunner.gitLog(config, getAlphaAllAliasAuthor());
        Assert.assertTrue(content.isEmpty());
    }

    @Test
    public void gitLog_includeAllJavaFiles_success() {
        config.setFormats(Collections.singletonList("java"));
        String content = CommandRunner.gitLog(config, getAlphaAllAliasAuthor());
        Assert.assertTrue(TestUtil.compareNumberExpectedCommitsToGitLogLines(8, content));
    }

    @Test
    public void gitLog_fakeAuthorNameOnly_success() {
        Author fakeAuthorName = new Author(FAKE_AUTHOR_NAME);

        String content = CommandRunner.gitLog(config, fakeAuthorName);
        Assert.assertTrue(TestUtil.compareNumberExpectedCommitsToGitLogLines(4, content));
    }

    @Test
    public void gitLog_fakeAuthorNameWithSpecialCharacter_success() {
        Author fakeAuthorWithSpecialCharacter = new Author(FAKE_AUTHOR_NAME.replace("fake", "#()!"));

        String content = CommandRunner.gitLog(config, fakeAuthorWithSpecialCharacter);
        Assert.assertTrue(TestUtil.compareNumberExpectedCommitsToGitLogLines(4, content));
    }

    @Test
    public void gitLog_includeAllJavaFilesAuthorIgnoreMovedFile_success() {
        config.setFormats(Collections.singletonList("java"));
        Author ignoreMovedFileAuthor = getAlphaAllAliasAuthor();
        ignoreMovedFileAuthor.setIgnoreGlobList(Collections.singletonList("**movedFile.java"));

        String content = CommandRunner.gitLog(config, ignoreMovedFileAuthor);
        Assert.assertTrue(TestUtil.compareNumberExpectedCommitsToGitLogLines(6, content));
    }

    @Test
    public void gitLog_authorIgnoreAllJavaFiles_success() {
        Author ignoreAllJavaFilesAuthor = getAlphaAllAliasAuthor();
        ignoreAllJavaFilesAuthor.setIgnoreGlobList(Collections.singletonList("*.java"));

        String content = CommandRunner.gitLog(config, ignoreAllJavaFilesAuthor);
        Assert.assertTrue(TestUtil.compareNumberExpectedCommitsToGitLogLines(2, content));
    }

    @Test
    public void gitLog_authorWithAllCharactersRegexAlias_emptyResult() {
        Author authorWithAllCharactersRegexAlias = new Author("none");
        authorWithAllCharactersRegexAlias.setAuthorAliases(Collections.singletonList(".*"));

        String content = CommandRunner.gitLog(config, authorWithAllCharactersRegexAlias);
        Assert.assertTrue(content.isEmpty());
    }

    @Test
    public void gitLog_sinceDateInFuture_noContent() {
        Date date = TestUtil.getDate(2050, Calendar.JANUARY, 1);
        config.setSinceDate(date);
        String content = CommandRunner.gitLog(config, getAlphaAllAliasAuthor());
        Assert.assertTrue(content.isEmpty());

        date = TestUtil.getDate(1950, Calendar.JANUARY, 1);
        config.setUntilDate(date);
        config.setSinceDate(null);
        content = CommandRunner.gitLog(config, getAlphaAllAliasAuthor());
        Assert.assertTrue(content.isEmpty());
    }

    @Test
    public void blameRaw_validFile_success() {
        String content = CommandRunner.blameRaw(config.getRepoRoot(), "blameTest.java");
        Assert.assertFalse(content.isEmpty());
    }

    @Test(expected = RuntimeException.class)
    public void blameRaw_nonExistentFile_throwsRunTimeException() {
        CommandRunner.blameRaw(config.getRepoRoot(), "nonExistentFile");
    }

    @Test
    public void diffCommit_validCommitHash_success() {
        String diffResult = CommandRunner.diffCommit(config.getRepoRoot(), FAKE_AUTHOR_BLAME_TEST_FILE_COMMIT_08022018);
        Assert.assertFalse(diffResult.isEmpty());
    }

    @Test
    public void diffCommit_emptyCommitHash_emptyResult() {
        String diffResult = CommandRunner.diffCommit(config.getRepoRoot(), EUGENE_AUTHOR_README_FILE_COMMIT_07052018);
        Assert.assertTrue(diffResult.isEmpty());
    }

    @Test
    public void diffCommit_latestCommitHash_emptyResult() {
        String diffResult = CommandRunner.diffCommit(config.getRepoRoot(), "");
        Assert.assertTrue(diffResult.isEmpty());
    }

    @Test(expected = RuntimeException.class)
    public void diffCommit_nonexistentCommitHash_throwsRunTimeException() {
        CommandRunner.diffCommit(config.getRepoRoot(), NONEXISTENT_COMMIT_HASH);
    }

    @Test
    public void getCommitHashBeforeDate_beforeInitialCommitDate_emptyResult() {
        Date date = TestUtil.getDate(2018, Calendar.FEBRUARY, 4);
        String commitHash = CommandRunner.getCommitHashBeforeDate(config.getRepoRoot(), config.getBranch(), date);
        Assert.assertTrue(commitHash.isEmpty());
    }

    @Test
    public void getCommitHashBeforeDate_afterLatestCommitDate_success() {
        Date date = TestUtil.getDate(2018, Calendar.MAY, 10);
        String commitHash = CommandRunner.getCommitHashBeforeDate(config.getRepoRoot(), config.getBranch(), date);

        // result from git has a newline at the end
        Assert.assertEquals(EUGENE_AUTHOR_README_FILE_COMMIT_07052018 + "\n", commitHash);
    }

    @Test
    public void getCommitHashBeforeDate_februaryNineDate_success() {
        Date date = TestUtil.getDate(2018, Calendar.FEBRUARY, 9);
        String commitHash = CommandRunner.getCommitHashBeforeDate(config.getRepoRoot(), config.getBranch(), date);

        // result from git has a newline at the end
        Assert.assertEquals(FAKE_AUTHOR_BLAME_TEST_FILE_COMMIT_08022018 + "\n", commitHash);
    }

    @Test
    public void getCommitHashBeforeDate_nullDate_emptyResult() {
        String commitHash = CommandRunner.getCommitHashBeforeDate(config.getRepoRoot(), config.getBranch(), null);
        Assert.assertTrue(commitHash.isEmpty());
    }

    @Test(expected = RuntimeException.class)
    public void getCommitHashBeforeDate_invalidBranch_throwsRunTimeException() {
        Date date = TestUtil.getDate(2018, Calendar.FEBRUARY, 9);
        CommandRunner.getCommitHashBeforeDate(config.getRepoRoot(), "invalidBranch", date);
    }
}
