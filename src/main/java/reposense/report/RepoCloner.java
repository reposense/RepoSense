package reposense.report;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

import reposense.git.GitBranch;
import reposense.git.GitClone;
import reposense.git.exception.GitBranchException;
import reposense.git.exception.GitCloneException;
import reposense.model.RepoConfiguration;
import reposense.model.RepoLocation;
import reposense.system.CommandRunnerProcess;
import reposense.system.CommandRunnerProcessException;
import reposense.system.LogsManager;
import reposense.util.FileUtil;
import reposense.util.SystemUtil;

/**
 * Handles asynchronous cloning of repos to allow multiple repos to be cloned and analyzed concurrently.
 */
public class RepoCloner {
    private static final String MESSAGE_START_CLONING = "Cloning in parallel from %s...";
    private static final String MESSAGE_WAITING_FOR_CLONING = "Waiting for cloning of %s to complete...";
    private static final String MESSAGE_COMPLETE_CLONING = "Cloning of %s completed!";
    private static final String MESSAGE_ERROR_DELETING_DIRECTORY = "Error deleting report directory.";
    private static final String MESSAGE_ERROR_CLONING =
            "Exception met while trying to clone the repo \"%s\", will skip this repo.";
    private static final String MESSAGE_ERROR_GETTING_BRANCH =
            "Exception met while trying to get current branch of %s (%s), will skip this repo.";

    private static final int MAX_NO_OF_REPOS = 2;
    private static final Logger logger = LogsManager.getLogger(RepoCloner.class);

    private RepoConfiguration[] configs = new RepoConfiguration[MAX_NO_OF_REPOS];
    private int currentIndex = 0;
    private int previousIndex = 0;
    private boolean isCurrentRepoCloned = false;
    private String currentRepoDefaultBranch;
    private CommandRunnerProcess crp;

    /**
     * Spawns a process to clone the bare repository specified by {@code config}.
     * Does not wait for process to finish executing.
     */
    public void cloneBare(RepoConfiguration config) {
        configs[currentIndex] = config;
        isCurrentRepoCloned = spawnCloneProcess(config);
    }

    /**
     * Waits for current clone process to finish executing and returns the {@code RepoLocation} of the corresponding
     * {@code RepoConfiguration}.
     */
    public RepoLocation getClonedRepoLocation() {
        if (isCurrentRepoCloned) {
            isCurrentRepoCloned = waitForCloneProcess(configs[currentIndex]);
        }

        if (!isCurrentRepoCloned) {
            deleteDirectory(configs[currentIndex].getRepoRoot());
            return null;
        }

        try {
            String bareRepoPath = FileUtil.getBareRepoPath(configs[currentIndex]).toString();
            currentRepoDefaultBranch = GitBranch.getCurrentBranch(bareRepoPath);
        } catch (GitBranchException gbe) {
            // GitBranch will throw this exception when repository is empty
            logger.log(Level.WARNING, String.format(MESSAGE_ERROR_GETTING_BRANCH,
                    configs[currentIndex].getLocation(), configs[currentIndex].getBranch()), gbe);
            return null;
        }
        cleanupPrevRepoFolder();

        previousIndex = currentIndex;
        currentIndex = (currentIndex + 1) % configs.length;
        return configs[previousIndex].getLocation();
    }

    /**
     * Cleans up after all repos have been cloned and analyzed.
     */
    public void cleanup() {
        deleteDirectory(FileUtil.REPOS_ADDRESS);
    }

    /**
     * Spawns a process to clone repo specified in {@code config}. Does not wait for process to finish executing.
     * Should only handle a maximum of one spawned process at any time.
     */
    private boolean spawnCloneProcess(RepoConfiguration config) {
        assert(crp == null);

        try {
            if (!SystemUtil.isTestEnvironment()) {
                FileUtil.deleteDirectory(FileUtil.getBareRepoPath(config).toString());
            }

            Path rootPath = Paths.get(FileUtil.REPOS_ADDRESS, config.getRepoFolderName());
            Path repoPath = Paths.get(rootPath.toString(), config.getRepoName());

            if (SystemUtil.isTestEnvironment() && Files.exists(repoPath)) {
                logger.info("Skipped cloning from " + config.getLocation() + " as it was cloned before.");
                return true;
            }

            Files.createDirectories(rootPath);

            logger.info(String.format(MESSAGE_START_CLONING, config.getLocation()));
            crp = GitClone.cloneBareAsync(config, rootPath, FileUtil.getBareRepoFolderName(config));
        } catch (GitCloneException | IOException e) {
            logger.log(Level.WARNING, String.format(MESSAGE_ERROR_CLONING, config.getDisplayName()), e);
            return false;
        }
        return true;
    }

    /**
     * Waits for previously spawned clone process to finish executing.
     * Should only be called after {@code spawnCloneProcess} has been called.
     */
    private boolean waitForCloneProcess(RepoConfiguration config) {
        try {
            Path repoPath = Paths.get(FileUtil.REPOS_ADDRESS, config.getRepoFolderName(), config.getRepoName());

            if (SystemUtil.isTestEnvironment() && Files.exists(repoPath)) {
                return true;
            }

            logger.info(String.format(MESSAGE_WAITING_FOR_CLONING, config.getLocation()));
            crp.waitForProcess();
            logger.info(String.format(MESSAGE_COMPLETE_CLONING, config.getLocation()));
        } catch (RuntimeException | CommandRunnerProcessException e) {
            crp = null;
            logger.log(Level.WARNING, String.format(MESSAGE_ERROR_CLONING, config.getDisplayName()), e);
            return false;
        }
        crp = null;
        return true;
    }

    /**
     * Deletes previously cloned repo directories that are not in use anymore.
     */
    private void cleanupPrevRepoFolder() {
        if (previousIndex != currentIndex) {
            deleteDirectory(configs[previousIndex].getRepoRoot());
        }
    }

    /**
     * Deletes the {@code root} directory, unless RepoSense is currently being tested.
     */
    private void deleteDirectory(String root) {
        if (SystemUtil.isTestEnvironment()) {
            return;
        }

        try {
            FileUtil.deleteDirectory(root);
        } catch (IOException ioe) {
            logger.log(Level.WARNING, MESSAGE_ERROR_DELETING_DIRECTORY, ioe);
        }
    }

    public String getCurrentRepoDefaultBranch() {
        return currentRepoDefaultBranch;
    }
}
