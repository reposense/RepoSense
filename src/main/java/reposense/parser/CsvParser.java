package reposense.parser;

import reposense.dataobject.Author;
import reposense.dataobject.RepoConfiguration;
import reposense.exception.ParseException;
import reposense.frontend.CliArguments;
import reposense.util.Constants;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CsvParser {

    private static final int SKIP_FIRST_LINE = 1;
    private static final int ORGANIZATION_POSITION = 0;
    private static final int REPOSITORY_NAME_POSITION = 1;
    private static final int BRANCH_POSITION = 2;
    private static final String PARSE_EXCEPTION_MESSAGE_MALFORMED_CSV_FILE = "The supplied CSV file is malformed or corrupted.";

    /**
     * Returns a list of RepoConfiguration, which are the inflated object a line of the csv file.
     *
     * @param arguments
     * @return List of RepoConfiguration
     * @throws IllegalArgumentException  If argument is null
     * @throws ParseException If given inputs or file fail to parse
     */
    public List<RepoConfiguration> parse(CliArguments arguments) throws IllegalArgumentException {

        if (arguments == null) {
            throw new IllegalArgumentException("The supplied argument cannot be null");
        }

        List<RepoConfiguration> configs = new ArrayList<>();

        Date sinceDate = arguments.getSinceDate().orElse(null);
        Date untilDate = arguments.getUntilDate().orElse(null);

        Path path = arguments.getConfigFile().toPath();

        try {
            Files.lines(path).skip(SKIP_FIRST_LINE).forEach(line -> {
                        if (!line.isEmpty()) {
                            String[] elements = line.split(Constants.CSV_SPLITTER);

                            String org = elements[ORGANIZATION_POSITION];
                            String repoName = elements[REPOSITORY_NAME_POSITION];
                            String branch = elements[BRANCH_POSITION];

                            RepoConfiguration config = new RepoConfiguration(org, repoName, branch);
                            aggregateAuthorInfo(elements, config);
                            config.setToDate(sinceDate);
                            config.setFromDate(untilDate);
                            configs.add(config);
                        }
                    }
            );
        } catch (IOException exception) {
            throw new IllegalArgumentException(PARSE_EXCEPTION_MESSAGE_MALFORMED_CSV_FILE);
        }

        return configs;
    }

    private static void aggregateAuthorInfo(String[] elements, RepoConfiguration config) {
        for (int i = 3; i < elements.length; i += 3) {
            Author currentAuthor = new Author(elements[i]);
            config.getAuthorList().add(currentAuthor);
            //put the gitID itself as alias
            config.getAuthorAliasMap().put(elements[i], currentAuthor);
            //handle student's display name
            if (i + 1 == elements.length) {
                // put the gitID itself as display name if display name is not available
                config.getAuthorDisplayNameMap().put(currentAuthor, currentAuthor.getGitId());
                break;
            }
            if (elements[i + 1].length() == 0) {
                config.getAuthorDisplayNameMap().put(currentAuthor, currentAuthor.getGitId());
            } else {
                config.getAuthorDisplayNameMap().put(currentAuthor, elements[i + 1]);
            }
            //handle student's git aliases
            if (i + 2 == elements.length) {
                break;
            }
            if (elements[i + 2].length() != 0) {
                for (String alias : elements[i + 2].split(Constants.AUTHOR_ALIAS_SPLITTER)) {
                    config.getAuthorAliasMap().put(alias, currentAuthor);
                }
            }
        }
    }
}