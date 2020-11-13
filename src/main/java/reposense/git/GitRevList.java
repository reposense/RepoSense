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

    /**
     * Returns a list of commit hashes separated by newline that are within the range of {@code startHash} and
     * {@code endHash}. Both the {@code startHash} and {@code endHash} are guaranteed to be in the list.
     */
    public static String getCommitHashInRange(String root, String branchName, String startHash, String endHash) {
        if (startHash == null && endHash == null) {
            return "";
        } else if (startHash == null) {
            return endHash;
        } else if (endHash == null) {
            return startHash;
        }

        String fromStartHash = getAllCommitHashSince(root, branchName, startHash);
        String fromEndHash = getAllCommitHashSince(root, branchName, endHash);
        StringBuilder output = new StringBuilder();

        // If invalid hashes were given, do not use the results obtained from rev-list
        if (fromStartHash.equals("") && fromEndHash.equals("")) {
            return "";
        } else if (fromStartHash.equals("")) {
            return endHash;
        } else if (fromEndHash.equals("")) {
            return startHash;
        }

        // Perform a set difference in the list of commits to get the commits within the given range, since both lists
        // will have the list of commits starting from the given commit to HEAD, hence this removes the overlap part.
        // Also ensure that both hashes are present in the final output
        if (fromStartHash.length() > fromEndHash.length()) {
            output.append(endHash);
            output.append(fromStartHash.substring(fromEndHash.length()));
        } else {
            output.append(startHash);
            output.append(fromEndHash.substring(fromStartHash.length()));
        }

        return output.toString();
    }

    /**
     * Returns a list of commit hashes separated by newline that exist since {@code hash} until HEAD.
     */
    private static String getAllCommitHashSince(String root, String branchName, String hash) {
        Path rootPath = Paths.get(root);
        String revListCommand = "git rev-list " + hash + "..HEAD " + branchName + REVISION_PATH_SEPARATOR;

        try {
            return runCommand(rootPath, revListCommand) + hash;
        } catch (RuntimeException rte) {
            // An invalid commit hash was provided
            return "";
        }
    }
}
