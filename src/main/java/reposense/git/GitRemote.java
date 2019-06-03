package reposense.git;

import static reposense.system.CommandRunner.runCommand;

import java.nio.file.Path;
import java.nio.file.Paths;

import reposense.model.RepoConfiguration;

/**
 * Contains git remote related functionalities.
 * Git remote is responsible to manage the set of tracked repositories.
 */
public class GitRemote {

    /**
     * Extracts the remote url from the cloned local repository
     */
    public static String getRemoteUrl(RepoConfiguration config) {
        Path rootPath = Paths.get(config.getLocation().toString());
        String command = "git remote get-url origin";

        try {
            return runCommand(rootPath, command).replace("\n", "");
        } catch (RuntimeException re) {
            return null;
        }
    }
}
