package reposense.git;

import static reposense.system.CommandRunner.runCommand;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Contains git version related functionalities.
 * Git version is responsible for finding out the version of git the user of RepoSense is running.
 */
public class GitVersion {
    public static final String FINDING_PREVIOUS_AUTHORS_INVALID_VERSION_WARNING_MESSAGE =
            "--find-previous-authors/-F requires git version 2.23 and above. Feature will be disabled for this run";

    /** Regex for matching Git version 2.23 and above */
    public static final Pattern FINDING_PREVIOUS_AUTHORS_VALID_GIT_VERSION_PATTERN =
            Pattern.compile("(((2\\d*\\.(2[3-9]\\d*|[3-9]\\d+|\\d{3,}))|((([3-9]\\d*)|(1\\d+))\\.\\d+))\\.\\d*)");

    /**
     * Get current git version of RepoSense user
     */
    public static String getGitVersion() {
        Path rootPath = Paths.get("/");
        String versionCommand = "git --version";

        return runCommand(rootPath, versionCommand);
    }

    /**
     * Returns a boolean indicating whether the current user has a version valid for running
     * Find Previous Authors functionality in RepoSense.
     */
    public static boolean isGitVersionSufficientForFindingPreviousAuthors() {
        return FINDING_PREVIOUS_AUTHORS_VALID_GIT_VERSION_PATTERN.matcher(getGitVersion()).find();
    }
}
