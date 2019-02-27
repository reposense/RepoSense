package reposense.report;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

import reposense.git.GitBranch;
import reposense.git.GitClone;
import reposense.git.GitCloneException;
import reposense.model.RepoConfiguration;
import reposense.system.LogsManager;
import reposense.util.FileUtil;

public class RepoCloner {
    private static final Logger logger = LogsManager.getLogger(RepoCloner.class);

    private RepoConfiguration[] configs = new RepoConfiguration[2];
    private int index = 0;
    private int prevIndex = 0;
    private boolean isCurrentRepoCloned = false;
    private String prevRepoDefaultBranch;

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
            GitClone.updateRepoConfigBranch(configs[index], prevRepoDefaultBranch);
        }
        cleanupPrevRepoFolder();
        prevIndex = index;
        index = (index + 1) % configs.length;
        return configs[prevIndex];
    }

    /**
     * Returns true if current repo is different from the previously cloned repo.
     */
    private boolean isPreviousRepoDifferent() {
        return prevIndex == index || !configs[prevIndex].getLocation().equals(configs[index].getLocation());
    }

    private boolean spawnCloneProcess(String outputPath, RepoConfiguration config) throws IOException {
        try {
            GitClone.spawnCloneProcess(config);
            return true;
        } catch (GitCloneException gde) {
            logger.log(Level.WARNING,
                    "Exception met while trying to clone the repo, will skip this repo.", gde);
            generateEmptyRepoReport(outputPath, config);
        } catch (RuntimeException rte) {
            logger.log(Level.SEVERE, "Error has occurred during analysis, will skip this repo.", rte);
        }
        return false;
    }

    private boolean waitForCloneProcess(String outputPath, RepoConfiguration config) throws IOException {
        try {
            GitClone.waitForCloneProcess(config);
            return true;
        } catch (GitCloneException gde) {
            logger.log(Level.WARNING,
                    "Exception met while trying to clone the repo, will skip this repo.", gde);
            generateEmptyRepoReport(outputPath, config);
        } catch (RuntimeException rte) {
            logger.log(Level.SEVERE, "Error has occurred during analysis, will skip this repo.", rte);
        }
        return false;
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
