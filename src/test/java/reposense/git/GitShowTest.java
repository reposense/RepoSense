package reposense.git;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import reposense.git.exception.CommitNotFoundException;
import reposense.template.GitTestTemplate;

public class GitShowTest extends GitTestTemplate {

    @Test
    public void getExpandedCommitHash_shortCommitHash_success() throws Exception {
        String expandedCommitHash = GitShow.getExpandedCommitHash(config.getRepoRoot(), TEST_COMMIT_HASH).toString();
        Assert.assertEquals(expandedCommitHash, TEST_COMMIT_HASH_LONG);
    }

    @Test(expected = CommitNotFoundException.class)
    public void getExpandedCommitHash_nonExistentCommit_throwsEmptyCommitException() throws Exception {
        GitShow.getExpandedCommitHash(config.getRepoRoot(), NONEXISTENT_COMMIT_HASH);
    }

    @Test
    public void getCommitDate_normalCommit_success() throws Exception {
        LocalDateTime commitDate = GitShow.getCommitDate(config.getRepoRoot(), TEST_COMMIT_HASH);
        LocalDateTime expectedDate = LocalDateTime.parse("2018-02-09 22:17:39 +0800",
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss Z"));
        Assert.assertEquals(expectedDate, commitDate);
    }

    @Test(expected = CommitNotFoundException.class)
    public void getParentCommits_nonExistentCommit_throwsEmptyCommitException() throws Exception {
        GitShow.getCommitDate(config.getRepoRoot(), NONEXISTENT_COMMIT_HASH);
    }

    @Test
    public void getEarliestCommitDate_singleCommit_success() throws Exception {
        LocalDateTime earliestDate = GitShow.getEarliestCommitDate(
                config.getRepoRoot(), Arrays.asList(TEST_COMMIT_HASH));
        LocalDateTime expectedDate = LocalDateTime.parse("2018-02-09 22:17:39 +0800",
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss Z"));
        Assert.assertEquals(expectedDate, earliestDate);
    }

    @Test
    public void getEarliestCommitDate_multipleCommits_success() throws Exception {
        LocalDateTime earliestDate = GitShow.getEarliestCommitDate(
                config.getRepoRoot(), Arrays.asList(TEST_COMMIT_HASH, ROOT_COMMIT_HASH, LATEST_COMMIT_HASH));
        LocalDateTime expectedDate = LocalDateTime.parse("2018-02-05 16:00:39 +0800",
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss Z"));
        Assert.assertEquals(expectedDate, earliestDate);
    }

    @Test(expected = CommitNotFoundException.class)
    public void getEarliestCommitDate_nonexistentCommit_throwsEmptyCommitException() throws Exception {
        GitShow.getEarliestCommitDate(config.getRepoRoot(),
                Arrays.asList(NONEXISTENT_COMMIT_HASH, NONEXISTENT_COMMIT_HASH, NONEXISTENT_COMMIT_HASH));
    }
}
