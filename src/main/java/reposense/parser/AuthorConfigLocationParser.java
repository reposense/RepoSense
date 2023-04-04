package reposense.parser;

import static reposense.util.FileUtil.pathExists;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Contains repository location parsing functionalities specified to {@code author-config.csv} file.
 */
public class AuthorConfigLocationParser {
    public static final String LOCATION_BRANCHES_DELIMITER = "~";
    public static final String BRANCHES_DELIMITER = "|";
    // String::split requires escape characters
    public static final String SPLIT_SAFE_BRANCHES_DELIMITER = "\\" + BRANCHES_DELIMITER;
    private static final Pattern GITHUB_BRANCH_URL_PATTERN =
            Pattern.compile("(http|https)://github.com/(?<org>.+?)/(?<repoName>.+?)/tree/(?<branch>.+?)");

    /**
     * Parses a repository location and returns a list containing location and branch information
     * according to whether it uses no additional special syntax, delimiter syntax or
     * GitHub-specific branch URL syntax.
     * The first item in the list is the location, while subsequent items are the
     * branches specified.
     */
    public static List<String> parseLocation(String location, String defaultSpecifiedBranch) {
        if (isGithubBranchUrl(location)) {
            return parseGithubBranchUrl(location);
        }

        if (isUsingDelimiter(location)) {
            return parseDelimiter(location);
        }

        return parseDefault(location, defaultSpecifiedBranch);
    }

    /**
     * Parses a repository location that is not using any additional special syntax
     * into a 2-element list containing location and branch information.
     * The first item in the list is the location, while the second item is the default branch
     * from {@code author-config.csv} branch column.
     */
    private static List<String> parseDefault(String location, String defaultSpecifiedBranch) {
        List<String> parsedLocationAndBranch = new ArrayList<>();
        parsedLocationAndBranch.add(location);
        parsedLocationAndBranch.add(defaultSpecifiedBranch);
        return parsedLocationAndBranch;
    }

    /**
     * Returns true if the given {@code locationAndBranch} is using the location and
     * branch delimiter syntax. If the given {@code locationAndBranch} is an actual
     * path, then it is considered to not be using the delimiter syntax.
     */
    private static boolean isUsingDelimiter(String locationAndBranch) {
        int lastLocationBranchDelimiterIndex = locationAndBranch.lastIndexOf(LOCATION_BRANCHES_DELIMITER);
        if (lastLocationBranchDelimiterIndex == -1) {
            return false;
        }

        return !pathExists(locationAndBranch);
    }

    /**
     * Parses a repository location into a list containing location and branch information.
     * The first item in the list is the location, while subsequent items are the
     * branches specified.
     * <br><br>
     * The repository location can contain several ~ or | characters as well. The last ~ character will
     * be used as the separator between the actual path and the specified branches.
     * <br><br>
     * Example: home~Desktop~RepoSense~master|cypress is parsed to give [home~Desktop~RepoSense, master, cypress]
     * <br><br>
     * There exist an obscure case where the delimiter syntax will not work due to conflicts in directory names.
     * See: <a href="https://github.com/reposense/RepoSense/pull/1961#discussion_r1155475445">PR #1961 Discussion</a>
     */
    private static List<String> parseDelimiter(String locationAndBranches) {
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

        if (branches.length == 0) {
            // by default, if no branches are specified, an empty branch is returned since
            // the only case where branches.length is 0 is when delimiter is used but no
            // branch is specified.
            parsedLocationAndBranches.add("");
        }

        return parsedLocationAndBranches;
    }

    /**
     * Returns true if the given {@code url} is a GitHub branch URL.
     */
    private static boolean isGithubBranchUrl(String url) {
        Matcher matcher = GITHUB_BRANCH_URL_PATTERN.matcher(url);
        return matcher.matches();
    }

    /**
     * Parses a repository location in the form of a GitHub branch URL into a
     * 2-element list of the form [location, branch].
     */
    private static List<String> parseGithubBranchUrl(String githubBranchUrl) {
        Matcher matcher = GITHUB_BRANCH_URL_PATTERN.matcher(githubBranchUrl);
        matcher.matches();

        String org = matcher.group("org");
        String repoName = matcher.group("repoName");
        String branch = matcher.group("branch");
        String location = "https://github.com/" + org + "/" + repoName + ".git";

        List<String> parsedLocation = new ArrayList<>();
        parsedLocation.add(location);
        parsedLocation.add(branch);
        return parsedLocation;
    }
}
