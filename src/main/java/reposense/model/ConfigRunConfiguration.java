package reposense.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import reposense.parser.AuthorConfigCsvParser;
import reposense.parser.GroupConfigCsvParser;
import reposense.parser.RepoConfigCsvParser;
import reposense.parser.exceptions.InvalidCsvException;
import reposense.parser.exceptions.InvalidHeaderException;
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
        List<RepoConfiguration> repoConfigs = new RepoConfigCsvParser(cliArguments.getRepoConfigFilePath(),
                cliArguments.isPortfolio()).parse();
        List<AuthorConfiguration> authorConfigs;
        List<GroupConfiguration> groupConfigs;

        Path authorConfigFilePath = cliArguments.getAuthorConfigFilePath();
        Path groupConfigFilePath = cliArguments.getGroupConfigFilePath();


        if (authorConfigFilePath != null && Files.exists(authorConfigFilePath)) {
            try {
                authorConfigs = new AuthorConfigCsvParser(cliArguments.getAuthorConfigFilePath()).parse();
                RepoConfiguration.merge(repoConfigs, authorConfigs);
                RepoConfiguration.setHasAuthorConfigFileToRepoConfigs(repoConfigs, true);
            } catch (IOException | InvalidCsvException e) {
                // for all IO and invalid csv exceptions, log the error and continue
                logger.log(Level.WARNING, e.getMessage(), e);
            }
        }

        if (groupConfigFilePath != null && Files.exists(groupConfigFilePath)) {
            try {
                groupConfigs = new GroupConfigCsvParser(cliArguments.getGroupConfigFilePath()).parse();
                RepoConfiguration.setGroupConfigsToRepos(repoConfigs, groupConfigs);
            } catch (IOException | InvalidCsvException e) {
                // for all IO and invalid csv exceptions, log the error and continue
                logger.log(Level.WARNING, e.getMessage(), e);
            }
        }

        return repoConfigs;
    }
}
