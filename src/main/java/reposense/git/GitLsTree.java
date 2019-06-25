package reposense.git;

import static reposense.system.CommandRunner.runCommand;

import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import reposense.git.exception.InvalidFilePathException;
import reposense.model.RepoConfiguration;
import reposense.system.LogsManager;
import reposense.util.FileUtil;
import reposense.util.StringsUtil;
import reposense.util.SystemUtil;

/**
 * Contains git ls tree related functionalities.
 * Git ls tree is responsible to obtain the list of staged files of a branch.
 */
public class GitLsTree {

    private static final Logger logger = LogsManager.getLogger(GitLsTree.class);
    private static final String MESSAGE_INVALID_PATH = "Invalid filepath: %s contains %s";

    // Although forward-slash (/) is an invalid character in Windows file path, it is not used as the forward-slash in
    // the output of git-ls-tree is ambiguous. i.e. /path/to/test\file or /path/contains:a/file.
    // Also, it is not possible to create and commit such a file on Unix systems.
    private static final Pattern ILLEGAL_WINDOWS_CHARACTER_PATTERN = Pattern.compile("[:\\\\*?|<>:\"]");

    /**
     * Clones a bare repo in {@code repoConfig} and verifies that the repo contains only file paths that are
     * compatible in Windows.
     *
     * @throws IOException if it fails to create/delete the folders.
     * @throws InvalidFilePathException if the repository contains invalid file paths that are not compatible with
     * Windows.
     */
    public static void validateFilePaths(RepoConfiguration repoConfig) throws IOException, InvalidFilePathException {
        GitClone.cloneBare(repoConfig, FileUtil.getBareRepoFolderName(repoConfig));
        validateFilePaths(repoConfig, FileUtil.getBareRepoPath(repoConfig));
    }

    /**
     * Verifies that the repository in {@code config} contains only file paths that are compatible with Windows.
     * Skips check if the operating system is not Windows.
     *
     * @throws InvalidFilePathException if the repository contains invalid file paths that are not compatible with
     * Windows.
     */
    public static void validateFilePaths(RepoConfiguration config, Path clonedBareRepoLocation)
            throws InvalidFilePathException {
        if (!SystemUtil.isWindows()) {
            return;
        }

        boolean hasError = false;
        String[] paths = getFilePaths(clonedBareRepoLocation, config);

        for (String path : paths) {
            path = StringsUtil.removeQuote(path);
            Matcher matcher = ILLEGAL_WINDOWS_CHARACTER_PATTERN.matcher(path);

            if (matcher.find()) {
                logger.log(Level.SEVERE, String.format(MESSAGE_INVALID_PATH, path, matcher.group()));
                hasError = true;
            }
        }

        if (hasError) {
            throw new InvalidFilePathException("Invalid file paths found in " + config.getLocation());
        }
    }

    /**
     * Returns an Array of {@code String} containing file paths of all tracked files.
     */
    private static String[] getFilePaths(Path clonedRepoLocation, RepoConfiguration config) {
        String command = "git ls-tree --name-only -r " + config.getBranch();

        return runCommand(clonedRepoLocation, command).split("\n");
    }
}
