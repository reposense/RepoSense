package reposense.parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import reposense.model.Author;
import reposense.model.RepoConfiguration;
import reposense.system.LogsManager;

/**
 * Parses a CSV configuration file for repository information.
 */
public class CsvParser {
    private static final String ELEMENT_SEPARATOR = ",";
    private static final String AUTHOR_ALIAS_SEPARATOR = ";";

    private static final String MESSAGE_UNABLE_TO_READ_CSV_FILE = "Unable to read the supplied CSV file.";
    private static final String MESSAGE_MALFORMED_LINE_FORMAT = "Warning! line %d in configuration file is malformed.\n"
            + "Contents: %s";

    /**
     * Positions of the elements of a line in the user-supplied CSV file
     */
    private static final int ORGANIZATION_POSITION = 0;
    private static final int REPOSITORY_NAME_POSITION = 1;
    private static final int BRANCH_POSITION = 2;
    private static final int GITHUB_ID_POSITION = 3;
    private static final int DISPLAY_NAME_POSITION = 4;
    private static final int ALIAS_POSITION = 5;

    private static final Logger logger = LogsManager.getLogger(CsvParser.class);

    /**
     * Returns a list of {@code RepoConfiguration}, each of which corresponds to a data line in the csv file.
     * The first line is assumed to be the header line and will be ignored.
     *
     * @throws IOException if user-supplied csv file does not exists or is not readable.
     */
    public static List<RepoConfiguration> parse(Path configFilePath) throws IOException {
        assert (configFilePath != null);

        List<RepoConfiguration> repoConfigurations = new ArrayList<RepoConfiguration>();
        int lineNumber = 1;

        try {
            // Skip first line, which is the header row
            final Collection<String> lines = Files.lines(configFilePath).skip(1).collect(Collectors.toList());

            for (final String line : lines) {
                processLine(repoConfigurations, line, lineNumber);
                lineNumber++;
            }

        } catch (IOException ioe) {
            throw new IOException(MESSAGE_UNABLE_TO_READ_CSV_FILE, ioe);
        }

        return repoConfigurations;
    }

    /**
     * Adds the {@code Author} to its corresponding {@code RepoConfiguration} if it exists, or creates a new
     * {@code RepoConfiguration} containing the {@code Author} and add it to the {@code repoConfigurations} otherwise.
     */
    private static void processLine(List<RepoConfiguration> repoConfigurations, String line, int lineNumber) {
        if (line.isEmpty()) {
            return;
        }

        String[] elements = line.split(ELEMENT_SEPARATOR);

        if (elements.length < GITHUB_ID_POSITION) {
            logger.warning(String.format(MESSAGE_MALFORMED_LINE_FORMAT, lineNumber, line));
            return;
        }

        String organization = elements[ORGANIZATION_POSITION];
        String repositoryName = elements[REPOSITORY_NAME_POSITION];
        String branch = elements[BRANCH_POSITION];

        RepoConfiguration config = new RepoConfiguration(organization, repositoryName, branch);
        int index = repoConfigurations.indexOf(config);

        // Take existing repoConfig if exists
        if (index != -1) {
            config = repoConfigurations.get(index);
        } else {
            // Add it in if it does not
            repoConfigurations.add(config);
        }

        Author author = new Author(elements[GITHUB_ID_POSITION]);
        config.getAuthorList().add(author);
        setDisplayName(elements, config, author);
        setAliases(elements, config, author);
    }

    /**
     * Associates display name from {@code elements} to {@code author}, if provided.
     * Otherwise, use github id from {@code elements}.
     */
    private static void setDisplayName(String[] elements, RepoConfiguration config, Author author) {
        boolean isDisplayNameInElements = elements.length > DISPLAY_NAME_POSITION
                && !elements[DISPLAY_NAME_POSITION].isEmpty();

        config.setAuthorDisplayName(author,
                isDisplayNameInElements ? elements[DISPLAY_NAME_POSITION] : author.getGitId());
    }

    /**
     * Associates github id and additional aliases in {@code elements} to {@code author}.
     */
    private static void setAliases(String[] elements, RepoConfiguration config, Author author) {
        config.getAuthorAliasMap().put(elements[GITHUB_ID_POSITION], author);
        boolean areAliasesInElements = elements.length > ALIAS_POSITION
                && !elements[ALIAS_POSITION].isEmpty();

        if (areAliasesInElements) {
            String[] aliases = elements[ALIAS_POSITION].split(AUTHOR_ALIAS_SEPARATOR);
            config.setAuthorAliases(author, aliases);
        }
    }
}
