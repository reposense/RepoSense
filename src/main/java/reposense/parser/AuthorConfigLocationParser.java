package reposense.parser;

import static reposense.parser.LocalRepoLocationParser.parseLocalRepoLocation;
import static reposense.parser.RemoteRepoLocationParser.parseRemoteRepoLocation;

/**
 * Contains repository location parsing functionalities specified to {@code author-config.csv} file.
 */
public class AuthorConfigLocationParser {
    /**
     * Parse a location and returns a 2-element array of the form {location, branch}
     * according to whether it uses no additional special syntax, delimiter syntax or
     * GitHub-specific branch URL syntax.
     */
    public static String[] parseLocation(String location, String defaultSpecifiedBranch) {
        String[] parsedResult;
        parsedResult = parseLocalRepoLocation(location);
        if (parsedResult != null) {
            return parsedResult;
        }

        parsedResult = parseRemoteRepoLocation(location);
        if (parsedResult != null) {
            return parsedResult;
        }

        return new String[] { location, defaultSpecifiedBranch };
    }
}
