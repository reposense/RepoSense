package reposense.parser;

import static reposense.util.FileUtil.pathExists;

/**
 * Contains local repository location parsing functionalities specified to {@code author-config.csv} file.
 */
public class LocalRepoLocationParser {
    private static final String LOCATION_BRANCH_DELIMITER = "|";

    /**
     * Given a string {@code locationAndBranch} representing a local repository,
     * extract it into a 2-element array of the form {location, branch} based on its form.
     * Returns null if no additional special syntax was used.
     */
    public static String[] parseLocalRepoLocation(String locationAndBranch) {
        if (isUsingDelimiter(locationAndBranch)) {
            return parseLocalWithDelimiter(locationAndBranch);
        } else {
            return null;
        }
    }

    /**
     * Parses a local repository path into a 2-element array of the form {location, branch}.
     * The path to the repo can contain several | characters as well. The last | character will
     * be used to parse the path into the array.
     * Example: home|Desktop|RepoSense|master is parsed to give { home|Desktop|RepoSense, master }
     */
    private static String[] parseLocalWithDelimiter(String locationAndBranch) {
        int lastLocationBranchDelimiterIndex = locationAndBranch.lastIndexOf(LOCATION_BRANCH_DELIMITER);
        String location = locationAndBranch.substring(0, lastLocationBranchDelimiterIndex);
        String branch = locationAndBranch.substring(lastLocationBranchDelimiterIndex + 1);
        return new String[] { location, branch };
    }

    /**
     * Returns true if the given {@code locationAndBranch} is using the delimiter syntax.
     * If the given {@code locationAndBranch} exists, then it is considered to not be
     * using the delimiter syntax.
     */
    private static boolean isUsingDelimiter(String locationAndBranch) {
        if (pathExists(locationAndBranch)) {
            return false;
        }

        return locationAndBranch.lastIndexOf(LOCATION_BRANCH_DELIMITER) != -1;
    }
}
