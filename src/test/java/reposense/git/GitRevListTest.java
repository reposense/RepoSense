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
}
