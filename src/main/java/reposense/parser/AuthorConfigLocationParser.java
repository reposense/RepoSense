package reposense.parser;

import static reposense.parser.LocalRepoLocationParser.parseLocalRepoLocation;
import static reposense.parser.RemoteRepoLocationParser.parseRemoteRepoLocation;
import static reposense.util.SystemUtil.isValidUrl;

import java.util.List;

/**
 * Contains repository location parsing functionalities specified to {@code author-config.csv} file.
 */
public class AuthorConfigLocationParser {
    public static final String LOCATION_BRANCHES_DELIMITER = "~";
    public static final String BRANCHES_DELIMITER = "|";
    public static final String SPLIT_SAFE_BRANCHES_DELIMITER = "\\" + BRANCHES_DELIMITER;

    /**
     * Parse a location and returns a list containing location and branch information
     * according to whether it uses no additional special syntax, delimiter syntax or
     * GitHub-specific branch URL syntax.
     * The first item in the list is the location, while subsequent items are the
     * branches specified.
     */
    public static List<String> parseLocation(String location, String defaultSpecifiedBranch) {
        if (isRemoteRepo(location)) {
            return parseRemoteRepoLocation(location, defaultSpecifiedBranch);
        } else {
            return parseLocalRepoLocation(location, defaultSpecifiedBranch);
        }
    }

    private static boolean isRemoteRepo(String location) {
        // File URI schemes are considered to be remote repos.
        return isValidUrl(location);
    }
}
