package reposense.git;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import reposense.git.exception.CommitNotFoundException;
import reposense.template.GitTestTemplate;

public class GitShowTest extends GitTestTemplate {

    @Test
    public void getCommitDate_normalCommit_success() throws Exception {
        Date commitDate = GitShow.getCommitDate(config.getRepoRoot(), TEST_COMMIT_HASH);
        Date expectedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z").parse("2018-02-09 22:17:39 +0800");
        Assert.assertEquals(expectedDate, commitDate);
    }

    @Test(expected = CommitNotFoundException.class)
    public void getCommitDate_nonexistentCommit_throwsEmptyCommitException() throws Exception {
        GitShow.getCommitDate(config.getRepoRoot(), NONEXISTENT_COMMIT_HASH);
    }

    @Test
    public void getEarliestCommitDate_singleCommit_success() throws Exception {
        Date earliestDate = GitShow.getEarliestCommitDate(
                config.getRepoRoot(), Arrays.asList(TEST_COMMIT_HASH));
        Date expectedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z").parse("2018-02-09 22:17:39 +0800");
        Assert.assertEquals(expectedDate, earliestDate);
    }

    @Test
    public void getEarliestCommitDate_multipleCommits_success() throws Exception {
        Date earliestDate = GitShow.getEarliestCommitDate(
                config.getRepoRoot(), Arrays.asList(TEST_COMMIT_HASH, ROOT_COMMIT_HASH, LATEST_COMMIT_HASH));
        Date expectedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z").parse("2018-02-05 16:00:39 +0800");
        Assert.assertEquals(expectedDate, earliestDate);
    }

    @Test(expected = CommitNotFoundException.class)
    public void getEarliestCommitDate_nonexistentCommit_throwsEmptyCommitException() throws Exception {
        GitShow.getEarliestCommitDate(config.getRepoRoot(),
                Arrays.asList(NONEXISTENT_COMMIT_HASH, NONEXISTENT_COMMIT_HASH, NONEXISTENT_COMMIT_HASH));
    }
}
