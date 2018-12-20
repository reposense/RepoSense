package reposense.git;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import reposense.model.RepoConfiguration;
import reposense.system.CommandRunner;
import reposense.system.LogsManager;
import reposense.util.FileUtil;

/**
 * Clones the repository from GitHub.
 */
public class GitDownloader {

    private static final Logger logger = LogsManager.getLogger(GitDownloader.class);

    public static void downloadRepo(RepoConfiguration repoConfig)
            throws GitDownloaderException {
        try {
            FileUtil.deleteDirectory(repoConfig.getRepoRoot());
            logger.info("Cloning from " + repoConfig.getLocation() + "...");
            CommandRunner.cloneRepo(repoConfig.getLocation(), repoConfig.getRepoName());
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
            if (repoConfig.getBranch().equals(RepoConfiguration.DEFAULT_BRANCH)) {
                String currentBranch = CommandRunner.getCurrentBranch(repoConfig.getRepoRoot());
                repoConfig.setBranch(currentBranch);
            }
            GitChecker.checkout(repoConfig.getRepoRoot(), repoConfig.getBranch());
        } catch (RuntimeException e) {
            logger.log(Level.SEVERE, "Branch does not exist! Analyze terminated.", e);
            throw new GitDownloaderException(e);
        }

    }
}
