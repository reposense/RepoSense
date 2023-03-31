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
     * @throws IOException if user-supplied csv file does not exist or is not readable.
     * @throws InvalidCsvException if user-supplied repo-config csv is malformed.
     * @throws InvalidHeaderException if user-supplied csv file has header that cannot be parsed.
     */
    @Override
    public List<RepoConfiguration> getRepoConfigurations()
            throws IOException, InvalidCsvException, InvalidHeaderException {
        List<RepoConfiguration> repoConfigs = new RepoConfigCsvParser(cliArguments.getRepoConfigFilePath()).parse();
        List<AuthorConfiguration> authorConfigs;
        List<GroupConfiguration> groupConfigs;

        try {
            authorConfigs = new AuthorConfigCsvParser(cliArguments.getAuthorConfigFilePath()).parse();
            RepoConfiguration.merge(repoConfigs, authorConfigs);
            RepoConfiguration.setHasAuthorConfigFileToRepoConfigs(repoConfigs, true);
        } catch (FileNotFoundException fnfe) {
            // FileNotFoundException thrown as author-config.csv is not found.
            // Ignore exception as the file is optional.
        } catch (IOException | InvalidCsvException e) {
            // for all IO and invalid csv exceptions, log the error and continue
            logger.log(Level.WARNING, e.getMessage(), e);
        }

        try {
            groupConfigs = new GroupConfigCsvParser(cliArguments.getGroupConfigFilePath()).parse();
            RepoConfiguration.setGroupConfigsToRepos(repoConfigs, groupConfigs);
        } catch (FileNotFoundException fnfe) {
            // FileNotFoundException thrown as groups-config.csv is not found.
            // Ignore exception as the file is optional.
        } catch (IOException | InvalidCsvException e) {
            // for all other IO and invalid csv exceptions, log the error and continue
            logger.log(Level.WARNING, e.getMessage(), e);
        }

        return repoConfigs;
    }
}
