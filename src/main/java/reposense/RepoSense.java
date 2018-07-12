package reposense;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import reposense.model.CliArguments;
import reposense.model.RepoConfiguration;
import reposense.parser.ArgsParser;
import reposense.parser.CsvParser;
import reposense.parser.ParseException;
import reposense.report.ReportGenerator;
import reposense.system.LogsManager;

public class RepoSense {
    public static final List<String> DEFAULT_FILE_FORMATS = Arrays.asList("java", "adoc", "js", "md", "css",
            "html", "cs", "json", "xml", "py", "fxml", "tag", "jsp", "gradle");

    private static final Logger logger = LogsManager.getLogger(RepoSense.class);

    public static void main(String[] args) {
        try {
            CliArguments cliArguments = ArgsParser.parse(args);

            List<RepoConfiguration> configs = CsvParser.parse(cliArguments.getConfigFilePath());
            RepoConfiguration.setFormatsToRepoConfigs(configs, DEFAULT_FILE_FORMATS);
            RepoConfiguration.setDatesToRepoConfigs(configs, cliArguments.getSinceDate(), cliArguments.getUntilDate());

            ReportGenerator.generateReposReport(
                    configs, cliArguments.getOutputFilePath().toAbsolutePath().toString());
        } catch (IOException ioe) {
            logger.log(Level.WARNING, ioe.getMessage(), ioe);
        } catch (ParseException pe) {
            logger.log(Level.WARNING, pe.getMessage(), pe);
        }
    }
}
