package reposense;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.JsonSyntaxException;

import net.sourceforge.argparse4j.helper.HelpScreenException;
import reposense.model.AuthorConfiguration;
import reposense.model.CliArguments;
import reposense.model.ConfigCliArguments;
import reposense.model.LocationsCliArguments;
import reposense.model.RepoConfiguration;
import reposense.model.RepoLocation;
import reposense.model.ReportConfiguration;
import reposense.model.ViewCliArguments;
import reposense.parser.ArgsParser;
import reposense.parser.AuthorConfigCsvParser;
import reposense.parser.InvalidLocationException;
import reposense.parser.ParseException;
import reposense.parser.RepoConfigCsvParser;
import reposense.parser.ReportConfigJsonParser;
import reposense.report.ReportGenerator;
import reposense.system.LogsManager;
import reposense.system.ReportServer;
import reposense.util.FileUtil;

public class RepoSense {
    private static final Logger logger = LogsManager.getLogger(RepoSense.class);
    private static final int SERVER_PORT_NUMBER = 9000;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E MMM d HH:mm:ss yyyy z");
    private static final String MESSAGE_INVALID_CONFIG_JSON = "%s Ignoring the report config provided.";

    public static void main(String[] args) {
        try {
            CliArguments cliArguments = ArgsParser.parse(args);
            List<RepoConfiguration> configs = null;
            ReportConfiguration reportConfig = new ReportConfiguration();

            if (cliArguments instanceof ViewCliArguments) {
                ReportServer.startServer(SERVER_PORT_NUMBER, ((
                        ViewCliArguments) cliArguments).getReportDirectoryPath().toAbsolutePath());
                return;
            } else if (cliArguments instanceof ConfigCliArguments) {
                configs = getRepoConfigurations((ConfigCliArguments) cliArguments);
                reportConfig = getReportConfigurations((ConfigCliArguments) cliArguments);
            } else if (cliArguments instanceof LocationsCliArguments) {
                configs = getRepoConfigurations((LocationsCliArguments) cliArguments);
            } else {
                throw new AssertionError("CliArguments's subclass type is unhandled.");
            }

            RepoConfiguration.setFormatsToRepoConfigs(configs, cliArguments.getFormats());
            RepoConfiguration.setDatesToRepoConfigs(configs, cliArguments.getSinceDate(), cliArguments.getUntilDate());
            ReportGenerator.generateReposReport(configs, cliArguments.getOutputFilePath().toAbsolutePath().toString(),
                    reportConfig, formatter.format(ZonedDateTime.now(cliArguments.getZoneId())));

            FileUtil.zip(cliArguments.getOutputFilePath().toAbsolutePath(), ".json");

            if (cliArguments.isAutomaticallyLaunching()) {
                ReportServer.startServer(SERVER_PORT_NUMBER, cliArguments.getOutputFilePath().toAbsolutePath());
            }
        } catch (IOException ioe) {
            logger.log(Level.WARNING, ioe.getMessage(), ioe);
        } catch (ParseException pe) {
            logger.log(Level.WARNING, pe.getMessage(), pe);
        } catch (HelpScreenException e) {
            // help message was printed by the ArgumentParser; it is safe to exit.
        }
    }

    /**
     * Constructs a list of {@code RepoConfiguration} if {@code cliArguments} is a {@code ConfigCliArguments}.
     *
     * @throws IOException if user-supplied csv file does not exists or is not readable.
     */
    public static List<RepoConfiguration> getRepoConfigurations(ConfigCliArguments cliArguments) throws IOException {
        List<RepoConfiguration> repoConfigs = new RepoConfigCsvParser(cliArguments.getRepoConfigFilePath()).parse();
        List<AuthorConfiguration> authorConfigs = null;

        try {
            authorConfigs = new AuthorConfigCsvParser(cliArguments.getAuthorConfigFilePath()).parse();
            RepoConfiguration.merge(repoConfigs, authorConfigs);
        } catch (IOException ioe) {
            // IOException thrown as author-config.csv is not found.
            // Ignore exception as the file is optional.
        }

        return repoConfigs;
    }

    /**
     * Constructs a list of {@code RepoConfiguration} if {@code cliArguments} is a {@code LocationsCliArguments}.
     */
    public static List<RepoConfiguration> getRepoConfigurations(LocationsCliArguments cliArguments) {
        List<RepoConfiguration> configs = new ArrayList<>();
        for (String locationString : cliArguments.getLocations()) {
            try {
                configs.add(new RepoConfiguration(new RepoLocation(locationString)));
            } catch (InvalidLocationException ile) {
                logger.log(Level.WARNING, ile.getMessage(), ile);
            }
        }

        RepoConfiguration.setStandaloneConfigIgnoredToRepoConfigs(configs, cliArguments.isStandaloneConfigIgnored());

        return configs;
    }

    /**
     * Constructs {@code ReportConfiguration} if {@code cliArguments} is a {@code ConfigCliArguments}.
     */
    public static ReportConfiguration getReportConfigurations(ConfigCliArguments cliArguments) {
        ReportConfiguration reportConfig = new ReportConfiguration();
        try {
            reportConfig = new ReportConfigJsonParser().parse(cliArguments.getReportConfigFilePath());
        } catch (JsonSyntaxException jse) {
            logger.warning(String.format("%s is malformed.", cliArguments.getReportConfigFilePath()));
        } catch (IllegalArgumentException iae) {
            logger.warning(String.format(MESSAGE_INVALID_CONFIG_JSON, iae.getMessage()));
        } catch (IOException ioe) {
            // IOException thrown as report-config.json is not found.
            // Ignore exception as the file is optional.
        }
        return reportConfig;
    }
}
