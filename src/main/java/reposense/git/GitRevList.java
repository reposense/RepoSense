package reposense.git;

import static reposense.system.CommandRunner.runCommand;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

/**
 * Contains git rev list related functionalities.
 * Git rev list is capable of showing commit objects in reverse chronological order.
 */
public class GitRevList {

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
                + GitUtil.GIT_LOG_SINCE_DATE_FORMAT.format(date) + " " + branchName;
        return runCommand(rootPath, revListCommand);
    }
}
