package reposense.model;

import static reposense.util.FileUtil.fileExists;
import static reposense.util.SystemUtil.isValidUrl;

import java.nio.file.Paths;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import reposense.parser.InvalidLocationException;

/**
 * Represents a repository location.
 */
public class RepoLocation {
    public static final String MESSAGE_INVALID_LOCATION = "The given location is invalid";
    private static final String GIT_LINK_SUFFIX = ".git";
    private static final String BRANCH_DELIMITER = "#";
    private static final Pattern GIT_REPOSITORY_LOCATION_PATTERN =
            Pattern.compile("^.*github.com\\/(?<org>.+?)\\/(?<repoName>.+?)\\.git(#(?<branch>.+))?$");
    private static final Pattern GITHUB_BRANCH_URL_PATTERN =
            Pattern.compile("(http|https)://github.com/(?<org>.+?)/(?<repoName>.+?)/tree/(?<branch>.+?)");

    private static final int LOCATION_INDEX = 0;
    private static final int REPO_NAME_INDEX = 1;
    private static final int ORG_INDEX = 2;
    private static final int BRANCH_NAME_INDEX = 3;

    private final String location;
    private final transient Optional<String> parsedBranch;
    private final String repoName;
    private String organization;

    /**
     * @throws InvalidLocationException if {@code location} cannot be represented by a {@code URL} or {@code Path}.
     */
    public RepoLocation(String location) throws InvalidLocationException {
        this(parse(location));
    }

    private RepoLocation(String[] repoLocationDetails) {
        assert repoLocationDetails.length == 4;
        location = repoLocationDetails[LOCATION_INDEX];
        repoName = repoLocationDetails[REPO_NAME_INDEX];
        organization = repoLocationDetails[ORG_INDEX];
        parsedBranch = Optional.ofNullable(repoLocationDetails[BRANCH_NAME_INDEX]);
    }

    public boolean isEmpty() {
        return location.isEmpty();
    }

    public String getRepoName() {
        return repoName;
    }

    public String getOrganization() {
        return organization;
    }

    public Optional<String> getParsedBranch() {
        return parsedBranch;
    }

    /**
     * Given a String representing a repo location, returns
     * an array containing the following details of the repository (in order):
     * { location, repository name, organisation name, branch name (if any) }
     *
     * @param location a repository location, which is a file path or URL with the branch
     *         name optionally appended
     * @throws InvalidLocationException if the repo location is an invalid path or an invalid URL
     */
    private static String[] parse(String location) throws InvalidLocationException {
        String[] parsedInfo = tryParsingAsRepoUrl(location);
        if (parsedInfo != null) {
            return parsedInfo;
        }

        parsedInfo = tryParsingAsPath(location);
        if (parsedInfo != null) {
            return parsedInfo;
        }

        parsedInfo = tryParsingAsBranchUrl(location);
        if (parsedInfo != null) {
            return parsedInfo;
        }

        throw new InvalidLocationException(MESSAGE_INVALID_LOCATION);
    }

    /**
     * Parses a given path to a repo and returns an array containing the following info:
     * { url, repository name, organisation name, branch name (if any) }
     *
     * @return null if the given String is an invalid path, or no directory exists at the path.
     */
    private static String[] tryParsingAsPath(String location)  {
        String[] split = location.split(BRANCH_DELIMITER);
        String filePath = split[0];
        if (!fileExists(filePath)) {
            return null;
        }
        String repoName = Paths.get(location).getFileName().toString().replace(GIT_LINK_SUFFIX, "");
        String branch = split.length == 1 ? null : split[1];
        return new String[] { filePath, repoName, null, branch };
    }

    /**
     * Parses a given repo URL and returns an array containing the following info:
     * { url, repository name, organisation name, branch name (if any) }
     *
     * @return null if the given String is an invalid URL, or does not match the
     *         format of a GitHub repo URL.
     */
    private static String[] tryParsingAsRepoUrl(String repoUrl) {
        return tryParsingAsUrl(GIT_REPOSITORY_LOCATION_PATTERN, repoUrl);
    }

    /**
     * Parses a given branch URL and returns an array containing the following info:
     * { url, repository name, organisation name, branch name (if any) }
     *
     * @return null if the given String is an invalid URL, or does not match the
     *         format of a branch URL.
     */
    private static String[] tryParsingAsBranchUrl(String branchUrl) {
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

    @Override
    public String toString() {
        return location;
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (this == other) {
            return true;
        }

        // instanceof handles null
        if (!(other instanceof RepoLocation)) {
            return false;
        }

        RepoLocation otherLocation = (RepoLocation) other;
        return this.location.equals(otherLocation.location);
    }

    @Override
    public int hashCode() {
        return location.hashCode();
    }

    private static String createRepoUrl(String org, String repoName) {
        return "https://github.com/" + org + "/" + repoName + GIT_LINK_SUFFIX;
    }
}
