package reposense.git;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;

import reposense.model.Author;
import reposense.system.CommandRunner;
import reposense.template.GitTestTemplate;
import reposense.util.TestUtil;

public class GitCommitEditerTest extends GitTestTemplate {
    private static final int NUMBER_FAKE_AUTHOR_COMMIT = 4;
    private static final int NUMBER_EUGENE_AUTHOR_COMMIT = 1;

    @Test
    public void removeAuthorFromIgnoredCommits_noCommitHash_success() {
        GitCommitEditer.removeAuthorFromIgnoredCommits(config);

        String content = CommandRunner.gitLog(config, new Author(FAKE_AUTHOR_NAME));
        Assert.assertTrue(TestUtil.compareNumberExpectedCommitsToGitLogLines(NUMBER_FAKE_AUTHOR_COMMIT, content));

        content = CommandRunner.gitLog(config, new Author(EUGENE_AUTHOR_NAME));
        Assert.assertTrue(TestUtil.compareNumberExpectedCommitsToGitLogLines(NUMBER_EUGENE_AUTHOR_COMMIT, content));
    }

    @Test
    public void removeAuthorFromIgnoredCommits_singleValidCommitHash_success() {
        config.setIgnoreCommitList(Collections.singletonList(FAKE_AUTHOR_COMMIT_HASH_08022018));
        GitCommitEditer.removeAuthorFromIgnoredCommits(config);

        String content = CommandRunner.gitLog(config, new Author(FAKE_AUTHOR_NAME));
        Assert.assertTrue(TestUtil.compareNumberExpectedCommitsToGitLogLines(NUMBER_FAKE_AUTHOR_COMMIT - 1, content));
    }

    @Test
    public void removeAuthorFromIgnoredCommits_multipleValidCommitHash_success() {
        config.setIgnoreCommitList(
                Arrays.asList(EUGENE_AUTHOR_COMMIT_HASH_07052018, FAKE_AUTHOR_COMMIT_HASH_08022018));
        GitCommitEditer.removeAuthorFromIgnoredCommits(config);

        String content = CommandRunner.gitLog(config, new Author(FAKE_AUTHOR_NAME));
        Assert.assertTrue(TestUtil.compareNumberExpectedCommitsToGitLogLines(NUMBER_FAKE_AUTHOR_COMMIT - 1, content));

        content = CommandRunner.gitLog(config, new Author(EUGENE_AUTHOR_NAME));
        Assert.assertTrue(TestUtil.compareNumberExpectedCommitsToGitLogLines(NUMBER_EUGENE_AUTHOR_COMMIT - 1, content));
    }

    @Test
    public void removeAuthorFromIgnoredCommits_commitHashInvalidOrder_incorrectResult() {
        // set earlier commit before latest commit
        config.setIgnoreCommitList(
                Arrays.asList(FAKE_AUTHOR_COMMIT_HASH_08022018, EUGENE_AUTHOR_COMMIT_HASH_07052018));
        GitCommitEditer.removeAuthorFromIgnoredCommits(config);

        String content = CommandRunner.gitLog(config, new Author(FAKE_AUTHOR_NAME));
        Assert.assertFalse(TestUtil.compareNumberExpectedCommitsToGitLogLines(NUMBER_FAKE_AUTHOR_COMMIT - 1, content));
    }

    @Test(expected = RuntimeException.class)
    public void removeAuthorFromIgnoredCommit_nonexistentCommitHash_throwsRunTimeException() {
        config.setIgnoreCommitList(Collections.singletonList(NONEXISTENT_COMMIT_HASH));
        GitCommitEditer.removeAuthorFromIgnoredCommits(config);
    }
}
