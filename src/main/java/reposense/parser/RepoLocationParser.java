package reposense.parser;

import static reposense.util.FileUtil.fileExists;
import static reposense.util.SystemUtil.isValidUrl;

import java.nio.file.Paths;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import reposense.model.RepoLocation;

public class RepoLocationParser {

    private static final Pattern GIT_REPOSITORY_LOCATION_PATTERN =
            Pattern.compile("^.*github.com\\/(?<org>.+?)\\/(?<repoName>.+?)\\.git$");
    private static final Pattern GITHUB_BRANCH_URL_PATTERN =
            Pattern.compile("(http|https)://github.com/(?<org>.+?)/(?<repoName>.+?)/tree/(?<branch>.+?)");
    private static final LOCATION = "LOCATION";
    private static final REPO_NAME = "REPOSITORY_NAME";
    private static final ORGANIZATION_NAME = "ORGANIZATION";
    private static final BRANCH_NAME = "BRANCH"

    /**
     * Given a String representing a repo location, returns
     * an array containing the following details of the repository (in order):
     * { location, repository name, organisation name, branch name (if any) }
     *
     * @param location a repository location, which is a file path or UR with the branch
     *                 name optionally appended
     * @throws InvalidLocationException if the repo location is an invalid path or an invalid URL
     */
    private static RepoLocation parse(String location) throws InvalidLocationException {
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
    private static Map<String, String> tryParsingAsRepoUrl(String location) {
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
    private static String[] tryParsingAsBranchUrl(String branchUrl) {
        Matcher matcher = GITHUB_BRANCH_URL_PATTERN.matcher(branchUrl);
        if (!isValidUrl(branchUrl) || !matcher.matches()) {
            return null;
        }
        String org = matcher.group("org");
        String repoName = matcher.group("repoName");
        String branch = matcher.group("branch");
        String location = createRepoUrl(org, repoName);
        return new String[] { location, repoName, org, branch };
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

}
