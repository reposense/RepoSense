package reposense.git;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import reposense.git.exception.CommitNotFoundException;
import reposense.model.RepoConfiguration;
import reposense.template.GitTestTemplate;

public class GitCatFileTest extends GitTestTemplate {
    private RepoConfiguration config;

    @BeforeEach
    public void before() throws Exception {
        super.before();
        config = configs.get();
    }

    @Test
    public void getParentCommits_normalCommit_success() throws Exception {
        List<String> parentsList = GitCatFile.getParentCommits(config.getRepoRoot(), TEST_COMMIT_HASH);
        Assertions.assertEquals(1, parentsList.size());
        Assertions.assertEquals(TEST_COMMIT_HASH_PARENT, parentsList.get(0));
    }

    @Test
    public void getParentCommits_rootCommit_success() throws Exception {
        List<String> parentsList = GitCatFile.getParentCommits(config.getRepoRoot(), ROOT_COMMIT_HASH);
        Assertions.assertEquals(0, parentsList.size());
    }

    @Test
    public void getParentCommits_nonExistentCommit_throwsEmptyCommitException() {
        Assertions.assertThrows(CommitNotFoundException.class, () -> GitCatFile.getParentCommits(config.getRepoRoot(),
                NONEXISTENT_COMMIT_HASH));
    }

    @Test
    public void getParentsOfCommits_singleCommit_success() {
        List<String> parentsList = GitCatFile.getParentsOfCommits(
                config.getRepoRoot(), Arrays.asList(TEST_COMMIT_HASH));
        Assertions.assertEquals(1, parentsList.size());
        Assertions.assertEquals(TEST_COMMIT_HASH_PARENT, parentsList.get(0));
        List<String> emptyParentsList = GitCatFile.getParentsOfCommits(
                config.getRepoRoot(), Arrays.asList(ROOT_COMMIT_HASH));
        Assertions.assertEquals(0, emptyParentsList.size());
    }

    @Test
    public void getParentsOfCommits_multipleCommits_success() {
        List<String> parentsList = GitCatFile.getParentsOfCommits(
                config.getRepoRoot(), Arrays.asList(TEST_COMMIT_HASH, ROOT_COMMIT_HASH, LATEST_COMMIT_HASH));
        Assertions.assertEquals(2, parentsList.size());
        Assertions.assertEquals(TEST_COMMIT_HASH_PARENT, parentsList.get(0));
        Assertions.assertEquals(LATEST_COMMIT_HASH_PARENT, parentsList.get(1));
    }
}
