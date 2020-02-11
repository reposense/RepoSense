package reposense.git;

import java.io.IOException;

import org.junit.After;
import org.junit.Assume;
import org.junit.Test;

import reposense.git.exception.InvalidFilePathException;
import reposense.model.RepoConfiguration;
import reposense.template.GitTestTemplate;
import reposense.util.FileUtil;
import reposense.util.SystemUtil;

public class GitLsTreeTest extends GitTestTemplate {

    @After
    public void after() {
       // Overrides checkout master behaviour in GitTestTemplate as it throws error when run on a bare clone.
    }

    @Test
    public void repo_validFilePaths_success() throws Exception {
        validateFilePaths(config);
    }

    @Test(expected = InvalidFilePathException.class)
    public void windows_cloneInvalidWindowsFilePaths_throwsInvalidFilePathException()
            throws Exception {
        // Runs test only on Windows operating systems
        Assume.assumeTrue(SystemUtil.isWindows());

        config.setBranch("391-invalid-filepaths");
        validateFilePaths(config);
    }

    @Test
    public void unix_cloneInvalidWindowsFilePaths_success() {
        // Runs test only on non Windows (Unix) operating systems
        Assume.assumeTrue(!SystemUtil.isWindows());

        config.setBranch("391-invalid-filepaths");
    }

    /**
     * Clones a bare repo in {@code config} and verifies that the repo contains only file paths that are
     * compatible in Windows.
     * @throws IOException if it fails to create/delete the folders.
     * @throws InvalidFilePathException if the repository contains invalid file paths that are not compatible with
     * Windows.
     */
    private void validateFilePaths(RepoConfiguration config) throws Exception {
        GitClone.cloneBare(config, FileUtil.getBareRepoFolderName(config));
        GitLsTree.validateFilePaths(config, FileUtil.getBareRepoPath(config));
    }
}
