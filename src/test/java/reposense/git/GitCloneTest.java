package reposense.git;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Assert;
import org.junit.Test;

import reposense.template.GitTestTemplate;

public class GitCloneTest extends GitTestTemplate {

    @Test
    public void cloneTest_validRepo_success() throws Exception {
        // As the clone has been performed in the {@code GitTestTemplate},
        // this checks whether the clone has been executed successfully by performing a file system check.
        Path dir = Paths.get(config.getRepoRoot());
        Assert.assertTrue(Files.exists(dir));
    }
}
