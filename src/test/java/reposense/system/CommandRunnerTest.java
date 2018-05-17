package reposense.system;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;

import org.junit.Assert;
import org.junit.Test;

import reposense.template.GitTestTemplate;
import reposense.util.TestConstants;


public class CommandRunnerTest extends GitTestTemplate {

    @Test
    public void cloneTest() {
        Path dir = Paths.get(TestConstants.LOCAL_TEST_REPO_ADDRESS);
        Assert.assertTrue(Files.exists(dir));
    }

    @Test
    public void checkoutTest() {
        CommandRunner.checkOut(TestConstants.LOCAL_TEST_REPO_ADDRESS, "test");
        Path branchFile = Paths.get(TestConstants.LOCAL_TEST_REPO_ADDRESS, "inTestBranch.java");
        Assert.assertTrue(Files.exists(branchFile));
    }

    @Test
    public void logWithContentTest() {
        String content = CommandRunner.gitLog(TestConstants.LOCAL_TEST_REPO_ADDRESS, null, null);
        Assert.assertNotEquals(content.length(), 0);
    }

    @Test
    public void logWithoutContentTest() {
        Calendar c = Calendar.getInstance();
        c.set(2050, 1, 1, 0, 0);
        String content = "";
        try {
            content = CommandRunner.gitLog(TestConstants.LOCAL_TEST_REPO_ADDRESS, c.getTime(), null);
            Assert.assertEquals(content.length(), 0);
        } catch (Exception e) {
            Assert.assertTrue(false);
        }
        c.set(1950, 1, 1, 0, 0);

        try {
            content = CommandRunner.gitLog(TestConstants.LOCAL_TEST_REPO_ADDRESS, null, c.getTime());
            Assert.assertEquals(content.length(), 0);
        } catch (Exception e) {
            Assert.assertTrue(false);
        }

    }

    @Test
    public void blameRawTest() {
        try {
            String content = CommandRunner.blameRaw(TestConstants.LOCAL_TEST_REPO_ADDRESS, "blameTest.java");
            Assert.assertNotEquals(content.length(), 0);
        } catch (Exception e) {
            Assert.assertTrue(false);
        }
    }


}
