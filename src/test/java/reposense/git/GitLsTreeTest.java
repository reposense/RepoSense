package reposense.git;

import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import reposense.git.exception.GitCloneException;
import reposense.git.exception.InvalidFilePathException;
import reposense.template.GitTestTemplate;
import reposense.util.SystemUtil;

public class GitLsTreeTest extends GitTestTemplate {

    @Before
    public void before() {
        // Runs test only on Windows operating systems
        Assume.assumeTrue(SystemUtil.isWindows());
    }

    @Test
    public void repo_validFilePaths_success() throws InvalidFilePathException, GitCloneException {
        GitLsTree.validateFilePaths(config);
    }

    @Test(expected = InvalidFilePathException.class)
    public void repo_containsInvalidFilePaths_throwsInvalidFilePathException()
            throws InvalidFilePathException, GitCloneException {
        config.setBranch("391-invalid-filepaths");
        GitLsTree.validateFilePaths(config);
    }
}
