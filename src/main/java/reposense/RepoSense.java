package reposense;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import reposense.model.CliArguments;
import reposense.model.ConfigCliArguments;
import reposense.model.LocationsCliArguments;
import reposense.model.RepoConfiguration;
import reposense.model.ViewCliArguments;
import reposense.parser.ArgsParser;
import reposense.parser.CsvParser;
import reposense.parser.InvalidLocationException;
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
            List<RepoConfiguration> configs = null;

            if (cliArguments instanceof ViewCliArguments) {
                DashboardServer.startServer(SERVER_PORT_NUMBER, cliArguments.getReportDirectoryPath().toAbsolutePath());
                return;
            } else if (cliArguments instanceof ConfigCliArguments) {
                configs = getRepoConfigurations((ConfigCliArguments) cliArguments);
            } else if (cliArguments instanceof LocationsCliArguments) {
                configs = getRepoConfigurations((LocationsCliArguments) cliArguments);
            } else {
                throw new AssertionError("CliArguments's subclass type is unhandled.");
            }

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

    /**
     * Constructs a list of {@code RepoConfiguration} if {@code cliArguments} is a {@code ConfigCliArguments}.
     *
     * @throws IOException if user-supplied csv file does not exists or is not readable.
     */
    public static List<RepoConfiguration> getRepoConfigurations(ConfigCliArguments cliArguments) throws IOException {
        return CsvParser.parse(cliArguments.getConfigFolderPath());
    }

    /**
     * Constructs a list of {@code RepoConfiguration} if {@code cliArguments} is a {@code LocationsCliArguments}.
     */
    public static List<RepoConfiguration> getRepoConfigurations(LocationsCliArguments cliArguments) {
        List<RepoConfiguration> configs = new ArrayList<>();
        for (String location : cliArguments.getLocations()) {
            try {
                configs.add(new RepoConfiguration(location));
            } catch (InvalidLocationException ile) {
                logger.log(Level.WARNING, ile.getMessage(), ile);
            }
        }

        return configs;
    }
}
