package reposense.parser;

import static reposense.util.SystemUtil.isValidUrl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Contains functionality used to extract repo location details from a repo url or branch url.
 */
public class UrlLocationParser {

    private static final Pattern GIT_REPOSITORY_LOCATION_PATTERN =
            Pattern.compile("^.*github.com\\/(?<org>.+?)\\/(?<repoName>.+?)\\.git(#(?<branch>.+))?$");
    private static final Pattern GITHUB_BRANCH_URL_PATTERN =
            Pattern.compile("(http|https)://github.com/(?<org>.+?)/(?<repoName>.+?)/tree/(?<branch>.+?)");

    /**
     * Parses a given repo URL and returns an array containing the following info:
     * { url, repository name, organisation name, branch name (if any) }
     *
     * @return null if the given String is an invalid URL, or does not match the
     *         format of a GitHub repo URL.
     */
    public static String[] tryParsingAsRepoUrl(String repoUrl) {
        return tryParsingAsUrl(GIT_REPOSITORY_LOCATION_PATTERN, repoUrl);
    }

    /**
     * Parses a given branch URL and returns an array containing the following info:
     * { url, repository name, organisation name, branch name (if any) }
     *
     * @return null if the given String is an invalid URL, or does not match the
     *         format of a branch URL.
     */
    public static String[] tryParsingAsBranchUrl(String branchUrl) {
        return tryParsingAsUrl(GITHUB_BRANCH_URL_PATTERN, branchUrl);
    }

    /**
     * Parses a given URL and returns an array containing the following info:
     * { url, repository name, organisation name, branch name (if any) }
     *
     * @param urlPattern a Pattern that matches either the URL of a repo on GitHub,
     *        or the URL of a repo's branch on GitHub
     * @param url a String that may contain a url
     *
     * @return null if the given String is an invalid URL, or does not match the {@code urlPattern}.
     */
    private static String[] tryParsingAsUrl(Pattern urlPattern, String url) {
        Matcher matcher = urlPattern.matcher(url);
        if (!isValidUrl(url) || !matcher.matches()) {
            return null;
        }
        String org = matcher.group("org");
        String repoName = matcher.group("repoName");
        String branch = matcher.group("branch");
        String location = createRepoUrl(org, repoName);
        return new String[] { location, repoName, org, branch };
    }

    private static String createRepoUrl(String org, String repoName) {
        return "https://github.com/" + org + "/" + repoName + ".git";
    }
}
