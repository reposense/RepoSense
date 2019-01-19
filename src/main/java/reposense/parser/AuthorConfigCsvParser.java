package reposense.parser;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import reposense.model.Author;
import reposense.model.RepoConfiguration;
import reposense.model.RepoLocation;

public class AuthorConfigCsvParser extends CsvParser<RepoConfiguration> {
    public static final String AUTHOR_CONFIG_FILENAME = "author-config.csv";

    /**
     * Positions of the elements of a line in author-config.csv config file.
     */
    private static final int LOCATION_POSITION = 0;
    private static final int BRANCH_POSITION = 1;
    private static final int GITHUB_ID_POSITION = 2;
    private static final int EMAIL_POSITION = 3;
    private static final int DISPLAY_NAME_POSITION = 4;
    private static final int ALIAS_POSITION = 5;
    private static final int IGNORE_GLOB_LIST_POSITION = 6;

    public AuthorConfigCsvParser(Path csvFilePath) throws IOException {
        super(csvFilePath);
    }

    /**
     * Gets the list of positions that are mandatory for verification.
     */
    @Override
    protected int[] mandatoryPositions() {
        return new int[] {
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
        String branch = getValueInElement(elements, BRANCH_POSITION, RepoConfiguration.DEFAULT_BRANCH);
        String gitHubId = getValueInElement(elements, GITHUB_ID_POSITION);
        List<String> emails = getManyValueInElement(elements, EMAIL_POSITION);
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
        setEmails(author, emails);
        setDisplayName(config, author, displayName);
        setEmailsAndAliases(config, author, gitHubId, aliases, emails);
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
        RepoConfiguration config = new RepoConfiguration(new RepoLocation(location), branch);
        int index = results.indexOf(config);

        if (index != -1) {
            config = results.get(index);
        } else {
            results.add(config);
        }

        return config;
    }

    /**
     * Associates {@code emails} to {@code author}, if provided and not empty.
     * Adds the default github privacy email to {@code author}'s list of emails.
     */
    private static void setEmails(Author author, List<String> emails) {
        if (!emails.isEmpty()) {
            author.setEmails(new ArrayList<>(emails));
        }

        String defaultEmail = author.getGitId() + "@users.noreply.github.com";
        if (!author.getEmails().contains(defaultEmail)) {
            author.getEmails().add(defaultEmail);
        }
    }

    /**
     * Associates {@code displayName} to {@code author}, if provided and not empty.
     * Otherwise, use github id from {@code author}.
     */
    private static void setDisplayName(RepoConfiguration config, Author author, String displayName) {
        author.setDisplayName(!displayName.isEmpty() ? displayName : author.getGitId());
        config.setAuthorDisplayName(author, !displayName.isEmpty() ? displayName : author.getGitId());
    }

    /**
     * Associates {@code gitHubId} and additional {@code aliases} to {@code author}.
     */
    private static void setEmailsAndAliases(RepoConfiguration config, Author author, String gitHubId,
            List<String> aliases, List<String> emails) {
        config.addAuthorEmailsAndAliases(author, Arrays.asList(gitHubId));

        if (aliases.isEmpty()) {
            return;
        }

        config.addAuthorEmailsAndAliases(author, aliases);
        config.addAuthorEmailsAndAliases(author, emails);
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
