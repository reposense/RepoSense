package reposense.util;

import static reposense.system.CommandRunner.runCommand;
import static reposense.util.StringsUtil.addQuotesForFilePath;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

import reposense.git.GitCheckout;
import reposense.git.exception.GitBranchException;
import reposense.model.RepoConfiguration;
import reposense.system.LogsManager;

/**
 * Contains cloning utility methods that help facilitate writing of test cases.
 */
public class TestRepoCloner {
    private static final Logger logger = LogsManager.getLogger(TestRepoCloner.class);

    /**
     * Clones repo specified in the {@code config} and updates it with the branch info.
     *
     * @throws GitBranchException when an error occurs while attempting to branch.
     */
    public static void cloneAndBranch(RepoConfiguration config) throws GitBranchException {
        cloneAndBranch(config, "");
    }

    /**
     * Clones repo specified in the {@code config} and updates it with the branch info. {@code extraOutputFolderName}
     * will be appended to the output folder name. If the original output folder is './aaa', it is now
     * './aaa/{@code extraOutputFolderName}'.
     *
     * @throws GitBranchException when an error occurs while attempting to branch.
     */
    public static void cloneAndBranch(RepoConfiguration config, String extraOutputFolderName) throws GitBranchException {
        String outputFolderName = Paths.get(FileUtil.getRepoParentFolder(config).toString(),
                config.getRepoName(), extraOutputFolderName).toString();
        clone(config, Paths.get("."), outputFolderName);
        config.updateBranch();
        GitCheckout.checkout(config.getRepoRoot(), config.getBranch());
    }

    /**
     * Clones repo specified in {@code config} from working directory at {@code rootPath} to {@code outputFolderName}.
     */
    public static void clone(RepoConfiguration config, Path rootPath, String outputFolderName) {
        if (Files.exists(Paths.get(outputFolderName))) {
            logger.info("Skipped cloning from " + config.getLocation() + " as it was cloned before.");
        } else {
            logger.info("Cloning from " + config.getLocation() + "...");
            String command = getCloneCommand(config, outputFolderName);
            runCommand(rootPath, command);
            logger.info("Cloning completed!");
        }
    }

    /**
     * Clones a bare repo, with {@code rootPath} as working directory, specified in {@code config}
     * into the folder {@code outputFolderName}.
     *
     * @throws IOException if it fails to delete or create a directory.
     */
    public static void cloneBare(RepoConfiguration config, Path rootPath, String outputFolderName) throws IOException {
        Path outputFolderPath = Paths.get(outputFolderName);
        if (Files.exists(outputFolderPath)) {
            return;
        }
        String command = getCloneBareCommand(config, outputFolderName);
        runCommand(rootPath, command);
    }

    /**
     * Constructs the command to clone a repo specified in the {@code config} into the folder {@code outputFolderName}.
     */
    private static String getCloneCommand(RepoConfiguration config, String outputFolderName) {
        return "git clone " + addQuotesForFilePath(config.getLocation().toString()) + " "
                + addQuotesForFilePath(outputFolderName);
    }

    /**
     * Constructs the command to clone a bare repo specified in the {@code config}
     * into the folder {@code outputFolderName}.
     */
    private static String getCloneBareCommand(RepoConfiguration config, String outputFolderName) {
        String output = "git clone --bare "
                + addQuotesForFilePath(config.getLocation().toString()) + " "
                + addQuotesForFilePath(outputFolderName);
        return output;
    }
}
