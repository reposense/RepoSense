package reposense.report;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import reposense.git.GitBranch;
import reposense.git.GitClone;
import reposense.git.GitCloneException;
import reposense.model.RepoConfiguration;
import reposense.system.LogsManager;

public class RepoCloner {
    private static final Logger logger = LogsManager.getLogger(RepoCloner.class);

    private static RepoConfiguration[] configs = new RepoConfiguration[2];
    private static int index = 0;
    private static int prevIndex = 0;
    private static boolean isCurrentRepoCloned = false;
    private static String prevRepoDefaultBranch;

    /**
     * Spawns a process to clone the repository specified by {@code config}.
     * Does not wait for process to finish executing.
     */
    public static void clone(String outputPath, RepoConfiguration config) throws IOException {
        configs[index] = config;
        isCurrentRepoCloned = true;
        if (isPreviousRepoDifferent()) {
            isCurrentRepoCloned = spawnCloneProcess(outputPath, config);
        }
    }

    /**
     * Waits for current clone process to finish executing and returns the corresponding {@code RepoConfiguration}.
     */
    public static RepoConfiguration getClonedRepo(String outputPath) throws IOException {
        if (isCurrentRepoCloned && isPreviousRepoDifferent()) {
            isCurrentRepoCloned = waitForCloneProcess(outputPath, configs[index]);
        }
        if (!isCurrentRepoCloned) {
            return null;
        }
        if (!isPreviousRepoDifferent()) {
            GitClone.updateRepoConfigBranch(configs[index], prevRepoDefaultBranch);
        } else {
            prevRepoDefaultBranch = GitBranch.getCurrentBranch(configs[index].getRepoRoot());
        }
        prevIndex = index;
        index = (index + 1) % configs.length;
        return configs[prevIndex];
    }

    /**
     * Returns true if current repo is different from the previously cloned repo.
     */
    private static boolean isPreviousRepoDifferent() {
        return prevIndex == index || !configs[prevIndex].getLocation().equals(configs[index].getLocation());
    }

    private static boolean spawnCloneProcess(String outputPath, RepoConfiguration config) throws IOException {
        try {
            GitClone.spawnCloneProcess(config);
            return true;
        } catch (GitCloneException gde) {
            logger.log(Level.WARNING,
                    "Exception met while trying to clone the repo, will skip this repo.", gde);
            ReportGenerator.generateEmptyRepoReport(outputPath, config);
        }
        return false;
    }

    private static boolean waitForCloneProcess(String outputPath, RepoConfiguration config) throws IOException {
        try {
            GitClone.waitForCloneProcess(config);
            return true;
        } catch (GitCloneException gde) {
            logger.log(Level.WARNING,
                    "Exception met while trying to clone the repo, will skip this repo.", gde);
            ReportGenerator.generateEmptyRepoReport(outputPath, config);
        } catch (RuntimeException rte) {
            logger.log(Level.SEVERE, "Error has occurred during analysis, will skip this repo.", rte);
        }
        return false;
    }
}
