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
import reposense.util.TestUtil;


public class GitCheckerTest extends GitTestTemplate {
    @Test
    public void checkout_fromDiskLocation_success() throws GitDownloaderException, IOException,
            InvalidLocationException {
        Path diskLocation = Paths.get(config.getRepoRoot()).toAbsolutePath();
        RepoConfiguration diskConfig = new RepoConfiguration(diskLocation.toString(), "master");
        GitDownloader.downloadRepo(diskConfig);
        Path clonedDiskLocation = Paths.get(FileUtil.REPOS_ADDRESS, DISK_REPO_DISPLAY_NAME).toAbsolutePath();
        TestUtil.compareDirectories(diskLocation, clonedDiskLocation);
    }

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
    public void checkoutToDateTest() {
        Path newFile = Paths.get(config.getRepoRoot(), "newFile.java");
        Assert.assertTrue(Files.exists(newFile));

        Date untilDate = TestUtil.getDate(2018, Calendar.FEBRUARY, 6);
        GitChecker.checkoutToDate(config.getRepoRoot(), config.getBranch(), untilDate);
        Assert.assertFalse(Files.exists(newFile));
    }
}
