package reposense.parser;

import static reposense.util.FileUtil.fileExists;

import java.nio.file.Paths;
import java.util.Arrays;

/**
 * Contains functionality to parse repo location details from a path to a repo.
 */
public class PathLocationParser {

    private static final String GIT_LINK_SUFFIX = ".git";
    private static final String BRANCH_DELIMITER = "#";
    /**
     * Parses a given path to a repo and returns an array containing the following info:
     * { path to repo, repository name, organisation name, branch name (if any) }
     *
     * @param pathAndBranch A path to a repository, with a branch name optionally appended at the end
     *         e.g. /home/users/tom/Desktop/reposense#release
     * @param isPathValidationNeeded If this flag is set to true, then the method will check
     *         verify that {@code path} is a valid path and refers to an actual directory on the
     *         filesystem.
     *
     * @return null if the given String is an invalid path, or no directory exists at the path,
     *         and {@code isPathValidationNeeded} was set to true.
     */
    public static String[] tryParsingAsPath(String pathAndBranch, boolean isPathValidationNeeded)  {
        String pathToRepo;
        String branch;

        if (fileExists(pathAndBranch)) {
            // If the given path represents an existing file, then any # characters that occur in the path
            // are part of the filenames in the path, rather than an indication of a branch name.
            pathToRepo = pathAndBranch;
            branch = null;
        } else {
            String[] split = splitPathAndBranch(pathAndBranch);
            pathToRepo = split[0];
            branch = split[1];
            if (isPathValidationNeeded && !fileExists(pathToRepo)) {
                return null;
            }
        }

        String repoName = Paths.get(pathToRepo).getFileName().toString().replace(GIT_LINK_SUFFIX, "");
        return new String[] { pathToRepo, repoName, null, branch };
    }

    /**
     * Given a string of the form stringA#stringB, breaks the string at the # character
     * and returns a 2-element array of the form { stringA, stringB }
     * Note that stringA can contain several # characters as well.
     * The string is broken at the last # character.
     * Example: stringA#stringB#stringC is parsed to give { stringA#strinB, stringC }
     */
    private static String[] splitPathAndBranch(String pathAndBranch) {
        String[] split = pathAndBranch.split(BRANCH_DELIMITER);
        String path;
        String branch;
        if (split.length == 1) {
            // no branch name present
            path = split[0];
            branch = null;
        } else {
            path = String.join(BRANCH_DELIMITER, Arrays.copyOf(split, split.length - 1));
            branch = split[split.length - 1];
        }
        return new String[] { path, branch };
    }
}
