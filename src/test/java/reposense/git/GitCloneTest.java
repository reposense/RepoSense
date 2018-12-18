package reposense.git;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Assert;
import org.junit.Test;

import reposense.template.GitTestTemplate;

public class GitCloneTest extends GitTestTemplate {

    @Test
    public void cloneTest_validRepo_success() {
        Path dir = Paths.get(config.getRepoRoot());
        Assert.assertTrue(Files.exists(dir));
    }
}
