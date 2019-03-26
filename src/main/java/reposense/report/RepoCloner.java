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
import reposense.git.GitCheckout;
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
    public RepoLocation getClonedRepo(String outputPath) throws IOException {
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
     * Prepares repo specified by {@code config} to be analyzed by updating and checking out its branch.
     */
    public void updateAndCheckoutBranch(RepoConfiguration config) {
        config.updateBranch(currentRepoDefaultBranch);
        GitCheckout.checkout(config.getRepoRoot(), config.getBranch());
    }

    /**
     * Cleans up after all repos have been cloned and analyzed
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
            FileUtil.deleteDirectory(config.getRepoRoot());
            logger.info("Cloning in parallel from " + config.getLocation() + "...");
            Path rootPath = Paths.get(FileUtil.REPOS_ADDRESS, config.getRepoFolderName());
            Files.createDirectories(rootPath);
            crp = runCommandAsync(rootPath, "git clone " + addQuote(config.getLocation().toString()));
        } catch (RuntimeException | IOException e) {
            logger.log(Level.WARNING,
                    "Exception met while trying to clone the repo, will skip this repo.", e);
            handleCloningFailed(outputPath, config);
            return false;
        }
        return true;
    }

    /**
     * Waits for previously spawned clone process to finish executing.
     * Should only be called after {@code spawnCloneProcess} has been called.
     */
    private boolean waitForCloneProcess(String outputPath, RepoConfiguration config) throws IOException {
        try {
            crp.waitForProcess();
            logger.info("Cloning of " + config.getLocation() + " completed!");
        } catch (RuntimeException | CommandRunnerProcessException e) {
            crp = null;
            logger.log(Level.WARNING,
                    "Exception met while trying to clone the repo, will skip this repo.", e);
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

    private void deleteDirectory(String root) {
        try {
            FileUtil.deleteDirectory(root);
        } catch (IOException ioe) {
            logger.log(Level.WARNING, "Error deleting report directory.", ioe);
        }
    }
}
