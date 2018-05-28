package reposense.git;

import java.util.logging.Logger;

import reposense.system.CommandRunner;
import reposense.system.LogsManager;
import reposense.util.FileUtil;


public class GitCloner {

    private static final Logger logger = LogsManager.getLogger(GitCloner.class);

    public static void downloadRepo(String organization, String repoName, String branchName) throws GitClonerException {
        FileUtil.deleteDirectory(FileUtil.getRepoDirectory(organization, repoName));


        try {
            logger.info("Cloning " + organization + "/" + repoName + "...");
            CommandRunner.cloneRepo(organization, repoName);
            logger.info("Cloning completed!");
        } catch (RuntimeException e) {
            logger.info("Error encountered in Git Cloning, will attempt to continue analyzing");
            e.printStackTrace();
            throw new GitClonerException(e);
            //Due to an unsolved bug on Windows Git, for some repository, Git Clone will return an error even
            // though the repo is cloned properly.
        }

        try {
            GitChecker.checkout(FileUtil.getRepoDirectory(organization, repoName), branchName);
        } catch (RuntimeException e) {
            logger.info("Error: Branch does not exist! Analyze terminated.");
            e.printStackTrace();
            throw new GitClonerException(e);
        }

    }
}
