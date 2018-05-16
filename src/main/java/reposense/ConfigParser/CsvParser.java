package reposense.ConfigParser;

import reposense.dataobject.Author;
import reposense.dataobject.RepoConfiguration;
import reposense.exceptions.ParseException;
import reposense.util.Constants;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class CsvParser extends Parser<List<RepoConfiguration>, InputParameter> {

    private static final String PARSE_EXCEPTION_MESSAGE_MALFORMED_CSV_FILE = "The supplied CSV file is malformed or corrupted.";

    private static final int SKIP_FIRST_LINE = 1;

    /** Positions of the elements of a line in the user-supplied CSV file */
    private static final int ORGANIZATION_POSITION = 0;
    private static final int REPOSITORY_NAME_POSITION = 1;
    private static final int BRANCH_POSITION = 2;
    private static final int GITHUB_ID_POSITION = 3;
    private static final int DISPLAY_NAME_POSITION = 4;
    private static final int ALIAS_POSITION = 5;

    /** String format of the key for RepoConfig HashMap */
    private static final String REPO_CONFIG_MAP_KEY_FORMAT = "%s|%s|%s";

    private HashMap<String, RepoConfiguration> repoMap = new HashMap<String, RepoConfiguration>();

    /**
     * Creates a RepoConfiguration object in the repoMap, if it does not exists.
     *
     * Parameters - organization, repositoryName and branch are used to create the key to access RepoMap.
     *
     * @param organization the String of the GitHub Organization
     * @param repositoryName the String of the Repository Name
     * @param branch the String of the Branch
     * @param sinceDate the starting Date to limit the results
     * @param untilDate the ending Date to limit the results
     */
    private void createRepoConfigInMapIfNotExists(final String organization, final String repositoryName, final String branch, final Date sinceDate, final Date untilDate) {

        final String key = String.format(REPO_CONFIG_MAP_KEY_FORMAT, organization, repositoryName, branch);

        if (!repoMap.containsKey(key)) {
            RepoConfiguration config = new RepoConfiguration(organization, repositoryName, branch);

            config.setToDate(sinceDate);
            config.setFromDate(untilDate);

            repoMap.put(key, config);
        }
    }

    /**
     * Creates a RepoConfiguration object in the repoMap, if it does not exists.
     *
     * Parameters - organization, repositoryName and branch are used to create the key to access RepoMap.
     *
     * @param organization the String of the GitHub Organization
     * @param repositoryName the String of the Repository Name
     * @param branch the String of the Branch
     * @throws UnsupportedOperationException, if the key does not exists in repoMap, which may happen if createRepoConfigInMapIfNotExists() is not called before this method.
     */
    private RepoConfiguration getRepoConfigFromMap(final String organization, final String repositoryName, final String branch) {
        final String key = String.format(REPO_CONFIG_MAP_KEY_FORMAT, organization, repositoryName, branch);

        if (repoMap.containsKey(key)) {
            return repoMap.get(key);
        }

        throw new UnsupportedOperationException("Illegal Usage: RepositoryConfiguration key does not exists. You should use call createRepoConfigInMapIfNotExists() first.");
    }

    /**
     * Returns a list of RepoConfiguration, which are the inflated object a line of the csv file.
     *
     * @param parameter Instance of an InputParameter object.
     * @return List of RepoConfiguration.
     * @throws IllegalArgumentException  If parameter is null.
     * @throws ParseException If user-supplied csv file fails to parse.
     */
    @Override
    public List<RepoConfiguration> parse(InputParameter parameter) throws ParseException {

        if (parameter == null) {
            throw new IllegalArgumentException("The supplied argument cannot be null");
        }

        final Date sinceDate = parameter.getSinceDate().orElse(null);
        final Date untilDate = parameter.getUntilDate().orElse(null);
        final Path path = parameter.getConfigFile().toPath();

        try {
            Files.lines(path).skip(SKIP_FIRST_LINE).forEach(line -> {
                        if (!line.isEmpty()) {
                            final String[] elements = line.split(Constants.CSV_SPLITTER);

                            final String organization = elements[ORGANIZATION_POSITION];
                            final String repositoryName = elements[REPOSITORY_NAME_POSITION];
                            final String branch = elements[BRANCH_POSITION];

                            createRepoConfigInMapIfNotExists(organization, repositoryName, branch, sinceDate, untilDate);

                            RepoConfiguration config = getRepoConfigFromMap(organization, repositoryName, branch);

                            Author author = new Author(elements[GITHUB_ID_POSITION]);
                            config.getAuthorList().add(author);

                            // Checks length of the elements array as , may not be included in empty columns, which results in a small length elements array
                            if (elements.length > DISPLAY_NAME_POSITION && !elements[DISPLAY_NAME_POSITION].isEmpty()) {
                                //Not empty then
                                config.getAuthorDisplayNameMap().put(author, elements[DISPLAY_NAME_POSITION]);
                            } else {
                                //Use GitHub Id as the display name
                                config.getAuthorDisplayNameMap().put(author, author.getGitId());
                            }

                            //Use GitHub Id as an alias
                            config.getAuthorAliasMap().put(elements[GITHUB_ID_POSITION], author);

                            if (elements.length > ALIAS_POSITION &&  !elements[ALIAS_POSITION].isEmpty()) {
                                final String[] aliases = elements[ALIAS_POSITION].split(Constants.AUTHOR_ALIAS_SPLITTER);

                                for (final String alias : aliases) {
                                    config.getAuthorAliasMap().put(alias, author);
                                }
                            }
                        }
                    }
            );
        } catch (IOException exception) {
            throw new ParseException(PARSE_EXCEPTION_MESSAGE_MALFORMED_CSV_FILE);
        }

        // Converts HashMap to list
        List<RepoConfiguration> configs = new ArrayList<RepoConfiguration>(repoMap.values());
        return configs;
    }
}
