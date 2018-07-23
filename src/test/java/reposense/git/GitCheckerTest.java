package reposense.git;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import reposense.model.RepoConfiguration;
import reposense.parser.InvalidLocationException;
import reposense.template.GitTestTemplate;
import reposense.util.FileUtil;
import reposense.util.TestConstants;
import reposense.util.TestUtil;


public class GitCheckerTest extends GitTestTemplate {
    @Test
    public void checkout_fromDiskLocation_success() throws GitDownloaderException, IOException,
            InvalidLocationException {
        Path diskLocation = Paths.get(LOCAL_TEST_REPO_ADDRESS).toAbsolutePath();
        RepoConfiguration diskConfig = new RepoConfiguration(diskLocation.toString(), "master");
        GitDownloader.downloadRepo(diskConfig);
        Path clonedDiskLocation = Paths.get(FileUtil.REPOS_ADDRESS, DISK_REPO_DISPLAY_NAME).toAbsolutePath();
        TestUtil.compareDirectories(diskLocation, clonedDiskLocation);
    }

    @Test
    public void checkoutBranchTest() {
        Path branchFile = Paths.get(LOCAL_TEST_REPO_ADDRESS, "inTestBranch.java");
        Assert.assertFalse(Files.exists(branchFile));

        GitChecker.checkoutBranch(LOCAL_TEST_REPO_ADDRESS, "test");
        Assert.assertTrue(Files.exists(branchFile));
    }

    @Test
    public void checkoutHashTest() {
        Path newFile = Paths.get(LOCAL_TEST_REPO_ADDRESS, "newFile.java");
        Assert.assertTrue(Files.exists(newFile));

        GitChecker.checkout(LOCAL_TEST_REPO_ADDRESS, TestConstants.FIRST_COMMIT_HASH);
        Assert.assertFalse(Files.exists(newFile));
    }

    @Test
    public void checkoutToDateTest() {
        Path newFile = Paths.get(LOCAL_TEST_REPO_ADDRESS, "newFile.java");
        Assert.assertTrue(Files.exists(newFile));

        Date untilDate = TestUtil.getDate(2018, Calendar.FEBRUARY, 6);
        GitChecker.checkoutToDate(LOCAL_TEST_REPO_ADDRESS, config.getBranch(), untilDate);
        Assert.assertFalse(Files.exists(newFile));
    }
}
