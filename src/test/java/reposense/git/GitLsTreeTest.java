package reposense.git;

import org.junit.Assume;
import org.junit.Test;

import reposense.git.exception.GitCloneException;
import reposense.git.exception.InvalidFilePathException;
import reposense.template.GitTestTemplate;
import reposense.util.SystemUtil;

public class GitLsTreeTest extends GitTestTemplate {

    @Test
    public void repo_validFilePaths_success() throws InvalidFilePathException, GitCloneException {
        GitLsTree.validateFilePaths(config);
    }

    @Test(expected = InvalidFilePathException.class)
    public void windows_cloneInvalidWindowsFilePaths_throwsInvalidFilePathException()
            throws InvalidFilePathException, GitCloneException {
        // Runs test only on Windows operating systems
        Assume.assumeTrue(SystemUtil.isWindows());

        config.setBranch("391-invalid-filepaths");
        GitLsTree.validateFilePaths(config);
    }

    @Test
    public void unix_cloneInvalidWindowsFilePaths_success() {
        // Runs test only on non Windows (Unix) operating systems
        Assume.assumeTrue(!SystemUtil.isWindows());

        config.setBranch("391-invalid-filepaths");
    }
}
