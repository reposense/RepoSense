package reposense.ConfigParser;

import reposense.dataobject.RepoConfiguration;
import reposense.exceptions.ParseException;
import reposense.system.CsvConfigurationBuilder;

import java.util.Date;
import java.util.List;

public class ConfigParser extends Parser<List<RepoConfiguration>, Argument> {

    @Override
    public List<RepoConfiguration> parse(Argument argument) throws ParseException {
        Date sinceDate = argument.getSinceDate().orElse(null);
        Date untilDate = argument.getUntilDate().orElse(null);

        List<RepoConfiguration> configs = CsvConfigurationBuilder.buildConfigs(argument.getConfigFile(), sinceDate, untilDate);

        return configs;
    }
}
