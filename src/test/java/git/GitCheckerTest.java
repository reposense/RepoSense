package git;

import org.junit.Assert;
import org.junit.Test;
import template.GitTestTemplate;
import util.TestConstants;

import java.io.File;

/**
 * Created by matanghao1 on 6/2/18.
 */
public class GitCheckerTest extends GitTestTemplate {
    @Test
    public void checkoutBranchTest(){
        GitChecker.checkoutBranch(TestConstants.LOCAL_TEST_REPO_ADDRESS,"test");
        File branchFile = new File(TestConstants.LOCAL_TEST_REPO_ADDRESS+File.separator+"inTestBranch.java");
        Assert.assertTrue(branchFile.exists());
    }

    @Test
    public void checkoutHashTest() {
        GitChecker.checkout(TestConstants.LOCAL_TEST_REPO_ADDRESS,TestConstants.FIRST_COMMIT_HASH);
        File newFile = new File(TestConstants.LOCAL_TEST_REPO_ADDRESS + File.separator + "newFile.java");
        Assert.assertFalse(newFile.exists());
    }

}
