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
    public static void clone(RepoConfiguration repoConfig)
            throws GitCloneException {
        try {
            FileUtil.deleteDirectory(repoConfig.getRepoRoot());
            logger.info("Cloning from " + repoConfig.getLocation() + "...");
            clone(repoConfig.getLocation(), repoConfig.getRepoFolderName(), repoConfig.getRepoName(), "");
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
     * Clones a repo given the repo location into a directory.
     * @throws IOException if it fails to create a directory.
     */
    private static void clone(RepoLocation location, String repoFolderName, String outputFolderName,
            String additionalCommand) throws IOException {
        Path rootPath = Paths.get(FileUtil.REPOS_ADDRESS, repoFolderName);
        Files.createDirectories(rootPath);
        String command =
                String.format("git clone %s %s %s", additionalCommand, addQuote(location.toString()), outputFolderName);
        runCommand(rootPath, command);
    }

    public static void cloneFromBareAndUpdateBranch(Path rootPath, Path clonedBareRepoLocation,
            String outputFolderName, String targetBranch) throws IOException {
        Path relativePath = rootPath.relativize(clonedBareRepoLocation);
        FileUtil.deleteDirectory(Paths.get(FileUtil.REPOS_ADDRESS, outputFolderName).toString());
        String command = String.format("git clone %s --branch %s %s", relativePath, targetBranch,
                outputFolderName);
        runCommand(rootPath, command);
    }
}
