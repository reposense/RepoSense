package reposense.parser;

import static reposense.parser.AuthorConfigLocationParser.LOCATION_BRANCHES_DELIMITER;
import static reposense.parser.AuthorConfigLocationParser.SPLIT_SAFE_BRANCHES_DELIMITER;
import static reposense.util.SystemUtil.isValidUrl;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Contains remote repository location parsing functionalities specified to {@code author-config.csv} file.
 */
public class RemoteRepoLocationParser {
    private static final Pattern GITHUB_BRANCH_URL_PATTERN =
            Pattern.compile("(http|https)://github.com/(?<org>.+?)/(?<repoName>.+?)/tree/(?<branch>.+?)");

    /**
     * Given a string {@code locationAndBranch} representing a remote repository,
     * extract it into a list containing location and branch information based on its form.
     * Returns null if no additional special syntax was used.
     */
    public static List<String> parseRemoteRepoLocation(String locationAndBranches, String defaultSpecifiedBranch) {
        assert isValidUrl(locationAndBranches);

        if (isGithubBranchUrl(locationAndBranches)) {
            return parseGithubBranchUrl(locationAndBranches);
        } else if (isUsingDelimiter(locationAndBranches)) {
            return parseUrlWithDelimiter(locationAndBranches);
        } else {
            return parseUrl(locationAndBranches, defaultSpecifiedBranch);
        }
    }

    /**
     * Returns true if the given {@code url} is a GitHub branch URL.
     */
    private static boolean isGithubBranchUrl(String url) {
        Matcher matcher = GITHUB_BRANCH_URL_PATTERN.matcher(url);
        return matcher.matches();
    }

    /**
     * Returns true if the given {@code locationAndBranch} is a URL and is also using
     * the location and branch delimiter syntax.
     */
    private static boolean isUsingDelimiter(String locationAndBranch) {
        int lastLocationBranchDelimiterIndex = locationAndBranch.lastIndexOf(LOCATION_BRANCHES_DELIMITER);
        if (lastLocationBranchDelimiterIndex == -1) {
            return false;
        }

        String url = locationAndBranch.substring(0, lastLocationBranchDelimiterIndex);
        return isValidUrl(url);
    }

    /**
     * Parses a GitHub branch URL into a 2-element list of the form {location, branch}.
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

    /**
     * Parses a URL that uses the delimiter syntax into a list containing
     * location and branch information.
     */
    private static List<String> parseUrlWithDelimiter(String locationAndBranches) {
        List<String> parsedLocationAndBranches = new ArrayList<>();
        int lastLocationBranchesDelimiterIndex = locationAndBranches.lastIndexOf(LOCATION_BRANCHES_DELIMITER);

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
     * Parses a remote repository URL that is not using any additional special syntax
     * into a 2-element list containing location and branch information.
     * The first item in the list is the location, while the second item is the default branch
     * from {@code author-config.csv} branch column.
     */
    private static List<String> parseUrl(String location, String defaultSpecifiedBranch) {
        List<String> parsedLocationAndBranch = new ArrayList<>();
        parsedLocationAndBranch.add(location);
        parsedLocationAndBranch.add(defaultSpecifiedBranch);
        return parsedLocationAndBranch;
    }
}
