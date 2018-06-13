package reposense.frontend;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import reposense.dataobject.RepoConfiguration;
import reposense.exception.ParseException;
import reposense.parser.ArgsParser;
import reposense.parser.CsvParser;
import reposense.report.RepoInfoFileGenerator;
import reposense.system.LogsManager;

public class RepoSense {

    private static final Logger logger = LogsManager.getLogger(RepoSense.class);

    private static void setDatesToRepoConfigs(List<RepoConfiguration> configs,
                                              Optional<Date> sinceDate, Optional<Date> untilDate) {
        for (RepoConfiguration config : configs) {
            config.setSinceDate(sinceDate.orElse(null));
            config.setUntilDate(untilDate.orElse(null));
        }
    }

    public static void main(String[] args) {
        try {
            CliArguments cliArguments = ArgsParser.parse(args);
            List<RepoConfiguration> configs = CsvParser.parse(cliArguments.getConfigFilePath());
            setDatesToRepoConfigs(configs, cliArguments.getSinceDate(), cliArguments.getUntilDate());
            RepoInfoFileGenerator.generateReposReport(configs,
                    cliArguments.getOutputFilePath().toAbsolutePath().toString());
        } catch (IOException ioe) {
            logger.warning(ioe.getMessage());
        } catch (ParseException pe) {
            logger.warning(pe.getMessage());
        }
    }
}
