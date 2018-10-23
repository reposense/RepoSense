package reposense.git;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import reposense.template.GitTestTemplate;
import reposense.util.TestUtil;


public class GitCheckerTest extends GitTestTemplate {
    @Test
    public void checkoutBranchTest() {
        Path branchFile = Paths.get(config.getRepoRoot(), "inTestBranch.java");
        Assert.assertFalse(Files.exists(branchFile));

        GitChecker.checkoutBranch(config.getRepoRoot(), "test");
        Assert.assertTrue(Files.exists(branchFile));
    }

    @Test
    public void checkoutHashTest() {
        Path newFile = Paths.get(config.getRepoRoot(), "newFile.java");
        Assert.assertTrue(Files.exists(newFile));

        GitChecker.checkout(config.getRepoRoot(), FIRST_COMMIT_HASH);
        Assert.assertFalse(Files.exists(newFile));
    }

    @Test
    public void checkoutToDate_validDate_success() throws CommitNotFoundException {
        Path newFile = Paths.get(config.getRepoRoot(), "newFile.java");
        Assert.assertTrue(Files.exists(newFile));

        Date untilDate = TestUtil.getDate(2018, Calendar.FEBRUARY, 6);
        GitChecker.checkoutToDate(config.getRepoRoot(), config.getBranch(), untilDate);
        Assert.assertFalse(Files.exists(newFile));
    }

    @Test(expected = CommitNotFoundException.class)
    public void checkoutToDate_invalidDate_throwsEmptyCommitException() throws CommitNotFoundException {
        Date untilDate = TestUtil.getDate(2015, Calendar.FEBRUARY, 6);
        GitChecker.checkoutToDate(config.getRepoRoot(), config.getBranch(), untilDate);
    }
}
