package reposense.git;

import static reposense.system.CommandRunner.runCommand;
import static reposense.system.CommandRunner.spawnCommandProcess;
import static reposense.system.CommandRunner.waitForCommandProcess;
import static reposense.util.StringsUtil.addQuote;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

import reposense.model.RepoConfiguration;
import reposense.model.RepoLocation;
import reposense.system.CommandRunnerProcess;
import reposense.system.LogsManager;
import reposense.util.FileUtil;

/**
 * Contains git clone related functionalities.
 * Git clone is responsible for cloning a local/remote repository into a new directory.
 */
public class GitClone {

    private static final Logger logger = LogsManager.getLogger(GitClone.class);
    private static CommandRunnerProcess crp;
    private static RepoLocation lastClonedRepoLocation;

    /**
     * Clones repo specified in the {@code repoConfig} and updates it with the branch info.
     */
    public static void clone(RepoConfiguration repoConfig)
            throws GitCloneException {
        try {
            FileUtil.deleteDirectory(repoConfig.getRepoRoot());
            logger.info("Cloning from " + repoConfig.getLocation() + "...");
            clone(repoConfig.getLocation(), repoConfig.getRepoFolderName());
            logger.info("Cloning completed!");
        } catch (RuntimeException rte) {
            logger.log(Level.SEVERE, "Error encountered in Git Cloning, will attempt to continue analyzing", rte);
            throw new GitCloneException(rte);
            //Due to an unsolved bug on Windows Git, for some repository, Git Clone will return an error even
            // though the repo is cloned properly.
        } catch (IOException ioe) {
            throw new GitCloneException(ioe);
        }
        updateRepoConfigBranch(repoConfig);
        checkOutBranch(repoConfig);
    }

    private static void clone(RepoLocation location, String repoName) throws IOException {
        Path rootPath = Paths.get(FileUtil.REPOS_ADDRESS, repoName);
        Files.createDirectories(rootPath);
        runCommand(rootPath, "git clone " + addQuote(location.toString()));
    }

    /**
     * Spawns a process to clone repo specified in {@code repoConfig}. Does not wait for process to finish executing.
     * Can only handle a maximum of one spawned process at any time and subsequent calls are ignored if current process
     * is still running.
     */
    public static void spawnCloneProcess(RepoConfiguration repoConfig) throws GitCloneException {
        if (isRepoPreviouslyCloned(repoConfig) || crp != null) {
            return;
        }
        try {
            FileUtil.deleteDirectory(repoConfig.getRepoRoot());
            logger.info("Cloning in parallel from " + repoConfig.getLocation() + "...");
            Path rootPath = Paths.get(FileUtil.REPOS_ADDRESS, repoConfig.getRepoFolderName());
            Files.createDirectories(rootPath);
            crp = spawnCommandProcess(rootPath, "git clone " + addQuote(repoConfig.getLocation().toString()));
        } catch (RuntimeException rte) {
            logger.log(Level.SEVERE, "Error encountered in Git Cloning, will attempt to continue analyzing", rte);
            throw new GitCloneException(rte);
        } catch (IOException ioe) {
            throw new GitCloneException(ioe);
        }
    }

    /**
     * Waits for previously spawned clone process to finish executing.
     * Should only be called after {@code spawnCloneProcess} has been called.
     */
    public static void waitForCloneProcess(RepoConfiguration repoConfig) throws GitCloneException {
        if (isRepoPreviouslyCloned(repoConfig)) {
            return;
        }
        try {
            Path rootPath = Paths.get(FileUtil.REPOS_ADDRESS, repoConfig.getRepoFolderName());
            waitForCommandProcess(rootPath, "git clone " + addQuote(repoConfig.getLocation().toString()), crp);
            logger.info("Cloning of " + repoConfig.getLocation() + " completed!");
            updateRepoConfigBranch(repoConfig);
            crp = null;
            lastClonedRepoLocation = repoConfig.getLocation();
        } catch (RuntimeException rte) {
            logger.log(Level.SEVERE, "Error encountered in Git Cloning, will attempt to continue analyzing", rte);
            throw new GitCloneException(rte);
        }
    }

    /**
     * Updates the branch of {@code repoConfig} to the current branch if default branch is specified.
     */
    private static void updateRepoConfigBranch(RepoConfiguration repoConfig) {
        if (repoConfig.getBranch().equals(RepoConfiguration.DEFAULT_BRANCH)) {
            String currentBranch = GitBranch.getCurrentBranch(repoConfig.getRepoRoot());
            repoConfig.setBranch(currentBranch);
        }
    }

    /**
     * Checks out the branch specified in {@code repoConfig}.
     */
    public static void checkOutBranch(RepoConfiguration repoConfig) throws GitCloneException {
        try {
            GitCheckout.checkout(repoConfig.getRepoRoot(), repoConfig.getBranch());
        } catch (RuntimeException e) {
            logger.log(Level.SEVERE, "Branch does not exist! Analysis terminated.", e);
            throw new GitCloneException(e);
        }
    }

    /**
     * Returns true if {@code repoConfig} matches the previously cloned repo.
     */
    private static boolean isRepoPreviouslyCloned(RepoConfiguration repoConfig) {
        return lastClonedRepoLocation != null && lastClonedRepoLocation.equals(repoConfig.getLocation());
    }
}
