package reposense.model;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import reposense.parser.AuthorConfigCsvParser;
import reposense.parser.GroupConfigCsvParser;
import reposense.parser.InvalidCsvException;
import reposense.parser.InvalidHeaderException;
import reposense.parser.InvalidLocationException;
import reposense.parser.ParseException;
import reposense.parser.RepoConfigCsvParser;
import reposense.system.LogsManager;

/**
 * Represents RepoSense run configured by CLI.
 */
public class CliRunConfiguration implements RunConfiguration {
    private static final Logger logger = LogsManager.getLogger(CliRunConfiguration.class);

    private final CliArguments cliArguments;

    public CliRunConfiguration(CliArguments cliArguments) {
        this.cliArguments = cliArguments;
    }

    /**
     * Constructs a list of {@link RepoConfiguration}.
     *
     * @throws ParseException if all repo locations are invalid.
     */
    @Override
    public List<RepoConfiguration> getRepoConfigurations() throws ParseException {
        List<RepoConfiguration> configs = new ArrayList<>();
        for (String locationString : cliArguments.getLocations()) {
            try {
                configs.add(new RepoConfiguration(new RepoLocation(locationString)));
            } catch (InvalidLocationException ile) {
                logger.log(Level.WARNING, ile.getMessage(), ile);
            }
        }

        if (configs.isEmpty()) {
            throw new ParseException("All repository locations are invalid.");
        }

        return configs;
    }
}
