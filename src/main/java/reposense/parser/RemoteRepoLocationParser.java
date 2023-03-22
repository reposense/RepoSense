package reposense.parser;

import static reposense.util.SystemUtil.isValidUrl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Contains remote repository location parsing functionalities specified to {@code author-config.csv} file.
 */
public class RemoteRepoLocationParser {
    // It is unlikely that a URL will contain "|" as they are typically escaped in a URL.
    private static final String LOCATION_BRANCH_DELIMITER = "|";
    private static final Pattern GITHUB_BRANCH_URL_PATTERN =
            Pattern.compile("(http|https)://github.com/(?<org>.+?)/(?<repoName>.+?)/tree/(?<branch>.+?)");

    /**
     * Given a string {@code locationAndBranch} representing a remote repository,
     * extract it into a 2-element array of the form {location, branch} based on its form.
     * Returns null if no additional special syntax was used.
     */
    public static String[] parseRemoteRepoLocation(String locationAndBranch) {
        if (isGithubBranchUrl(locationAndBranch)) {
            return parseGithubBranchUrl(locationAndBranch);
        } else if (isUsingDelimiter(locationAndBranch)) {
            return parseUrlWithDelimiter(locationAndBranch);
        } else {
            return null;
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
        int lastLocationBranchDelimiterIndex = locationAndBranch.lastIndexOf(LOCATION_BRANCH_DELIMITER);
        if (lastLocationBranchDelimiterIndex == -1) {
            return false;
        }

        String url = locationAndBranch.substring(0, lastLocationBranchDelimiterIndex);
        return isValidUrl(url);
    }

    /**
     * Parses a GitHub branch URL into a 2-element array of the form {location, branch}.
     */
    private static String[] parseGithubBranchUrl(String githubBranchUrl) {
        Matcher matcher = GITHUB_BRANCH_URL_PATTERN.matcher(githubBranchUrl);
        matcher.matches();

        String org = matcher.group("org");
        String repoName = matcher.group("repoName");
        String branch = matcher.group("branch");
        String location = "https://github.com/" + org + "/" + repoName + ".git";
        return new String[]{location, branch};
    }

    /**
     * Parses a URL that uses the delimiter syntax into a 2-element array of
     * the form {location, branch}.
     */
    private static String[] parseUrlWithDelimiter(String locationAndBranch) {
        int lastLocationBranchDelimiterIndex = locationAndBranch.lastIndexOf(LOCATION_BRANCH_DELIMITER);
        String location = locationAndBranch.substring(0, lastLocationBranchDelimiterIndex);
        String branch = locationAndBranch.substring(lastLocationBranchDelimiterIndex + 1);
        return new String[] { location, branch };
    }
}
