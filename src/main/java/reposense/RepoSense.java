package reposense;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sourceforge.argparse4j.helper.HelpScreenException;
import reposense.model.AuthorConfiguration;
import reposense.model.CliArguments;
import reposense.model.ConfigCliArguments;
import reposense.model.LocationsCliArguments;
import reposense.model.RepoConfiguration;
import reposense.model.RepoLocation;
import reposense.model.ViewCliArguments;
import reposense.parser.ArgsParser;
import reposense.parser.AuthorConfigCsvParser;
import reposense.parser.InvalidLocationException;
import reposense.parser.ParseException;
import reposense.parser.RepoConfigCsvParser;
import reposense.report.ReportGenerator;
import reposense.report.SummaryReportJson;
import reposense.system.LogsManager;
import reposense.system.ReportServer;
import reposense.util.FileUtil;

/**
 * The main RepoSense class.
 */
public class RepoSense {
    private static final Logger logger = LogsManager.getLogger(RepoSense.class);
    private static final int SERVER_PORT_NUMBER = 9000;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E MMM d HH:mm:ss yyyy z");
    private static final String VERSION_UNSPECIFIED = "unspecified";

    /**
     * The entry point of the program.
     */
    public static void main(String[] args) {
        try {
            CliArguments cliArguments = ArgsParser.parse(args);
            List<RepoConfiguration> configs = null;

            if (cliArguments instanceof ViewCliArguments) {
                ReportServer.startServer(SERVER_PORT_NUMBER, ((
                        ViewCliArguments) cliArguments).getReportDirectoryPath().toAbsolutePath());
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
                    formatter.format(ZonedDateTime.now(cliArguments.getZoneId())),
                    cliArguments.getSinceDate().orElse(null),
                    cliArguments.getUntilDate().orElse(null));

            HashSet<Path> relevantFolders = getRelativeFolders(cliArguments.getOutputFilePath(), configs);
            HashSet<Path> relevantFiles = new HashSet<>();
            relevantFiles.add(Paths.get(SummaryReportJson.SUMMARY_JSON_FILE_NAME));
            FileUtil.zipRelativeFiles(relevantFolders, relevantFiles, cliArguments.getOutputFilePath().toAbsolutePath(),
                    ".json");

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
     * Returns a list of relevant repo folders to be zipped up later
     * @param sourcePath
     * @param configs
     * @return
     */
    private static HashSet<Path> getRelativeFolders(Path sourcePath, List<RepoConfiguration> configs) {
        HashSet<Path> relevantFolders = new HashSet<>();
        for (RepoConfiguration repoConfiguration : configs) {
            relevantFolders.add(Paths.get(sourcePath + File.separator + repoConfiguration.getDisplayName())
            .toAbsolutePath());
        }

        return relevantFolders;
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

    public static String getVersion() {
        String version = RepoSense.class.getPackage().getImplementationVersion();

        if (version == null || version.equals(VERSION_UNSPECIFIED)) {
            version = System.getProperty("version");
        }

        return version;
    }
}
