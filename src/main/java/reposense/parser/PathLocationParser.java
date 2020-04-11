package reposense.parser;

import static reposense.util.FileUtil.fileExists;

import java.nio.file.Paths;

/**
 * Contains functionality to parse repo location details from a path to a repo.
 */
public class PathLocationParser {

    private static final String GIT_LINK_SUFFIX = ".git";
    private static final String BRANCH_DELIMITER = "#";
    /**
     * Parses a given path to a repo and returns an array containing the following info:
     * { url, repository name, organisation name, branch name (if any) }
     *
     * @param path A path to a repository
     * @param isPathValidationNeeded If this flag is set to true, then the method will check
     *         verify that {@code path} is a valid path and refers to an actual directory on the
     *         filesystem.
     *
     * @return null if the given String is an invalid path, or no directory exists at the path,
     *         and {@code isPathValidationNeeded} was set to true.
     */
    public static String[] tryParsingAsPath(String path, boolean isPathValidationNeeded)  {
        String[] split = path.split(BRANCH_DELIMITER);
        String filePath = split[0];
        if (isPathValidationNeeded && !fileExists(filePath)) {
            return null;
        }
        String repoName = Paths.get(filePath).getFileName().toString().replace(GIT_LINK_SUFFIX, "");
        String branch = split.length == 1 ? null : split[1];
        return new String[] { filePath, repoName, null, branch };
    }
}
