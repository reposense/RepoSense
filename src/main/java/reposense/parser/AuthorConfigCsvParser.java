package reposense.parser;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import reposense.model.Author;
import reposense.model.RepoConfiguration;

public class AuthorConfigCsvParser extends CsvParser<RepoConfiguration> {
    public static final String AUTHOR_CONFIG_FILENAME = "author-config.csv";

    /**
     * Positions of the elements of a line in author-config.csv config file.
     */
    private static final int LOCATION_POSITION = 0;
    private static final int BRANCH_POSITION = 1;
    private static final int GITHUB_ID_POSITION = 2;
    private static final int DISPLAY_NAME_POSITION = 3;
    private static final int ALIAS_POSITION = 4;
    private static final int IGNORE_GLOB_LIST_POSITION = 5;

    public AuthorConfigCsvParser(Path csvFilePath) throws IOException {
        super(csvFilePath);
    }

    /**
     * Gets the list of positions that are mandatory for verification.
     */
    @Override
    protected int[] mandatoryPositions() {
        return new int[] {
            LOCATION_POSITION,
            GITHUB_ID_POSITION,
        };
    }

    /**
     * Processes the csv file line by line and add created {@code RepoConfiguration} into {@code results} but
     * skips {@code author} already exists in a {@code RepoConfiguration} that has same {@code location} and
     * {@code branch}.
     */
    @Override
    protected void processLine(List<RepoConfiguration> results, String[] elements)
            throws ParseException {
        String location = getValueInElement(elements, LOCATION_POSITION);
        String branch = getValueInElement(elements, BRANCH_POSITION);
        String gitHubId = getValueInElement(elements, GITHUB_ID_POSITION);
        String displayName = getValueInElement(elements, DISPLAY_NAME_POSITION);
        List<String> aliases = getManyValueInElement(elements, ALIAS_POSITION);
        List<String> ignoreGlobList = getManyValueInElement(elements, IGNORE_GLOB_LIST_POSITION);

        RepoConfiguration config = getRepoConfiguration(results, location, branch);

        Author author = new Author(gitHubId);

        if (config.containsAuthor(author)) {
            logger.warning(String.format(
                    "Skipping author as %s already in repository %s", author.getGitId(), config.getDisplayName()));
            return;
        }

        config.addAuthor(author);
        setDisplayName(config, author, displayName);
        setAliases(config, author, gitHubId, aliases);
        setAuthorIgnoreGlobList(author, ignoreGlobList);
    }


    /**
     * Gets an existing {@code RepoConfiguration} from {@code results} if {@code location} and {@code branch} matches.
     * Otherwise adds a newly created {@code RepoConfigurtion} into {@code results} and returns it.
     *
     * @throws InvalidLocationException if {@code location} is invalid.
     */
    private static RepoConfiguration getRepoConfiguration(
            List<RepoConfiguration> results, String location, String branch) throws InvalidLocationException {
        RepoConfiguration config = new RepoConfiguration(location, branch);
        int index = results.indexOf(config);

        if (index != -1) {
            config = results.get(index);
        } else {
            results.add(config);
        }

        return config;
    }

    /**
     * Associates {@code displayName} to {@code author}, if provided and not empty.
     * Otherwise, use github id from {@code author}.
     */
    private static void setDisplayName(RepoConfiguration config, Author author, String displayName) {
        config.setAuthorDisplayName(author, !displayName.isEmpty() ? displayName : author.getGitId());
    }

    /**
     * Associates {@code gitHubId} and additional {@code aliases} to {@code author}.
     */
    private static void setAliases(RepoConfiguration config, Author author, String gitHubId, List<String> aliases) {
        config.addAuthorAliases(author, Arrays.asList(gitHubId));

        if (aliases.isEmpty()) {
            return;
        }

        config.addAuthorAliases(author, aliases);
        author.setAuthorAliases(aliases);
    }

    /**
     * Sets the list of globs to ignore for the {@code author} for file analysis.
     */
    private static void setAuthorIgnoreGlobList(Author author, List<String> ignoreGlobList) {
        if (ignoreGlobList.isEmpty()) {
            return;
        }

        author.setIgnoreGlobList(ignoreGlobList);
    }
}
