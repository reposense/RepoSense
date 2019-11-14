package reposense.git;

import org.junit.Assert;
import org.junit.Test;

import reposense.template.GitTestTemplate;


public class GitDiffTest extends GitTestTemplate {

    @Test
    public void diffCommit_validCommitHash_success() {
        String diffResult = GitDiff.diffCommit(config.getRepoRoot(),
                FAKE_AUTHOR_BLAME_TEST_FILE_COMMIT_08022018.toString());
        Assert.assertFalse(diffResult.isEmpty());
    }

    @Test
    public void diffCommit_emptyCommitHash_emptyResult() {
        String diffResult = GitDiff.diffCommit(config.getRepoRoot(), LATEST_COMMIT_HASH);
        Assert.assertTrue(diffResult.isEmpty());
    }

    @Test
    public void diffCommit_latestCommitHash_emptyResult() {
        String diffResult = GitDiff.diffCommit(config.getRepoRoot(), "");
        Assert.assertTrue(diffResult.isEmpty());
    }

    @Test(expected = RuntimeException.class)
    public void diffCommit_nonexistentCommitHash_throwsRunTimeException() {
        GitDiff.diffCommit(config.getRepoRoot(), NONEXISTENT_COMMIT_HASH);
    }

    @Test
    public void diffCommits_validCommitHashes_success() {
        String diffResult = GitDiff.diffCommits(config.getRepoRoot(),
                FAKE_AUTHOR_BLAME_TEST_FILE_COMMIT_08022018_STRING, LATEST_COMMIT_HASH);
        Assert.assertFalse(diffResult.isEmpty());
    }

    @Test(expected = RuntimeException.class)
    public void diffCommits_nonexistentCommitHash_throwsRunTimeException() {
        GitDiff.diffCommits(config.getRepoRoot(), NONEXISTENT_COMMIT_HASH, LATEST_COMMIT_HASH);
    }
}
