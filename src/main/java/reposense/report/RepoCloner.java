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
import reposense.git.exception.BranchNotFoundException;
import reposense.model.RepoConfiguration;
import reposense.system.CommandRunnerProcess;
import reposense.system.CommandRunnerProcessException;
import reposense.system.LogsManager;
import reposense.util.FileUtil;

public class RepoCloner {
    private static final Logger logger = LogsManager.getLogger(RepoCloner.class);

    private RepoConfiguration[] configs = new RepoConfiguration[2];
    private int index = 0;
    private int prevIndex = 0;
    private boolean isCurrentRepoCloned = false;
    private String prevRepoDefaultBranch;
    private CommandRunnerProcess crp;

    /**
     * Spawns a process to clone the repository specified by {@code config}.
     * Does not wait for process to finish executing.
     */
    public void clone(String outputPath, RepoConfiguration config) throws IOException {
        configs[index] = config;
        isCurrentRepoCloned = true;
        if (isPreviousRepoDifferent()) {
            isCurrentRepoCloned = spawnCloneProcess(outputPath, config);
        }
    }

    /**
     * Waits for current clone process to finish executing and returns the corresponding {@code RepoConfiguration}.
     */
    public RepoConfiguration getClonedRepo(String outputPath) throws IOException {
        if (isCurrentRepoCloned && isPreviousRepoDifferent()) {
            isCurrentRepoCloned = waitForCloneProcess(outputPath, configs[index]);
        }

        if (!isCurrentRepoCloned) {
            deleteDirectory(configs[index].getRepoRoot());
            return null;
        } else if (isPreviousRepoDifferent()) {
            prevRepoDefaultBranch = GitBranch.getCurrentBranch(configs[index].getRepoRoot());
        } else {
            configs[index].updateBranch(prevRepoDefaultBranch);
        }
        cleanupPrevRepoFolder();

        try {
            GitCheckout.checkout(configs[index].getRepoRoot(), configs[index].getBranch());
        } catch (RuntimeException e) {
            logger.log(Level.SEVERE, "Branch does not exist! Analysis terminated.", e);
            generateEmptyRepoReport(outputPath, configs[index]);
            return null;
        }
        prevIndex = index;
        index = (index + 1) % configs.length;
        return configs[prevIndex];
    }

    /**
     * Cleans up after all repos have been cloned and analyzed
     */
    public void cleanup() {
        deleteDirectory(FileUtil.REPOS_ADDRESS);
    }

    /**
     * Returns true if current repo is different from the previously cloned repo.
     */
    private boolean isPreviousRepoDifferent() {
        return prevIndex == index || !configs[prevIndex].getLocation().equals(configs[index].getLocation());
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
            generateEmptyRepoReport(outputPath, config);
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
            generateEmptyRepoReport(outputPath, config);
            return false;
        }
        crp = null;

        try {
            config.updateBranch();
        } catch (BranchNotFoundException e) {
            logger.log(Level.SEVERE, "Branch does not exist! Analysis terminated.", e);
            generateEmptyRepoReport(outputPath, config);
            return false;
        }
        return true;
    }

    private void generateEmptyRepoReport(String outputPath, RepoConfiguration config) throws IOException {
        Path repoReportDirectory = Paths.get(outputPath, config.getDisplayName());
        FileUtil.createDirectory(repoReportDirectory);
        ReportGenerator.generateEmptyRepoReport(repoReportDirectory.toString());
    }

    /**
     * Deletes previously cloned repo directories that are not in use anymore.
     */
    private void cleanupPrevRepoFolder() {
        if (isPreviousRepoDifferent() && prevIndex != index) {
            deleteDirectory(configs[prevIndex].getRepoRoot());
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
