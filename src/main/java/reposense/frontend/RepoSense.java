package reposense.frontend;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import reposense.dataobject.RepoConfiguration;
import reposense.exception.ParseException;
import reposense.parser.ArgsParser;
import reposense.parser.CsvParser;
import reposense.report.ReportGenerator;
import reposense.system.LogsManager;

public class RepoSense {
    private static final Logger logger = LogsManager.getLogger(RepoSense.class);

    public static void main(String[] args) {
        try {
            CliArguments cliArguments = ArgsParser.parse(args);

            List<RepoConfiguration> configs = CsvParser.parse(cliArguments.getConfigFilePath());
            RepoConfiguration.setDatesToRepoConfigs(configs, cliArguments.getSinceDate(), cliArguments.getUntilDate());

            ReportGenerator.generateReposReport(
                    configs, cliArguments.getOutputFilePath().toAbsolutePath().toString());
        } catch (IOException ioe) {
            logger.warning(ioe.getMessage());
        } catch (ParseException pe) {
            logger.warning(pe.getMessage());
        }
    }
}
