package reposense.git;

import java.nio.file.Paths;

import org.junit.Test;

import reposense.git.exception.GitBranchException;
import reposense.template.GitTestTemplate;

public class GitRevParseTest extends GitTestTemplate {

    @Test
    public void assertBranchExists_withExistingBranch_success() throws GitBranchException {
        config.setBranch("master");
        GitRevParse.assertBranchExists(config, Paths.get(config.getRepoRoot()));
    }

    @Test (expected = GitBranchException.class)
    public void assertBranchExists_withNonExistentBranch_throwsGitBranchException() throws GitBranchException {
        config.setBranch("nonExistentBranch");
        GitRevParse.assertBranchExists(config, Paths.get(config.getRepoRoot()));
    }
}
