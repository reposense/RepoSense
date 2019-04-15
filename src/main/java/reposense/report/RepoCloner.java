package reposense.report;

import static reposense.system.CommandRunner.runCommandAsync;
import static reposense.util.StringsUtil.addQuote;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

import reposense.git.GitBranch;
import reposense.git.GitLsTree;
import reposense.git.exception.GitCloneException;
import reposense.git.exception.InvalidFilePathException;

import reposense.model.RepoConfiguration;
import reposense.model.RepoLocation;
import reposense.system.CommandRunnerProcess;
import reposense.system.CommandRunnerProcessException;
import reposense.system.LogsManager;
import reposense.util.FileUtil;

/**
 * Handles asynchronous cloning of repos to allow multiple repos to be cloned and analyzed concurrently.
 */
public class RepoCloner {
    private static final String MESSAGE_START_CLONING = "Cloning in parallel from %s...";
    private static final String MESSAGE_WAITING_FOR_CLONING = "Waiting for cloning of %s to complete...";
    private static final String MESSAGE_COMPLETE_CLONING = "Cloning of %s completed!";
    private static final String MESSAGE_ERROR_DELETING_DIRECTORY = "Error deleting report directory.";
    private static final String MESSAGE_ERROR_CLONING =
            "Exception met while trying to clone the repo, will skip this repo.";

    private static final int MAX_NO_OF_REPOS = 2;
    private static final Logger logger = LogsManager.getLogger(RepoCloner.class);

    private RepoConfiguration[] configs = new RepoConfiguration[MAX_NO_OF_REPOS];
    private int currentIndex = 0;
    private int previousIndex = 0;
    private boolean isCurrentRepoCloned = false;
    private String currentRepoDefaultBranch;
    private CommandRunnerProcess crp;

    /**
     * Spawns a process to clone the repository specified by {@code config}.
     * Does not wait for process to finish executing.
     */
    public void clone(String outputPath, RepoConfiguration config) throws IOException {
        configs[currentIndex] = config;
        isCurrentRepoCloned = spawnCloneProcess(outputPath, config);
    }

    /**
     * Waits for current clone process to finish executing and returns the {@code RepoLocation} of the corresponding
     * {@code RepoConfiguration}.
     */
    public RepoLocation getClonedRepoLocation(String outputPath) throws IOException {
        if (isCurrentRepoCloned) {
            isCurrentRepoCloned = waitForCloneProcess(outputPath, configs[currentIndex]);
        }

        if (!isCurrentRepoCloned) {
            deleteDirectory(configs[currentIndex].getRepoRoot());
            return null;
        }

        currentRepoDefaultBranch = GitBranch.getCurrentBranch(configs[currentIndex].getRepoRoot());
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
     * Spawns a process to clone repo specified in {@code repoConfig}. Does not wait for process to finish executing.
     * Should only handle a maximum of one spawned process at any time.
     */
    private boolean spawnCloneProcess(String outputPath, RepoConfiguration config) throws IOException {
        assert(crp == null);

        try {
            GitLsTree.validateFilePaths(config);

            FileUtil.deleteDirectory(config.getRepoRoot());
            logger.info(String.format(MESSAGE_START_CLONING, config.getLocation()));
            Path rootPath = Paths.get(FileUtil.REPOS_ADDRESS, config.getRepoFolderName());
            Files.createDirectories(rootPath);
            crp = runCommandAsync(rootPath, "git clone " + addQuote(config.getLocation().toString()));
        } catch (RuntimeException | IOException e) {
            logger.log(Level.WARNING, MESSAGE_ERROR_CLONING, e);
            handleCloningFailed(outputPath, config);
            return false;
        } catch (InvalidFilePathException e) {
            handleCloningFailed(outputPath, config);
            return false;
        } catch (GitCloneException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * Waits for previously spawned clone process to finish executing.
     * Should only be called after {@code spawnCloneProcess} has been called.
     */
    private boolean waitForCloneProcess(String outputPath, RepoConfiguration config) throws IOException {
        try {
            logger.info(String.format(MESSAGE_WAITING_FOR_CLONING, config.getLocation()));
            crp.waitForProcess();
            logger.info(String.format(MESSAGE_COMPLETE_CLONING, config.getLocation()));
        } catch (RuntimeException | CommandRunnerProcessException e) {
            crp = null;
            logger.log(Level.WARNING, MESSAGE_ERROR_CLONING, e);
            handleCloningFailed(outputPath, config);
            return false;
        }
        crp = null;
        return true;
    }

    private void handleCloningFailed(String outputPath, RepoConfiguration config) throws IOException {
        Path repoReportDirectory = Paths.get(outputPath, config.getDisplayName());
        FileUtil.createDirectory(repoReportDirectory);
        ReportGenerator.generateEmptyRepoReport(repoReportDirectory.toString());
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
    * Deletes the {@code root} directory.
    */
    private void deleteDirectory(String root) {
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
