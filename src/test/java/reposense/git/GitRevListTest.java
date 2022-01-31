package reposense.git;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import reposense.template.GitTestTemplate;
import reposense.util.TestUtil;

public class GitRevListTest extends GitTestTemplate {

    @Test
    public void getCommitHashBeforeDate_beforeInitialCommitDate_emptyResult() {
        LocalDateTime date = TestUtil.getUntilDate(2018, Month.FEBRUARY.getValue(), 4);
        String commitHash = GitRevList.getCommitHashBeforeDate(config.getRepoRoot(), config.getBranch(), date,
                ZoneId.of(config.getZoneId()));
        Assert.assertTrue(commitHash.isEmpty());
    }

    @Test
    public void getCommitHashBeforeDate_afterLatestCommitDate_success() {
        LocalDateTime date = TestUtil.getUntilDate(2018, Month.MAY.getValue(), 10);
        String commitHash = GitRevList.getCommitHashBeforeDate(config.getRepoRoot(), config.getBranch(), date,
                ZoneId.of(config.getZoneId()));

        // result from git has a newline at the end
        Assert.assertEquals(EUGENE_AUTHOR_README_FILE_COMMIT_07052018 + "\n", commitHash);
    }

    @Test
    public void getCommitHashBeforeDate_februaryNineDate_success() {
        LocalDateTime date = TestUtil.getUntilDate(2018, Month.FEBRUARY.getValue(), 8);
        String commitHash = GitRevList.getCommitHashBeforeDate(config.getRepoRoot(), config.getBranch(), date,
                ZoneId.of(config.getZoneId()));

        // result from git has a newline at the end
        Assert.assertEquals(FAKE_AUTHOR_BLAME_TEST_FILE_COMMIT_08022018 + "\n", commitHash);
    }

    @Test
    public void getCommitHashBeforeDate_nullDate_emptyResult() {
        String commitHash = GitRevList.getCommitHashBeforeDate(config.getRepoRoot(), config.getBranch(), null, null);
        Assert.assertTrue(commitHash.isEmpty());
    }

    @Test(expected = RuntimeException.class)
    public void getCommitHashBeforeDate_invalidBranch_throwsRunTimeException() {
        LocalDateTime date = TestUtil.getUntilDate(2018, Month.FEBRUARY.getValue(), 9);
        GitRevList.getCommitHashBeforeDate(config.getRepoRoot(), "invalidBranch", date,
                ZoneId.of(config.getZoneId()));
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

    @Test
    public void getRootCommits_success() {
        List<String> rootCommits = GitRevList.getRootCommits(config.getRepoRoot());
        Assert.assertEquals(1, rootCommits.size());
        Assert.assertEquals(ROOT_COMMIT_HASH, rootCommits.get(0));
    }

    @Test
    public void getIsEmptyRepo_success() {
        boolean isEmpty = GitRevList.checkIsEmptyRepo(config.getRepoRoot());
        Assert.assertEquals(false, isEmpty);
    }
}
