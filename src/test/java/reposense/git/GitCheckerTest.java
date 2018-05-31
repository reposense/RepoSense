package reposense.git;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Assert;
import org.junit.Test;

import reposense.template.GitTestTemplate;
import reposense.util.TestConstants;


public class GitCheckerTest extends GitTestTemplate {
    @Test
    public void checkoutBranchTest() {
        GitChecker.checkoutBranch(TestConstants.LOCAL_TEST_REPO_ADDRESS, "test");
        Path branchFile = Paths.get(TestConstants.LOCAL_TEST_REPO_ADDRESS, "inTestBranch.java");
        Assert.assertTrue(Files.exists(branchFile));
    }

    @Test
    public void checkoutHashTest() {
        GitChecker.checkout(TestConstants.LOCAL_TEST_REPO_ADDRESS, TestConstants.FIRST_COMMIT_HASH);
        Path newFile = Paths.get(TestConstants.LOCAL_TEST_REPO_ADDRESS, "newFile.java");
        Assert.assertFalse(Files.exists(newFile));
    }

}
