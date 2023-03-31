package reposense.model;

/**
 * Decides which RepoSense run configuration to utilize.
 */
public class RunConfigurationDecider {
    public static RunConfiguration getRunConfiguration(CliArguments cliArguments) {
        if (cliArguments.getLocations() != null) {
            return new CliRunConfiguration(cliArguments);
        }
        return new ConfigRunConfiguration(cliArguments);
    }
}
