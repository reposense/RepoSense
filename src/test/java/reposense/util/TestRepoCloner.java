package reposense.util;

import static reposense.system.CommandRunner.runCommand;
import static reposense.util.StringsUtil.addQuotesForFilePath;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

import reposense.git.GitCheckout;
import reposense.git.exception.GitBranchException;
import reposense.model.RepoConfiguration;
import reposense.system.LogsManager;

public class TestRepoCloner {
    private static final Logger logger = LogsManager.getLogger(TestRepoCloner.class);

    /**
     * Clones repo specified in the {@code config} and updates it with the branch info.
     *
     * @throws GitBranchException when an error occurs while attempting to branch.
     */
    public static void cloneAndBranch(RepoConfiguration config) throws GitBranchException {
        String outputFolderName = Paths.get(FileUtil.getRepoParentFolder(config).toString(),
                config.getRepoName()).toString();
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
     * Constructs the command to clone a repo specified in the {@code config} into the folder {@code outputFolderName}.
     */
    private static String getCloneCommand(RepoConfiguration config, String outputFolderName) {
        return "git clone " + addQuotesForFilePath(config.getLocation().toString()) + " "
                + addQuotesForFilePath(outputFolderName);
    }
}
