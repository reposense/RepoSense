package reposense.git;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import reposense.system.CommandRunner;
import reposense.system.LogsManager;
import reposense.util.FileUtil;

/**
 * Clones the repository from GitHub.
 */
public class GitDownloader {

    private static final Logger logger = LogsManager.getLogger(GitDownloader.class);

    public static void downloadRepo(String location, String displayName, String repoName, String branchName)
            throws GitDownloaderException {
        try {
            FileUtil.deleteDirectory(FileUtil.getRepoDirectory(displayName));
            logger.info("Cloning " + location + "...");
            CommandRunner.cloneRepo(location, displayName);
            logger.info("Cloning completed!");
        } catch (RuntimeException rte) {
            logger.log(Level.SEVERE, "Error encountered in Git Cloning, will attempt to continue analyzing", rte);
            throw new GitDownloaderException(rte);
            //Due to an unsolved bug on Windows Git, for some repository, Git Clone will return an error even
            // though the repo is cloned properly.
        } catch (IOException ioe) {
            throw new GitDownloaderException(ioe);
        }

        try {
            GitChecker.checkout(FileUtil.getRepoDirectory(displayName, repoName), branchName);
        } catch (RuntimeException e) {
            logger.log(Level.SEVERE, "Branch does not exist! Analyze terminated.", e);
            throw new GitDownloaderException(e);
        }

    }
}
