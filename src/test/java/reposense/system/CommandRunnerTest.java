package reposense.system;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import reposense.template.GitTestTemplate;
import reposense.util.TestConstants;
import reposense.util.TestUtil;


public class CommandRunnerTest extends GitTestTemplate {

    @Test
    public void cloneTest() {
        Path dir = Paths.get(TestConstants.LOCAL_TEST_REPO_ADDRESS);
        Assert.assertTrue(Files.exists(dir));
    }

    @Test
    public void checkoutTest() {
        CommandRunner.checkout(TestConstants.LOCAL_TEST_REPO_ADDRESS, "test");
        Path branchFile = Paths.get(TestConstants.LOCAL_TEST_REPO_ADDRESS, "inTestBranch.java");
        Assert.assertTrue(Files.exists(branchFile));
    }

    @Test
    public void log_existingFormats_hasContent() {
        String content =
                CommandRunner.gitLog(TestConstants.LOCAL_TEST_REPO_ADDRESS, null, null, config.getFormats());
        Assert.assertFalse(content.isEmpty());
    }

    @Test
    public void log_nonExistingFormats_noContent() {
        String content =
                CommandRunner.gitLog(TestConstants.LOCAL_TEST_REPO_ADDRESS, null, null, Arrays.asList("py"));
        Assert.assertTrue(content.isEmpty());
    }

    @Test
    public void log_sinceDateInFuture_noContent() {
        Date date = TestUtil.getDate(2050, Calendar.JANUARY, 1);
        String content = CommandRunner.gitLog(
                TestConstants.LOCAL_TEST_REPO_ADDRESS, date, null, config.getFormats());
        Assert.assertTrue(content.isEmpty());

        date = TestUtil.getDate(1950, Calendar.JANUARY, 1);
        content = CommandRunner.gitLog(
                TestConstants.LOCAL_TEST_REPO_ADDRESS, null, date, config.getFormats());
        Assert.assertTrue(content.isEmpty());
    }

    @Test
    public void blameRawTest() {
        Date sinceDate = TestUtil.getDate(1950, Calendar.JANUARY, 1);
        Date untilDate = TestUtil.getDate(2050, Calendar.JANUARY, 1);

        // supply both sinceDate and untilDate -> success
        String content = CommandRunner.blameRaw(
                TestConstants.LOCAL_TEST_REPO_ADDRESS, "blameTest.java", sinceDate, untilDate);
        Assert.assertFalse(content.isEmpty());

        // supply only untilDate -> success
        content = CommandRunner.blameRaw(
                TestConstants.LOCAL_TEST_REPO_ADDRESS, "blameTest.java", null, untilDate);
        Assert.assertFalse(content.isEmpty());

        // supply only since sinceDate -> success
        content = CommandRunner.blameRaw(
                TestConstants.LOCAL_TEST_REPO_ADDRESS, "blameTest.java", sinceDate, null);
        Assert.assertFalse(content.isEmpty());

        // no sinceDate or untilDate supplied -> success
        content = CommandRunner.blameRaw(
                TestConstants.LOCAL_TEST_REPO_ADDRESS, "blameTest.java", null, null);
        Assert.assertFalse(content.isEmpty());
    }
}
