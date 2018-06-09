package reposense.frontend;

import java.nio.file.Path;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import reposense.dataobject.RepoConfiguration;
import reposense.exception.ParseException;
import reposense.parser.ArgsParser;
import reposense.report.RepoInfoFileGenerator;
import reposense.system.CsvConfigurationBuilder;
import reposense.system.LogsManager;

public class RepoSense {

    private static final Logger logger = LogsManager.getLogger(RepoSense.class);

    public static void main(String[] args) {
        try {
            CliArguments cliArguments = ArgsParser.parse(args);

            Path configFile = cliArguments.getConfigFilePath();
            Path targetFile = cliArguments.getOutputFilePath();
            Date fromDate = cliArguments.getSinceDate().orElse(null);
            Date toDate = cliArguments.getUntilDate().orElse(null);

            List<RepoConfiguration> configs = CsvConfigurationBuilder.buildConfigs(configFile, fromDate, toDate);
            RepoInfoFileGenerator.generateReposReport(configs, targetFile.toAbsolutePath().toString());
        } catch (ParseException pe) {
            logger.warning(pe.getMessage());
        }
    }
}
