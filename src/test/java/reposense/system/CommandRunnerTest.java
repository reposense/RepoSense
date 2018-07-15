package reposense.system;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import reposense.template.GitTestTemplate;
import reposense.util.TestUtil;

public class CommandRunnerTest extends GitTestTemplate {

    @Test
    public void cloneTest() {
        Path dir = Paths.get(config.getRepoRoot());
        Assert.assertTrue(Files.exists(dir));
    }

    @Test
    public void checkoutTest() {
        CommandRunner.checkout(config.getRepoRoot(), "test");
        Path branchFile = Paths.get(config.getRepoRoot(), "inTestBranch.java");
        Assert.assertTrue(Files.exists(branchFile));
    }

    @Test
    public void logWithContentTest() {
        String content = CommandRunner.gitLog(
                config.getRepoRoot(), null, null, config.getFileFormats());
        Assert.assertFalse(content.isEmpty());
    }

    @Test
    public void logWithoutContentTest() {
        Date date = TestUtil.getDate(2050, Calendar.JANUARY, 1);
        String content = CommandRunner.gitLog(
                config.getRepoRoot(), date, null, config.getFileFormats());
        Assert.assertTrue(content.isEmpty());

        date = TestUtil.getDate(1950, Calendar.JANUARY, 1);
        content = CommandRunner.gitLog(
                config.getRepoRoot(), null, date, config.getFileFormats());
        Assert.assertTrue(content.isEmpty());
    }

    @Test
    public void blameRawTest() {
        Date sinceDate = TestUtil.getDate(1950, Calendar.JANUARY, 1);
        Date untilDate = TestUtil.getDate(2050, Calendar.JANUARY, 1);

        // supply both sinceDate and untilDate -> success
        String content = CommandRunner.blameRaw(
                config.getRepoRoot(), "blameTest.java", sinceDate, untilDate);
        Assert.assertFalse(content.isEmpty());

        // supply only untilDate -> success
        content = CommandRunner.blameRaw(
                config.getRepoRoot(), "blameTest.java", null, untilDate);
        Assert.assertFalse(content.isEmpty());

        // supply only since sinceDate -> success
        content = CommandRunner.blameRaw(
                config.getRepoRoot(), "blameTest.java", sinceDate, null);
        Assert.assertFalse(content.isEmpty());

        // no sinceDate or untilDate supplied -> success
        content = CommandRunner.blameRaw(
                config.getRepoRoot(), "blameTest.java", null, null);
        Assert.assertFalse(content.isEmpty());
    }
}
