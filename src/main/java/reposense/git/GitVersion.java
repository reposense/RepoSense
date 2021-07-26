package reposense.git;

import static reposense.system.CommandRunner.runCommand;

import reposense.util.StringsUtil;

import java.nio.file.Path;
import java.nio.file.Paths;

public class GitVersion {
    /** Regex for matching Git version 2.23 and above */
    public static final String VALID_GIT_VERSION_FINDING_PREVIOUS_AUTHORS =
            "git version ((2\\d*\\.(2[3-9]\\d*|[3-9]\\d+|\\d{3,})|(([3-9]\\d*)|(1\\d+)\\.\\d+))\\.\\d*)";
    public static final String FINDING_PREVIOUS_AUTHORS_INVALID_VERSION_ERROR_MESSAGE =
            "RepoSense's Finding Previous Authors feature requires git version 2.23 and above";

    public static boolean isGitVersionSufficientForFindingPreviousAuthors() {
        Path rootPath = Paths.get("/");
        String versionCommand = "git --version";

        return !StringsUtil.filterText(runCommand(rootPath, versionCommand),
                VALID_GIT_VERSION_FINDING_PREVIOUS_AUTHORS).isEmpty();
    }
}
