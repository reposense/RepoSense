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
    private static final String LATEST_COMMIT_HASH = "2d87a431fcbb8f73a731b6df0fcbee962c85c250";
    private static final String FEBRUARY_EIGHT_COMMIT_HASH = "768015345e70f06add2a8b7d1f901dc07bf70582";

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
        String content = CommandRunner.gitLog(config, getNoFilterAuthor());
        Assert.assertFalse(content.isEmpty());
    }

    @Test
    public void gitLog_nonExistingFormats_noContent() {
        config.setFormats(Collections.singletonList("py"));
        String content = CommandRunner.gitLog(config, getNoFilterAuthor());
        Assert.assertTrue(content.isEmpty());
    }

    @Test
    public void gitLog_includeAllJavaFiles_success() {
        config.setFormats(Collections.singletonList("java"));
        String content = CommandRunner.gitLog(config, getNoFilterAuthor());
        String[] contentLines = content.split("\n");
        int expectedNumberCommits = 8;
        Assert.assertEquals(convertNumberExpectedCommitsToGitLogLines(expectedNumberCommits), contentLines.length);
    }

    @Test
    public void gitLog_includeAllJavaFilesIgnoreMovedFile_success() {
        config.setFormats(Collections.singletonList("java"));
        Author ignoreMovedFileAuthor = getNoFilterAuthor();
        ignoreMovedFileAuthor.setIgnoreGlobList(Collections.singletonList("**movedFile.java"));

        String content = CommandRunner.gitLog(config, ignoreMovedFileAuthor);
        String[] contentLines = content.split("\n");
        int expectedNumberCommits = 6;
        Assert.assertEquals(convertNumberExpectedCommitsToGitLogLines(expectedNumberCommits), contentLines.length);
    }

    @Test
    public void gitLog_ignoreAllJavaFiles_success() {
        Author ignoreAllJavaFilesAuthor = getNoFilterAuthor();
        ignoreAllJavaFilesAuthor.setIgnoreGlobList(Collections.singletonList("*.java"));

        String content = CommandRunner.gitLog(config, ignoreAllJavaFilesAuthor);
        String[] contentLines = content.split("\n");
        int expectedNumberCommits = 2;
        Assert.assertEquals(convertNumberExpectedCommitsToGitLogLines(expectedNumberCommits), contentLines.length);
    }

    @Test
    public void gitLog_sinceDateInFuture_noContent() {
        Date date = TestUtil.getDate(2050, Calendar.JANUARY, 1);
        config.setSinceDate(date);
        String content = CommandRunner.gitLog(config, getNoFilterAuthor());
        Assert.assertTrue(content.isEmpty());

        date = TestUtil.getDate(1950, Calendar.JANUARY, 1);
        config.setUntilDate(date);
        config.setSinceDate(null);
        content = CommandRunner.gitLog(config, getNoFilterAuthor());
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
        String diffResult = CommandRunner.diffCommit(config.getRepoRoot(), FEBRUARY_EIGHT_COMMIT_HASH);
        Assert.assertFalse(diffResult.isEmpty());
    }

    @Test
    public void diffCommit_emptyCommitHash_emptyResult() {
        String diffResult = CommandRunner.diffCommit(config.getRepoRoot(), LATEST_COMMIT_HASH);
        Assert.assertTrue(diffResult.isEmpty());
    }

    @Test
    public void diffCommit_latestCommitHash_emptyResult() {
        String diffResult = CommandRunner.diffCommit(config.getRepoRoot(), "");
        Assert.assertTrue(diffResult.isEmpty());
    }

    @Test(expected = RuntimeException.class)
    public void diffCommit_invalidCommitHash_throwsRunTimeException() {
        CommandRunner.diffCommit(config.getRepoRoot(), "invalidBranch");
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
        Assert.assertEquals(LATEST_COMMIT_HASH + "\n", commitHash);
    }

    @Test
    public void getCommitHashBeforeDate_februaryNineDate_success() {
        Date date = TestUtil.getDate(2018, Calendar.FEBRUARY, 9);
        String commitHash = CommandRunner.getCommitHashBeforeDate(config.getRepoRoot(), config.getBranch(), date);

        // result from git has a newline at the end
        Assert.assertEquals(FEBRUARY_EIGHT_COMMIT_HASH + "\n", commitHash);
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

    /**
     * Converts the {@code expectedNumberCommits} to the number of lines will be produced by the git log command.
     */
    private int convertNumberExpectedCommitsToGitLogLines(int expectedNumberCommits) {
        // each commit has 2 lines of info, and a blank line in between each
        return expectedNumberCommits * 3 - 1;
    }

    /**
     * Returns a {@code Author} that will not filter out any commits in git log command.
     */
    private Author getNoFilterAuthor() {
        return new Author(".*");
    }
}
