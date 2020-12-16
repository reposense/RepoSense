package reposense.git;

import java.util.Calendar;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import reposense.template.GitTestTemplate;
import reposense.util.TestUtil;

public class GitRevListTest extends GitTestTemplate {

    @Test
    public void getCommitHashBeforeDate_beforeInitialCommitDate_emptyResult() {
        Date date = TestUtil.getUntilDate(2018, Calendar.FEBRUARY, 4);
        String commitHash = GitRevList.getCommitHashBeforeDate(config.getRepoRoot(), config.getBranch(), date);
        Assert.assertTrue(commitHash.isEmpty());
    }

    @Test
    public void getCommitHashBeforeDate_afterLatestCommitDate_success() {
        Date date = TestUtil.getUntilDate(2018, Calendar.MAY, 10);
        String commitHash = GitRevList.getCommitHashBeforeDate(config.getRepoRoot(), config.getBranch(), date);

        // result from git has a newline at the end
        Assert.assertEquals(EUGENE_AUTHOR_README_FILE_COMMIT_07052018 + "\n", commitHash);
    }

    @Test
    public void getCommitHashBeforeDate_februaryNineDate_success() {
        Date date = TestUtil.getUntilDate(2018, Calendar.FEBRUARY, 8);
        String commitHash = GitRevList.getCommitHashBeforeDate(config.getRepoRoot(), config.getBranch(), date);

        // result from git has a newline at the end
        Assert.assertEquals(FAKE_AUTHOR_BLAME_TEST_FILE_COMMIT_08022018 + "\n", commitHash);
    }

    @Test
    public void getCommitHashBeforeDate_nullDate_emptyResult() {
        String commitHash = GitRevList.getCommitHashBeforeDate(config.getRepoRoot(), config.getBranch(), null);
        Assert.assertTrue(commitHash.isEmpty());
    }

    @Test(expected = RuntimeException.class)
    public void getCommitHashBeforeDate_invalidBranch_throwsRunTimeException() {
        Date date = TestUtil.getUntilDate(2018, Calendar.FEBRUARY, 9);
        GitRevList.getCommitHashBeforeDate(config.getRepoRoot(), "invalidBranch", date);
    }

    @Test
    public void getCommitHashInRange_nullStartAndEndHash_emptyResult() {
        String commitHash = GitRevList.getCommitHashInRange(config.getRepoRoot(), config.getBranch(), null, null);
        Assert.assertTrue(commitHash.isEmpty());
    }

    @Test
    public void getCommitHashInRange_nullStartHash_success() {
        String commitHash = GitRevList.getCommitHashInRange(config.getRepoRoot(), config.getBranch(), null,
                LATEST_COMMIT_HASH);
        Assert.assertEquals(LATEST_COMMIT_HASH, commitHash);
    }

    @Test
    public void getCommitHashInRange_nullEndHash_success() {
        String commitHash = GitRevList.getCommitHashInRange(config.getRepoRoot(), config.getBranch(),
                LATEST_COMMIT_HASH, null);
        Assert.assertEquals(LATEST_COMMIT_HASH, commitHash);
    }

    @Test
    public void getCommitHashInRange_sameStartAndEndHash_success() {
        String commitHash = GitRevList.getCommitHashInRange(config.getRepoRoot(), config.getBranch(),
                LATEST_COMMIT_HASH, LATEST_COMMIT_HASH);
        Assert.assertEquals(LATEST_COMMIT_HASH, commitHash);
    }

    @Test
    public void getCommitHashInRange_invalidStartAndEndHash_emptyResult() {
        String commitHash = GitRevList.getCommitHashInRange(config.getRepoRoot(), config.getBranch(),
                NONEXISTENT_COMMIT_HASH, NONEXISTENT_COMMIT_HASH);
        Assert.assertTrue(commitHash.isEmpty());
    }

    @Test
    public void getCommitHashInRange_invalidStartHash_success() {
        String commitHash = GitRevList.getCommitHashInRange(config.getRepoRoot(), config.getBranch(),
                NONEXISTENT_COMMIT_HASH, LATEST_COMMIT_HASH);
        Assert.assertEquals(LATEST_COMMIT_HASH, commitHash);
    }

    @Test
    public void getCommitHashInRange_invalidEndHash_success() {
        String commitHash = GitRevList.getCommitHashInRange(config.getRepoRoot(), config.getBranch(),
                LATEST_COMMIT_HASH, NONEXISTENT_COMMIT_HASH);
        Assert.assertEquals(LATEST_COMMIT_HASH, commitHash);
    }
}
