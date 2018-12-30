package reposense.git;

import static reposense.system.CommandRunner.runCommand;
import static reposense.util.StringsUtil.addQuote;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

import reposense.model.RepoConfiguration;
import reposense.model.RepoLocation;
import reposense.system.LogsManager;
import reposense.util.FileUtil;

/**
 * Contains git clone related functionalities.
 * Git clone is responsible for cloning a local/remote repository into a new directory.
 */
public class GitClone {

    private static final Logger logger = LogsManager.getLogger(GitClone.class);

    /**
     * Clones repo specified in the {@code repoConfig} and updates it with the branch info.
     */
    public static void clone(RepoConfiguration repoConfig)
            throws GitCloneException {
        try {
            FileUtil.deleteDirectory(repoConfig.getRepoRoot());
            logger.info("Cloning from " + repoConfig.getLocation() + "...");
            clone(repoConfig.getLocation(), repoConfig.getRepoName());
            logger.info("Cloning completed!");
        } catch (RuntimeException rte) {
            logger.log(Level.SEVERE, "Error encountered in Git Cloning, will attempt to continue analyzing", rte);
            throw new GitCloneException(rte);
            //Due to an unsolved bug on Windows Git, for some repository, Git Clone will return an error even
            // though the repo is cloned properly.
        } catch (IOException ioe) {
            throw new GitCloneException(ioe);
        }

        try {
            if (repoConfig.getBranch().equals(RepoConfiguration.DEFAULT_BRANCH)) {
                String currentBranch = GitBranch.getCurrentBranch(repoConfig.getRepoRoot());
                repoConfig.setBranch(currentBranch);
            }
            GitCheckout.checkout(repoConfig.getRepoRoot(), repoConfig.getBranch());
        } catch (RuntimeException e) {
            logger.log(Level.SEVERE, "Branch does not exist! Analyze terminated.", e);
            throw new GitCloneException(e);
        }
    }

    private static void clone(RepoLocation location, String repoName) throws IOException {
        Path rootPath = Paths.get(FileUtil.REPOS_ADDRESS, repoName);
        Files.createDirectories(rootPath);
        runCommand(rootPath, "git clone " + addQuote(location.toString()));
    }
}
