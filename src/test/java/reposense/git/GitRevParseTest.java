package reposense.git;

import java.nio.file.Paths;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import reposense.git.exception.GitBranchException;
import reposense.template.GitTestTemplate;

public class GitRevParseTest extends GitTestTemplate {

    @Test
    public void assertBranchExists_withExistingBranch_success() throws Exception {
        config.setBranch("master");
        GitRevParse.assertBranchExists(config, Paths.get(config.getRepoRoot()));
    }

    @Test
    public void assertBranchExists_withNonExistentBranch_throwsGitBranchException() {
        config.setBranch("nonExistentBranch");
        Assertions.assertThrows(GitBranchException.class, () -> GitRevParse.assertBranchExists(config,
                Paths.get(config.getRepoRoot())));
    }
}
