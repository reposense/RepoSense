package reposense.frontend;

import java.nio.file.Path;
import java.util.Date;
import java.util.List;

import reposense.dataobject.RepoConfiguration;
import reposense.exception.ParseException;
import reposense.parser.ArgsParser;
import reposense.report.RepoInfoFileGenerator;
import reposense.system.CsvConfigurationBuilder;

public class RepoSense {

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
            System.out.print(pe.getMessage());
        }
    }
}
