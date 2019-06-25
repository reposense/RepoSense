package reposense.git;

import static reposense.system.CommandRunner.runCommand;
import static reposense.util.StringsUtil.addQuote;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

import reposense.git.exception.GitBranchException;
import reposense.git.exception.GitCloneException;
import reposense.model.RepoConfiguration;
import reposense.system.LogsManager;
import reposense.util.FileUtil;

/**
 * Contains git clone related functionalities.
 * Git clone is responsible for cloning a local/remote repository into a new directory.
 */
public class GitClone {
    private static final Logger logger = LogsManager.getLogger(GitClone.class);

    /**
     * Returns the command to clone a bare repo specified in the {@code repoConfig}
     * into the folder {@code outputFolderName}.
     */
    public static String getCloneBareCommand(RepoConfiguration repoConfig, String outputFolderName)
            throws IOException {
        FileUtil.deleteDirectory(outputFolderName);
        return ("git clone --bare " + repoConfig.getLocation() + " " + outputFolderName);
    }

    /**
     * Clones repo specified in the {@code repoConfig} and updates it with the branch info.
     */
    public static void clone(RepoConfiguration repoConfig) throws GitCloneException {
        try {
            FileUtil.deleteDirectory(repoConfig.getRepoRoot());
            logger.info("Cloning from " + repoConfig.getLocation() + "...");

            Path rootPath = Paths.get(FileUtil.REPOS_ADDRESS, repoConfig.getRepoFolderName());
            Files.createDirectories(rootPath);
            String command = String.format("git clone %s %s", addQuote(repoConfig.getLocation().toString()),
                    repoConfig.getRepoName());
            runCommand(rootPath, command);

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
            repoConfig.updateBranch();
            GitCheckout.checkout(repoConfig.getRepoRoot(), repoConfig.getBranch());
        } catch (GitBranchException gbe) {
            logger.log(Level.SEVERE,
                    "Exception met while trying to get current branch of repo. Analysis terminated.", gbe);
            throw new GitCloneException(gbe);
        } catch (RuntimeException rte) {
            logger.log(Level.SEVERE, "Branch does not exist! Analysis terminated.", rte);
            throw new GitCloneException(rte);
        }
    }

    /**
     * Clones a bare repo specified in {@code repoConfig} into the folder {@code outputFolderName}.
     *
     * @throws IOException if it fails to delete a directory.
     */
    public static void cloneBare(RepoConfiguration repoConfig, String outputFolderName) throws IOException {
        Path rootPath = Paths.get(FileUtil.REPOS_ADDRESS, repoConfig.getRepoFolderName());
        FileUtil.deleteDirectory(Paths.get(rootPath.toString(), outputFolderName).toString());
        Files.createDirectories(rootPath);
        String command = String.format("git clone --bare %s %s", addQuote(repoConfig.getLocation().toString()),
                outputFolderName);
        runCommand(rootPath, command);
    }

    /**
     * Performs a full clone from {@code clonedBareRepoLocation} into the folder {@code outputFolderName} and
     * directly branches out to {@code targetBranch}.
     *
     * @throws IOException if it fails to delete a directory.
     */
    public static void cloneFromBareAndUpdateBranch(Path rootPath, Path clonedBareRepoLocation,
            String outputFolderName, String targetBranch) throws IOException {
        Path relativePath = rootPath.relativize(clonedBareRepoLocation);
        FileUtil.deleteDirectory(Paths.get(FileUtil.REPOS_ADDRESS, outputFolderName).toString());
        String command = String.format("git clone %s --branch %s %s", relativePath, targetBranch,
                outputFolderName);
        runCommand(rootPath, command);
    }
}
