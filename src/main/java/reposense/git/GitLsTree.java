package reposense.git;

import static reposense.system.CommandRunner.runCommand;

import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import reposense.git.exception.InvalidFilePathException;
import reposense.model.RepoConfiguration;
import reposense.system.LogsManager;
import reposense.util.StringsUtil;
import reposense.util.SystemUtil;

/**
 * Contains git ls tree related functionalities.
 * Git ls tree is responsible to obtain the list of staged files of a branch.
 */
public class GitLsTree {

    /**
     * Returns an Array of {@code String} containing file paths of all tracked files.
     */
    public static String[] getFilePaths(Path clonedRepoDirectory, RepoConfiguration config) {
        String command = "git ls-tree --name-only -r " + config.getBranch();

        return runCommand(clonedRepoDirectory, command).split("\n");
    }
}
