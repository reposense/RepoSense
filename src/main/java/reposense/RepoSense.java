package reposense;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import reposense.model.CliArguments;
import reposense.model.RepoConfiguration;
import reposense.parser.ArgsParser;
import reposense.parser.CsvParser;
import reposense.parser.ParseException;
import reposense.report.ReportGenerator;
import reposense.system.DashboardServer;
import reposense.system.LogsManager;
import reposense.util.FileUtil;

public class RepoSense {
    private static final Logger logger = LogsManager.getLogger(RepoSense.class);
    private static final int SERVER_PORT_NUMBER = 9000;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E MMM d HH:mm:ss 'SGT' yyyy");

    public static void main(String[] args) {
        try {
            CliArguments cliArguments = ArgsParser.parse(args);

            if (cliArguments.getReportDirectoryPath() != null) {
                DashboardServer.startServer(SERVER_PORT_NUMBER, cliArguments.getReportDirectoryPath().toAbsolutePath());
                return;
            }

            List<RepoConfiguration> configs = CsvParser.parse(cliArguments.getConfigFolderPath());
            RepoConfiguration.setFormatsToRepoConfigs(configs, cliArguments.getFormats());
            RepoConfiguration.setDatesToRepoConfigs(configs, cliArguments.getSinceDate(), cliArguments.getUntilDate());
            ReportGenerator.generateReposReport(configs, cliArguments.getOutputFilePath().toAbsolutePath().toString(),
                    formatter.format(ZonedDateTime.now(ZoneId.of("UTC+8"))));

            FileUtil.zip(cliArguments.getOutputFilePath().toAbsolutePath(), ".json");
        } catch (IOException ioe) {
            logger.log(Level.WARNING, ioe.getMessage(), ioe);
        } catch (ParseException pe) {
            logger.log(Level.WARNING, pe.getMessage(), pe);
        }
    }
}
