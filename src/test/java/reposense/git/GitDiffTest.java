package reposense.git;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import reposense.model.RepoConfiguration;
import reposense.template.GitTestTemplate;

public class GitDiffTest extends GitTestTemplate {
    private RepoConfiguration config;

    @BeforeEach
    public void before() throws Exception {
        super.before();
        config = configs.get();
    }

    @Test
    public void diffCommit_validCommitHash_success() {
        String diffResult = GitDiff.diffCommit(config.getRepoRoot(),
                FAKE_AUTHOR_BLAME_TEST_FILE_COMMIT_08022018.toString());
        Assertions.assertFalse(diffResult.isEmpty());
    }

    @Test
    public void diffCommit_emptyCommitHash_emptyResult() {
        String diffResult = GitDiff.diffCommit(config.getRepoRoot(), LATEST_COMMIT_HASH);
        Assertions.assertTrue(diffResult.isEmpty());
    }

    @Test
    public void diffCommit_latestCommitHash_emptyResult() {
        String diffResult = GitDiff.diffCommit(config.getRepoRoot(), "");
        Assertions.assertTrue(diffResult.isEmpty());
    }

    @Test
    public void diffCommit_nonexistentCommitHash_throwsRunTimeException() {
        Assertions.assertThrows(RuntimeException.class, () -> GitDiff.diffCommit(config.getRepoRoot(),
                NONEXISTENT_COMMIT_HASH));
    }

    @Test
    public void diffCommit_commitContainingSubmodule_ignoresSubmodule() {
        GitCheckout.checkout(config.getRepoRoot(),
                "850-GitDiffTest-diffCommit_commitContainingSubmodule_ignoresSubmodule");
        String diffResult = GitDiff.diffCommit(config.getRepoRoot(), EMPTY_TREE_HASH);
        Assertions.assertFalse(diffResult.contains("Subproject commit"));
    }

    @Test
    public void diffCommits_validCommitHashes_success() {
        String diffResult = GitDiff.diffCommits(config.getRepoRoot(),
                FAKE_AUTHOR_BLAME_TEST_FILE_COMMIT_08022018_STRING, LATEST_COMMIT_HASH);
        Assertions.assertFalse(diffResult.isEmpty());
    }

    @Test
    public void diffCommits_nonexistentCommitHash_throwsRunTimeException() {
        Assertions.assertThrows(RuntimeException.class, () -> GitDiff.diffCommits(config.getRepoRoot(),
                NONEXISTENT_COMMIT_HASH, LATEST_COMMIT_HASH));
    }
}
