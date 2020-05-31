package reposense.parser;

/**
 * Contains functionality to parse repository location details from a String.
 * The String can be a a repo URL, branch URL or a filepath.
 * These details are: the repo location, repo name, organization name and branch name.
 */
public class RepoLocationParser {

    public static final String MESSAGE_INVALID_LOCATION = "The given location is invalid";
    public static final int LOCATION_INDEX = 0;
    public static final int REPO_NAME_INDEX = 1;
    public static final int ORG_INDEX = 2;
    public static final int BRANCH_NAME_INDEX = 3;
    public static final int NUM_OF_PARSED_DETAILS = 4;

    /**
     * Parses a String representing a repo location (which could be a repo URL, branch URL
     * or a filepath), and returns an array containing the following repo location details:
     * { location, repository name, organization name, branch name (if any) }
     *
     * @param locationToParse a string that needs to be parsed to extract all details about a repo location,
     *        This string can be either a file path or an URL, with a branch name optionally appended
     * @throws InvalidLocationException if the repo location is an invalid path or an invalid URL
     */
    public static String[] parse(String locationToParse) throws InvalidLocationException {
        String[] parsedInfo = UrlLocationParser.parseAsRepoUrl(locationToParse);
        if (parsedInfo != null) {
            return parsedInfo;
        }

        parsedInfo = PathLocationParser.parseAsPath(locationToParse, true);
        if (parsedInfo != null) {
            return parsedInfo;
        }

        parsedInfo = UrlLocationParser.parseAsBranchUrl(locationToParse);
        if (parsedInfo != null) {
            return parsedInfo;
        }

        throw new InvalidLocationException(MESSAGE_INVALID_LOCATION);
    }

}
