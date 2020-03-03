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
}
