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
    public static final String FINDING_PREVIOUS_AUTHORS_INVALID_VERSION_ERROR_MESSAGE =
            "RepoSense's Finding Previous Authors feature requires git version 2.23 and above";

    /** Regex for matching Git version 2.23 and above */
    private static final Pattern FINDING_PREVIOUS_AUTHORS_VALID_GIT_VERSION_PATTERN =
            Pattern.compile("(((2\\d*\\.(2[3-9]\\d*|[3-9]\\d+|\\d{3,}))|((([3-9]\\d*)|(1\\d+))\\.\\d+))\\.\\d*)");

    /**
     * Returns a boolean indicating whether the current user has a version valid for running
     * Find Previous Authors functionality in RepoSense.
     */
    public static boolean isGitVersionSufficientForFindingPreviousAuthors() {
        Path rootPath = Paths.get("/");
        String versionCommand = "git --version";

        Matcher validGitVersionMatcher =  FINDING_PREVIOUS_AUTHORS_VALID_GIT_VERSION_PATTERN
                .matcher(runCommand(rootPath, versionCommand));

        return validGitVersionMatcher.find();
    }
}
