package reposense.model;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import reposense.parser.AuthorConfigCsvParser;
import reposense.parser.GroupConfigCsvParser;
import reposense.parser.RepoConfigCsvParser;
import reposense.parser.exceptions.InvalidCsvException;
import reposense.parser.exceptions.InvalidHeaderException;
import reposense.system.LogsManager;
import reposense.util.function.FailableOptional;

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

        // parse the author config file path
        FailableOptional.of(cliArguments.getAuthorConfigFilePath())
                .filter(Files::exists)
                .map(x -> new AuthorConfigCsvParser(cliArguments.getAuthorConfigFilePath()).parse())
                .ifPresent(x -> RepoConfiguration.merge(repoConfigs, x))
                .ifPresent(() -> RepoConfiguration.setHasAuthorConfigFileToRepoConfigs(repoConfigs, true))
                .ifFailOfType(Arrays.asList(IOException.class, InvalidCsvException.class))
                .ifFail(x -> logger.log(Level.WARNING, x.getMessage(), x))
                .orElse(Collections.emptyList());

        // parse the group config file path
        FailableOptional.of(cliArguments.getGroupConfigFilePath())
                .filter(Files::exists)
                .map(x -> new GroupConfigCsvParser(x).parse())
                .ifPresent(x -> RepoConfiguration.setGroupConfigsToRepos(repoConfigs, x))
                .ifFailOfType(Arrays.asList(IOException.class, InvalidCsvException.class))
                .ifFail(x -> logger.log(Level.WARNING, x.getMessage(), x))
                .orElse(Collections.emptyList());

        return repoConfigs;
    }
}
