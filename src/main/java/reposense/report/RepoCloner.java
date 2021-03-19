package reposense.report;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import reposense.git.GitBranch;
import reposense.git.GitCatFile;
import reposense.git.GitClone;
import reposense.git.GitRevList;
import reposense.git.GitShow;
import reposense.git.exception.CommitNotFoundException;
import reposense.git.exception.GitBranchException;
import reposense.git.exception.GitCloneException;
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
    private static final String MESSAGE_START_CLONING_SHALLOW = "Shallow cloning in parallel from %s...";
    private static final String MESSAGE_ERROR_CLONING_SHALLOW =
            "Exception met while running shallow clone of repo \"%s\".";
    private static final String MESSAGE_START_CLONING_PARTIAL = "Partial cloning from %s...";
    private static final String MESSAGE_ERROR_CLONING_PARTIAL =
            "Exception met while running partial clone of repo \"%s\".";
    private static final String MESSAGE_START_CLONING_SHALLOW_PARTIAL = "Shallow partial cloning from %s...";
    private static final String MESSAGE_ERROR_CLONING_SHALLOW_PARTIAL =
            "Exception met while running shallow partial clone of repo \"%s\".";
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

        if (!config.isShallowCloningPerformed()) {
            isCurrentRepoCloned = spawnCloneProcess(config);
        } else {
            boolean didShallowPartialCloneSucceed = spawnShallowPartialCloneProcess(config);
            String shallowPartialBareRoot = FileUtil.getShallowPartialBareRepoPath(config).toString();

            if (!didShallowPartialCloneSucceed || GitRevList.checkIsEmptyRepo(shallowPartialBareRoot)) {
                isCurrentRepoCloned = spawnCloneProcess(config);
                return;
            }

            List<String> graftedCommits = GitRevList.getRootCommits(shallowPartialBareRoot);
            List<String> graftedCommitParents = GitCatFile.getParentsOfCommits(shallowPartialBareRoot, graftedCommits);

            boolean didPartialCloneSucceed = spawnPartialCloneProcess(config);
            if (!didPartialCloneSucceed) {
                isCurrentRepoCloned = spawnCloneProcess(config);
                return;
            }

            String partialBareRoot = FileUtil.getPartialBareRepoPath(config).toString();
            Date sinceDate;
            try {
                List<String> distinctParents = graftedCommitParents.stream().distinct().collect(Collectors.toList());
                sinceDate = GitShow.getEarliestCommitDate(partialBareRoot, distinctParents);
            } catch (CommitNotFoundException e) {
                sinceDate = null;
            }
            if (sinceDate != null) {
                isCurrentRepoCloned = spawnShallowCloneProcess(config, sinceDate);
            } else {
                isCurrentRepoCloned = spawnCloneProcess(config);
            }
        }
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
            FileUtil.deleteDirectory(FileUtil.getBareRepoPath(config).toString());
            Path rootPath = Paths.get(FileUtil.REPOS_ADDRESS, config.getRepoFolderName());
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
     * Spawns a process to shallow clone repo specified in {@code config}.
     * Does not wait for process to finish executing.
     * Should only handle a maximum of one spawned process at any time.
     */
    private boolean spawnShallowCloneProcess(RepoConfiguration config, Date shallowSinceDate) {
        assert(crp == null);

        try {
            FileUtil.deleteDirectory(FileUtil.getBareRepoPath(config).toString());
            Path rootPath = Paths.get(FileUtil.REPOS_ADDRESS, config.getRepoFolderName());
            Files.createDirectories(rootPath);

            logger.info(String.format(MESSAGE_START_CLONING_SHALLOW, config.getLocation()));
            crp = GitClone.cloneShallowBareAsync(config, rootPath, FileUtil.getBareRepoFolderName(config),
                    shallowSinceDate);
        } catch (GitCloneException | IOException e) {
            logger.log(Level.WARNING, String.format(MESSAGE_ERROR_CLONING_SHALLOW, config.getDisplayName()), e);
            return false;
        }
        return true;
    }

    /**
     * Spawns a process to create partial clone of repo specified in {@code config}.
     * Waits for process to finish executing.
     */
    private boolean spawnPartialCloneProcess(RepoConfiguration config) {
        assert(crp == null);

        try {
            FileUtil.deleteDirectory(FileUtil.getPartialBareRepoPath(config).toString());
            Path rootPath = Paths.get(FileUtil.REPOS_ADDRESS, config.getRepoFolderName());
            Files.createDirectories(rootPath);

            logger.info(String.format(MESSAGE_START_CLONING_PARTIAL, config.getLocation()));
            GitClone.clonePartialBare(config, rootPath, FileUtil.getPartialBareRepoFolderName(config));
        } catch (GitCloneException | IOException e) {
            logger.log(Level.WARNING, String.format(MESSAGE_ERROR_CLONING_PARTIAL, config.getDisplayName()), e);
            return false;
        }
        return true;
    }

    /**
     * Spawns a process to create shallow partial clone of repo specified in {@code config}.
     * Waits for process to finish executing.
     */
    private boolean spawnShallowPartialCloneProcess(RepoConfiguration config) {
        assert(crp == null);

        try {
            FileUtil.deleteDirectory(FileUtil.getShallowPartialBareRepoPath(config).toString());
            Path rootPath = Paths.get(FileUtil.REPOS_ADDRESS, config.getRepoFolderName());
            Files.createDirectories(rootPath);

            logger.info(String.format(MESSAGE_START_CLONING_SHALLOW_PARTIAL, config.getLocation()));
            GitClone.cloneShallowPartialBare(config, rootPath, FileUtil.getShallowPartialBareRepoFolderName(config),
                    config.getSinceDate());
        } catch (GitCloneException | IOException e) {
            logger.log(Level.WARNING, String.format(MESSAGE_ERROR_CLONING_SHALLOW_PARTIAL, config.getDisplayName()), e);
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
