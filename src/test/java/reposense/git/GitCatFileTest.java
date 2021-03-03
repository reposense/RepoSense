package reposense.git;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import reposense.git.exception.CommitNotFoundException;
import reposense.template.GitTestTemplate;

public class GitCatFileTest extends GitTestTemplate {

    @Test
    public void getParentCommits_normalCommit_success() throws Exception {
        List<String> parentsList = GitCatFile.getParentCommits(config.getRepoRoot(), TEST_COMMIT_HASH);
        Assert.assertEquals(1, parentsList.size());
        Assert.assertEquals(TEST_COMMIT_HASH_PARENT, parentsList.get(0));
    }

    @Test
    public void getParentCommits_rootCommit_success() throws Exception {
        List<String> parentsList = GitCatFile.getParentCommits(config.getRepoRoot(), ROOT_COMMIT_HASH);
        Assert.assertEquals(0, parentsList.size());
    }

    @Test(expected = CommitNotFoundException.class)
    public void getParentCommits_nonexistentCommit_throwsEmptyCommitException() throws Exception {
        GitCatFile.getParentCommits(config.getRepoRoot(), NONEXISTENT_COMMIT_HASH);
    }

    @Test
    public void getParentsOfCommits_singleCommit_success() throws Exception {
        List<String> parentsList = GitCatFile.getParentsOfCommits(
                config.getRepoRoot(), Arrays.asList(TEST_COMMIT_HASH));
        Assert.assertEquals(1, parentsList.size());
        Assert.assertEquals(TEST_COMMIT_HASH_PARENT, parentsList.get(0));
        List<String> emptyParentsList = GitCatFile.getParentsOfCommits(
                config.getRepoRoot(), Arrays.asList(ROOT_COMMIT_HASH));
        Assert.assertEquals(0, emptyParentsList.size());
    }

    @Test
    public void getParentsOfCommits_multipleCommits_success() throws Exception {
        List<String> parentsList = GitCatFile.getParentsOfCommits(
                config.getRepoRoot(), Arrays.asList(TEST_COMMIT_HASH, ROOT_COMMIT_HASH, LATEST_COMMIT_HASH));
        Assert.assertEquals(2, parentsList.size());
        Assert.assertEquals(TEST_COMMIT_HASH_PARENT, parentsList.get(0));
        Assert.assertEquals(LATEST_COMMIT_HASH_PARENT, parentsList.get(1));
    }
}
