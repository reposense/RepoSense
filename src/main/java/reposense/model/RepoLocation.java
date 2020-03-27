package reposense.model;

import static reposense.util.FileUtil.fileExists;
import static reposense.util.SystemUtil.isValidUrl;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import reposense.parser.InvalidLocationException;

/**
 * Represents a repository location.
 */
public class RepoLocation {
    public static final Pattern GIT_REPOSITORY_LOCATION_PATTERN =
            Pattern.compile("^.*github.com\\/(?<org>.+?)\\/(?<repoName>.+?).git(\\/(?<branch>.*))?$");
    private static final String GIT_LINK_SUFFIX = ".git";
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
        location = repoLocationDetails[0];
        repoName = repoLocationDetails[1];
        organization = repoLocationDetails[2];
        parsedBranch = Optional.ofNullable(repoLocationDetails[3]);
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
     * @param location a repository location, which is a file path or UR with the branch
     *                 name optionally appended
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

        throw new InvalidLocationException("The given location is invalid");
    }

    /**
     * Parses a given repo URL and returns an array containing the following info:
     * { url, repository name, organisation name, branch name (if any }
     *
     * @return null if the given String is an invalid URL
     */
    private static String[] tryParsingAsRepoUrl(String location) {
        String[] split = extractBranch(location);
        String repoUrl = split[0];
        Matcher matcher = GIT_REPOSITORY_LOCATION_PATTERN.matcher(repoUrl);

        if (!isValidUrl(repoUrl) || !matcher.matches()) {
            return null;
        }

        String organization = matcher.group("org");
        String repoName = matcher.group("repoName");
        String parsedBranch = split[1];
        return new String[] { repoUrl, repoName, organization, parsedBranch };
    }

    /**
     * Parses a given path to a repo and returns an array containing the following info:
     * { url, repository name, organisation name, branch name (if any }
     *
     * @return null if the given String is an invalid path, or no directory exists at the path
     */
    private static String[] tryParsingAsPath(String location)  {
        String[] split = extractBranch(location);
        String filePath = split[0];
        if (!fileExists(filePath)) {
            return null;
        }
        String repoName = Paths.get(location).getFileName().toString().replace(GIT_LINK_SUFFIX, "");
        String parsedBranch = split[1];
        return new String[] { filePath, repoName, null, parsedBranch };
    }

    /**
     * Parses a given branch URL and returns an array containing the following info:
     * { url, repository name, organisation name, branch name (if any }
     *
     * @return null if the given String is an invalid URL
     */
    private static String[] tryParsingAsBranchUrl(String possibleBranchUrl) {
        return null;
    }

    /**
     * Given a string of the forma 'repoLocation#branchName`, extracts the branch
     * name and returns an array containing the repoLocation and branchName.
     *
     * @returns an array of the form {repoLocation, null } if no branchName was present.
     */
    private static String[] extractBranch(String locationWithBranch) {
        String[] split = locationWithBranch.split("#");
        String location = split[0];
        if (split.length == 1) {
            return new String[] { location, null };
        }
        String branch = split[1];
        return new String[] { location, branch };
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

    /**
     * Converts all the strings in {@code locations} into {@code RepoLocation} objects.
     * Returns null if {@code locations} is null.
     * @throws InvalidLocationException if any of the strings are in invalid formats.
     */
    public static List<RepoLocation> convertStringsToLocations(List<String> locations) throws InvalidLocationException {
        if (locations == null) {
            return null;
        }

        List<RepoLocation> convertedLocations = new ArrayList<>();
        for (String location : locations) {
            convertedLocations.add(new RepoLocation(location));
        }

        return convertedLocations;
    }
}
