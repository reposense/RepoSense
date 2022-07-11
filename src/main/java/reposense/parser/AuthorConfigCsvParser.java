package reposense.parser;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVRecord;

import reposense.model.Author;
import reposense.model.AuthorConfiguration;
import reposense.model.RepoLocation;

/**
 * Container for the values parsed from {@code author-config.csv} file.
 */
public class AuthorConfigCsvParser extends CsvParser<AuthorConfiguration> {
    public static final String AUTHOR_CONFIG_FILENAME = "author-config.csv";

    /**
     * Positions of the elements of a line in author-config.csv config file.
     */
    private static final String[] LOCATION_HEADER = {"Repository's Location"};
    private static final String[] BRANCH_HEADER = {"Branch"};
    private static final String[] GIT_ID_HEADERS = {"Author's Git Host ID", "Author's GitHub ID"};
    private static final String[] EMAIL_HEADER = {"Author's Emails"};
    private static final String[] DISPLAY_NAME_HEADER = {"Author's Display Name"};
    private static final String[] ALIAS_HEADER = {"Author's Git Author Name"};
    private static final String[] IGNORE_GLOB_LIST_HEADER = {"Ignore Glob List"};

    public AuthorConfigCsvParser(Path csvFilePath) throws FileNotFoundException {
        super(csvFilePath);
    }

    /**
     * Gets the list of headers that are mandatory for verification.
     */
    @Override
    protected String[][] mandatoryHeaders() {
        return new String[][] {
                GIT_ID_HEADERS,
        };
    }

    /**
     * Gets the list of optional headers that can be parsed.
     */
    @Override
    protected String[][] optionalHeaders() {
        return new String[][] {
                LOCATION_HEADER, BRANCH_HEADER, EMAIL_HEADER, DISPLAY_NAME_HEADER, ALIAS_HEADER,
                IGNORE_GLOB_LIST_HEADER,
        };
    }

    /**
     * Processes the csv {@code record} line by line and add created {@link AuthorConfiguration} into {@code results}
     * but skips {@code author} already exists in a {@link AuthorConfiguration} that has same {@code location}
     * and {@code branch}.
     */
    @Override
    protected void processLine(List<AuthorConfiguration> results, CSVRecord record) throws ParseException {
        String location = get(record, LOCATION_HEADER);
        String branch = getOrDefault(record, BRANCH_HEADER, AuthorConfiguration.DEFAULT_BRANCH);
        String gitId = get(record, GIT_ID_HEADERS);
        List<String> emails = getAsList(record, EMAIL_HEADER);
        String displayName = get(record, DISPLAY_NAME_HEADER);
        List<String> aliases = getAsList(record, ALIAS_HEADER);
        List<String> ignoreGlobList = getAsList(record, IGNORE_GLOB_LIST_HEADER);

        AuthorConfiguration config = findMatchingAuthorConfiguration(results, location, branch);

        Author author = new Author(gitId);

        if (config.containsAuthor(author)) {
            logger.warning(String.format(
                    "Skipping author as %s already in repository %s %s",
                    author.getGitId(), config.getLocation(), config.getBranch()));
            return;
        }

        author.setEmails(new ArrayList<>(emails));
        author.setDisplayName(!displayName.isEmpty() ? displayName : author.getGitId());
        if (!aliases.isEmpty()) {
            author.setAuthorAliases(aliases);
        }
        if (!ignoreGlobList.isEmpty()) {
            author.setIgnoreGlobList(ignoreGlobList);
        }

        config.addAuthor(author);
    }


    /**
     * Gets an existing {@link AuthorConfiguration} from {@code results} if {@code location} and {@code branch} matches.
     * Otherwise, adds a newly created {@link AuthorConfiguration} into {@code results} and returns it.
     *
     * @throws InvalidLocationException if {@code location} is invalid.
     */
    private static AuthorConfiguration findMatchingAuthorConfiguration(List<AuthorConfiguration> results,
            String location, String branch) throws InvalidLocationException {
        AuthorConfiguration config = new AuthorConfiguration(new RepoLocation(location), branch);

        for (AuthorConfiguration authorConfig : results) {
            if (authorConfig.getLocation().equals(config.getLocation())
                    && authorConfig.getBranch().equals(config.getBranch())) {
                return authorConfig;
            }
        }

        results.add(config);
        return config;
    }
}
