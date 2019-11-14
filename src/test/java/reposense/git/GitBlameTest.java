package reposense.git;

import org.junit.Assert;
import org.junit.Test;

import reposense.template.GitTestTemplate;

public class GitBlameTest extends GitTestTemplate {

    @Test
    public void blameRaw_validFile_success() {
        String content = GitBlame.blame(config.getRepoRoot(), "blameTest.java");
        Assert.assertFalse(content.isEmpty());
    }

    @Test(expected = RuntimeException.class)
    public void blameRaw_nonExistentFile_throwsRunTimeException() {
        GitBlame.blame(config.getRepoRoot(), "nonExistentFile");
    }

    @Test
    public void blameLine_allValidArguments_success() {
        String content = GitBlame.blameLine(config.getRepoRoot(), FAKE_AUTHOR_BLAME_TEST_FILE_COMMIT_08022018_STRING,
                "blameTest.java", 1);
        Assert.assertFalse(content.isEmpty());
    }

    @Test(expected = RuntimeException.class)
    public void blameLine_nonExistentCommit_throwsRunTimeException() {
        GitBlame.blameLine(config.getRepoRoot(), NONEXISTENT_COMMIT_HASH, "blameTest.java", 1);
    }

    @Test(expected = RuntimeException.class)
    public void blameLine_nonExistentFile_throwsRunTimeException() {
        GitBlame.blameLine(config.getRepoRoot(), FAKE_AUTHOR_BLAME_TEST_FILE_COMMIT_08022018_STRING,
                "nonExistentFile", 1);
    }

    @Test(expected = RuntimeException.class)
    public void blameLine_nonExistentLine_throwsRunTimeException() {
        GitBlame.blameLine(config.getRepoRoot(), FAKE_AUTHOR_BLAME_TEST_FILE_COMMIT_08022018_STRING,
                "blameTest.java", 5);
    }
}
