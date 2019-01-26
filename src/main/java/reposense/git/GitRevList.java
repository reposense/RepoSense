package reposense.git;

import static reposense.system.CommandRunner.runCommand;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

/**
 * Contains git rev list related functionalities.
 * Git rev list is responsible for showing commit objects in reverse chronological order.
 */
public class GitRevList {

    private static final String REVISION_PATH_SEPARATOR = " -- ";

    /**
     * Returns the latest commit hash before {@code date}.
     * Returns an empty {@code String} if {@code date} is null, or there is no such commit.
     */
    public static String getCommitHashBeforeDate(String root, String branchName, Date date) {
        if (date == null) {
            return "";
        }

        Path rootPath = Paths.get(root);
        String revListCommand = "git rev-list -1 --before="
                + GitUtil.GIT_LOG_SINCE_DATE_FORMAT.format(date) + " " + branchName + REVISION_PATH_SEPARATOR;
        return runCommand(rootPath, revListCommand);
    }

    /**
     * Returns the latest commit hash inclusive and until the end of the day of {@code date}.
     * Returns an empty {@code String} if {@code date} is null, or there is no such commit.
     */
    public static String getCommitHashUntilDate(String root, String branchName, Date date) {
        if (date == null) {
            return "";
        }

        Path rootPath = Paths.get(root);
        String revListCommand = "git rev-list -1 --before="
                + GitUtil.GIT_LOG_UNTIL_DATE_FORMAT.format(date) + " " + branchName + REVISION_PATH_SEPARATOR;
        return runCommand(rootPath, revListCommand);
    }
}
