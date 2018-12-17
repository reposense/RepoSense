package reposense.system;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import reposense.git.GitDownloader;
import reposense.git.GitDownloaderException;
import reposense.model.RepoConfiguration;
import reposense.parser.ArgsParser;
import reposense.parser.InvalidLocationException;
import reposense.template.GitTestTemplate;
import reposense.util.TestUtil;

public class CommandRunnerTest extends GitTestTemplate {
    protected static final String TEST_REPO_UNCOMMON_DEFAULT_GIT_LOCATION =
            "https://github.com/reposense/testrepo-UncommonDefaultBranch.git";
    protected static RepoConfiguration uncommonDefaultConfig;

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
        String diffResult = CommandRunner.diffCommit(config.getRepoRoot(), LATEST_COMMIT_HASH);
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

    @Test
    public void getCurrentBranch_masterBranch_success() {
        String currentBranch = CommandRunner.getCurrentBranch(config.getRepoRoot());
        Assert.assertEquals("master", currentBranch);
    }

    @Test
    public void getCurrentBranch_uncommonDefaultBranch_success() throws GitDownloaderException,
            InvalidLocationException {
        uncommonDefaultConfig = new RepoConfiguration(TEST_REPO_UNCOMMON_DEFAULT_GIT_LOCATION,
                RepoConfiguration.DEFAULT_BRANCH);
        uncommonDefaultConfig.setFormats(ArgsParser.DEFAULT_FORMATS);

        GitDownloader.downloadRepo(uncommonDefaultConfig);
        String currentBranch = CommandRunner.getCurrentBranch(uncommonDefaultConfig.getRepoRoot());
        Assert.assertEquals("uncommon", currentBranch);
    }
}
