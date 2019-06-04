package reposense.git;

import static reposense.system.CommandRunner.runCommand;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

import reposense.model.RepoConfiguration;
import reposense.system.LogsManager;

/**
 * Contains git remote related functionalities.
 * Git remote is responsible to manage the set of tracked repositories.
 */
public class GitRemote {

    private static final Logger logger = LogsManager.getLogger(GitRemote.class);
    private static final String MESSAGE_REMOTE_DOES_NOT_EXIST =
            "%s does not have a remote url, will not update the location.";

    /**
     * Extracts the remote url from the cloned local repository
     */
    public static String getRemoteUrl(RepoConfiguration config) {
        Path rootPath = Paths.get(config.getLocation().toString());
        String command = "git remote get-url origin";

        try {
            return runCommand(rootPath, command).replace("\n", "");
        } catch (RuntimeException re) {
            logger.info(String.format(MESSAGE_REMOTE_DOES_NOT_EXIST, config.getLocation()));
            return null;
        }
    }
}
