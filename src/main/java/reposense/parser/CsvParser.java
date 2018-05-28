package reposense.parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import reposense.dataobject.Author;
import reposense.dataobject.RepoConfiguration;
import reposense.frontend.CliArguments;
import reposense.util.Constants;

/**
 * Parses a CSV configuration file for repository information.
 */
public class CsvParser {
    private static final String MESSAGE_UNABLE_TO_READ_CSV_FILE = "Unable to read the supplied CSV file.";
    private static final String MESSAGE_MALFORMED_LINE_FORMAT = "Warning! line %s is malformed.\n"
            + "Contents: %s";

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

    /**
     * Creates {@code RepoConfiguration} object in {@code repositoryMap}, if it does not exists.
     *
     * Parameters - organization, repositoryName and branch are used to create the key to access RepoMap.
     */
    private void createRepoConfigInMapIfNotExists(final HashMap<String, RepoConfiguration> repositoryMap,
            final String organization, final String repositoryName, final String branch, final Date sinceDate,
            final Date untilDate) {
        final String key = createRepoConfigKey(organization, repositoryName, branch);

        if (!repositoryMap.containsKey(key)) {
            RepoConfiguration config = new RepoConfiguration(organization, repositoryName, branch);

            config.setSinceDate(sinceDate);
            config.setUntilDate(untilDate);

            repositoryMap.put(key, config);
        }
    }

    /**
     * Returns a {@code RepoConfiguration} contained in {@code repositoryMap}
     *
     * Parameters - organization, repositoryName and branch are used to create the key to access repositoryMap.
     */
    private RepoConfiguration getRepoConfigFromMap(final HashMap<String, RepoConfiguration> repositoryMap,
            final String organization, final String repositoryName, final String branch,
            final Date sinceDate, final Date untilDate) {
        final String key = createRepoConfigKey(organization, repositoryName, branch);
        createRepoConfigInMapIfNotExists(repositoryMap, organization, repositoryName, branch, sinceDate, untilDate);

        return repositoryMap.get(key);
    }

    private String createRepoConfigKey(String organization, String repositoryName, String branch) {
        return String.format(REPO_CONFIG_MAP_KEY_FORMAT, organization, repositoryName, branch);
    }

    /**
     * Returns a list of {@code RepoConfiguration}, which are the inflated object a line of the csv file.
     *
     * @throws IOException If user-supplied csv file does not exists or is not readable.
     */
    public List<RepoConfiguration> parse(CliArguments argument) throws IOException {
        assert (argument != null);

        final Date sinceDate = argument.getSinceDate().orElse(null);
        final Date untilDate = argument.getUntilDate().orElse(null);
        final Path path = argument.getConfigFile().toPath();

        HashMap<String, RepoConfiguration> repositoryMap = new HashMap<String, RepoConfiguration>();
        int lineNumber = 1;

        try {
            // Skips first line, which is the header row
            final Collection<String> lines = Files.lines(path).skip(SKIP_FIRST_LINE).collect(Collectors.toList());

            for (final String line : lines) {
                processLine(repositoryMap, sinceDate, untilDate, line, lineNumber);
                lineNumber++;
            }

        } catch (IOException iex) {
            throw new IOException(MESSAGE_UNABLE_TO_READ_CSV_FILE);
        }

        return new ArrayList<RepoConfiguration>(repositoryMap.values());
    }

    private void processLine(HashMap<String, RepoConfiguration> repositoryMap,
            Date sinceDate, Date untilDate, String line, int lineNumber) {
        if (!line.isEmpty()) {
            String[] elements = line.split(Constants.CSV_SPLITTER);

            if (elements.length < GITHUB_ID_POSITION) {
                // Warns incorrect lines and does not stop the program
                System.out.println(String.format(MESSAGE_MALFORMED_LINE_FORMAT, lineNumber, line));
                return;
            }

            String organization = elements[ORGANIZATION_POSITION];
            String repositoryName = elements[REPOSITORY_NAME_POSITION];
            String branch = elements[BRANCH_POSITION];

            RepoConfiguration config = getRepoConfigFromMap(
                    repositoryMap, organization, repositoryName, branch, sinceDate, untilDate);

            Author author = new Author(elements[GITHUB_ID_POSITION]);
            config.getAuthorList().add(author);
            setDisplayName(elements, config, author);
            setAlias(elements, config, author);
        }
    }

    private void setDisplayName(String[] elements, RepoConfiguration config, Author author) {
        // Checks length of the elements array as trailing commas may be omitted for empty columns
        if (elements.length > DISPLAY_NAME_POSITION && !elements[DISPLAY_NAME_POSITION].isEmpty()) {
            //Not empty, take the supplied value as Display Name
            config.getAuthorDisplayNameMap().put(author, elements[DISPLAY_NAME_POSITION]);
        } else {
            //else, use GitHub Id as Display Name
            config.getAuthorDisplayNameMap().put(author, author.getGitId());
        }
    }

    private void setAlias(String[] elements, RepoConfiguration config, Author author) {
        //Always use GitHub Id as an alias
        config.getAuthorAliasMap().put(elements[GITHUB_ID_POSITION], author);

        // Checks length of the elements array as trailing commas may be omitted for empty columns
        // If more alias are provided, use them as well
        if (elements.length > ALIAS_POSITION &&  !elements[ALIAS_POSITION].isEmpty()) {
            String[] aliases = elements[ALIAS_POSITION].split(Constants.AUTHOR_ALIAS_SPLITTER);

            for (String alias : aliases) {
                config.getAuthorAliasMap().put(alias, author);
            }
        }
    }
}
