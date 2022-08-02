package reposense.git;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import reposense.model.RepoConfiguration;
import reposense.template.GitTestTemplate;

public class GitCloneTest extends GitTestTemplate {
    private RepoConfiguration config;

    @BeforeEach
    public void before() throws Exception {
        super.before();
        config = configs.get();
    }

    @Test
    public void cloneTest_validRepo_success() {
        // As the clone has been performed in the {@code GitTestTemplate},
        // this checks whether the clone has been executed successfully by performing a file system check.
        Path dir = Paths.get(config.getRepoRoot());
        Assertions.assertTrue(Files.exists(dir));
    }
}
