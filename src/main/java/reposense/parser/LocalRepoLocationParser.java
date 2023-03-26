package reposense.parser;

import static reposense.parser.AuthorConfigLocationParser.LOCATION_BRANCHES_DELIMITER;
import static reposense.parser.AuthorConfigLocationParser.SPLIT_SAFE_BRANCHES_DELIMITER;
import static reposense.util.FileUtil.pathExists;
import static reposense.util.SystemUtil.isValidUrl;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains local repository location parsing functionalities specified to {@code author-config.csv} file.
 */
public class LocalRepoLocationParser {
    /**
     * Given a string {@code locationAndBranches} representing a local repository,
     * extract it into a list containing location and branch information based on its form.
     * Returns null if no additional special syntax was used.
     * The first item in the list is the location, while subsequent items are the
     * branches specified.
     */
    public static List<String> parseLocalRepoLocation(String locationAndBranches, String defaultSpecifiedBranch) {
        assert !isValidUrl(locationAndBranches);

        if (isUsingDelimiter(locationAndBranches)) {
            return parseLocalWithDelimiter(locationAndBranches);
        } else {
            return parseLocal(locationAndBranches, defaultSpecifiedBranch);
        }
    }

    /**
     * Parses a local repository path into a list containing location and branch information.
     * The first item in the list is the location, while subsequent items are the
     * branches specified.
     * <br><br>
     * The path to the repo can contain several ~ characters as well. The last ~ character will
     * be used to parse the path into the array.
     * <br><br>
     * Example: home~Desktop~RepoSense~master is parsed to give { home~Desktop~RepoSense, master }
     */
    private static List<String> parseLocalWithDelimiter(String locationAndBranches) {
        List<String> parsedLocationAndBranches = new ArrayList<>();
        int lastLocationBranchesDelimiterIndex = locationAndBranches.lastIndexOf(
                LOCATION_BRANCHES_DELIMITER);

        String location = locationAndBranches.substring(0, lastLocationBranchesDelimiterIndex);
        parsedLocationAndBranches.add(location);

        String[] branches = locationAndBranches
                .substring(lastLocationBranchesDelimiterIndex + 1).split(SPLIT_SAFE_BRANCHES_DELIMITER);
        for (String branch : branches) {
            parsedLocationAndBranches.add(branch);
        }

        if (branches.length == 0) { // by default, an empty branch is used
            parsedLocationAndBranches.add("");
        }

        return parsedLocationAndBranches;
    }

    /**
     * Parses a local repository path that is not using any additional special syntax
     * into a 2-element list containing location and branch information.
     * The first item in the list is the location, while the second item is the default branch
     * from {@code author-config.csv} branch column.
     */
    private static List<String> parseLocal(String location, String defaultSpecifiedBranch) {
        List<String> parsedLocationAndBranch = new ArrayList<>();
        parsedLocationAndBranch.add(location);
        parsedLocationAndBranch.add(defaultSpecifiedBranch);
        return parsedLocationAndBranch;
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

        return locationAndBranch.lastIndexOf(LOCATION_BRANCHES_DELIMITER) != -1;
    }
}
