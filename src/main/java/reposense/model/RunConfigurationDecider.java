package reposense.model;


public class RunConfigurationDecider {
    public static RunConfiguration getRunConfiguration(CliArguments cliArguments) {
        if (cliArguments.getLocations() != null) {
            return new ConfigRunConfiguration(cliArguments);
        }
        return new CliRunConfiguration(cliArguments);
    }
}
