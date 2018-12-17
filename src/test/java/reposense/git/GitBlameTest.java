package reposense.git;

import static reposense.git.GitBlame.blame;

import org.junit.Assert;
import org.junit.Test;

import reposense.template.GitTestTemplate;

public class GitBlameTest extends GitTestTemplate {

    @Test
    public void blameRaw_validFile_success() {
        String content = blame(config.getRepoRoot(), "blameTest.java");
        Assert.assertFalse(content.isEmpty());
    }

    @Test(expected = RuntimeException.class)
    public void blameRaw_nonExistentFile_throwsRunTimeException() {
        blame(config.getRepoRoot(), "nonExistentFile");
    }

}
