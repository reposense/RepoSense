package reposense.git;

import java.io.IOException;
import java.util.logging.Logger;

import reposense.system.CommandRunner;
import reposense.system.LogsManager;
import reposense.util.FileUtil;


public class GitCloner {

    private static final Logger logger = LogsManager.getLogger(GitCloner.class);

    public static void downloadRepo(String organization, String repoName, String branchName) throws GitClonerException {
        try {
            FileUtil.deleteDirectory(FileUtil.getRepoDirectory(organization, repoName));
            logger.info("Cloning " + organization + "/" + repoName + "...");
            CommandRunner.cloneRepo(organization, repoName);
            logger.info("Cloning completed!");
        } catch (RuntimeException e) {
            logger.warning("Error encountered in Git Cloning, will attempt to continue analyzing");
            logger.severe(LogsManager.getErrorDetails(e));
            throw new GitClonerException(e);
            //Due to an unsolved bug on Windows Git, for some repository, Git Clone will return an error even
            // though the repo is cloned properly.
        } catch (IOException ioe) {
            throw new GitClonerException(ioe);
        }

        try {
            GitChecker.checkout(FileUtil.getRepoDirectory(organization, repoName), branchName);
        } catch (RuntimeException e) {
            logger.severe("Branch does not exist! Analyze terminated.");
            e.printStackTrace();
            throw new GitClonerException(e);
        }

    }
}
