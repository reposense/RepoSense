package reposense.git;

import java.nio.file.Paths;

import org.junit.Test;

import reposense.git.exception.GitBranchException;
import reposense.template.GitTestTemplate;
import reposense.util.FileUtil;

public class GitRevParseTest extends GitTestTemplate {

    @Test
    public void assertBranchExists_withExistingBranch_success() throws GitBranchException {
        config.setBranch("master");
        GitRevParse.assertBranchExists(config, Paths.get(FileUtil.REPOS_ADDRESS, config.getRepoFolderName()));
    }

    @Test (expected = GitBranchException.class)
    public void assertBranchExists_withNonExistentBranch_throwsGitBranchException() throws GitBranchException {
        config.setBranch("nonExistentBranch");
        GitRevParse.assertBranchExists(config, Paths.get(FileUtil.REPOS_ADDRESS, config.getRepoFolderName()));
    }
}
