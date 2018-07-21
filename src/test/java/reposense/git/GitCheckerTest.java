package reposense.git;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import reposense.template.GitTestTemplate;
import reposense.util.Constants;
import reposense.util.TestConstants;
import reposense.util.TestUtil;


public class GitCheckerTest extends GitTestTemplate {
    @Test
    public void checkout_fromDiskLocation_success() throws GitDownloaderException, IOException {
        Path diskLocation = Paths.get(TestConstants.LOCAL_TEST_REPO_ADDRESS).toAbsolutePath();
        GitDownloader.downloadRepo(diskLocation.toString(),
                TestConstants.DISK_REPO_DISPLAY_NAME, TestConstants.TEST_REPO, "master");
        Path clonedDiskLocation =
                Paths.get(Constants.REPOS_ADDRESS, TestConstants.DISK_REPO_DISPLAY_NAME).toAbsolutePath();
        TestUtil.compareDirectories(diskLocation, clonedDiskLocation);
    }

    @Test
    public void checkoutBranchTest() {
        Path branchFile = Paths.get(TestConstants.LOCAL_TEST_REPO_ADDRESS, "inTestBranch.java");
        Assert.assertFalse(Files.exists(branchFile));

        GitChecker.checkoutBranch(TestConstants.LOCAL_TEST_REPO_ADDRESS, "test");
        Assert.assertTrue(Files.exists(branchFile));
    }

    @Test
    public void checkoutHashTest() {
        Path newFile = Paths.get(TestConstants.LOCAL_TEST_REPO_ADDRESS, "newFile.java");
        Assert.assertTrue(Files.exists(newFile));

        GitChecker.checkout(TestConstants.LOCAL_TEST_REPO_ADDRESS, TestConstants.FIRST_COMMIT_HASH);
        Assert.assertFalse(Files.exists(newFile));
    }

    @Test
    public void checkoutToDateTest() {
        Path newFile = Paths.get(TestConstants.LOCAL_TEST_REPO_ADDRESS, "newFile.java");
        Assert.assertTrue(Files.exists(newFile));

        Date untilDate = TestUtil.getDate(2018, Calendar.FEBRUARY, 6);
        GitChecker.checkoutToDate(TestConstants.LOCAL_TEST_REPO_ADDRESS, config.getBranch(), untilDate);
        Assert.assertFalse(Files.exists(newFile));
    }
}
