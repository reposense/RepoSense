package reposense.model;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import reposense.parser.InvalidLocationException;
import reposense.parser.ParseException;
import reposense.system.LogsManager;

/**
 * Represents RepoSense run configured by config files.
 */
public class ConfigRunConfiguration implements RunConfiguration {
    private static final Logger logger = LogsManager.getLogger(ConfigRunConfiguration.class);

    private final CliArguments cliArguments;

    public ConfigRunConfiguration(CliArguments cliArguments) {
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
