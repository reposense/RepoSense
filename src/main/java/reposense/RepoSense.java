package reposense;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import net.sourceforge.argparse4j.helper.HelpScreenException;
import reposense.git.GitConfig;
import reposense.git.GitVersion;
import reposense.model.AuthorConfiguration;
import reposense.model.CliArguments;
import reposense.model.ConfigCliArguments;
import reposense.model.GroupConfiguration;
import reposense.model.LocationsCliArguments;
import reposense.model.RepoConfiguration;
import reposense.model.RepoLocation;
import reposense.model.ReportConfiguration;
import reposense.model.ViewCliArguments;
import reposense.parser.ArgsParser;
import reposense.parser.AuthorConfigCsvParser;
import reposense.parser.GroupConfigCsvParser;
import reposense.parser.InvalidCsvException;
import reposense.parser.InvalidHeaderException;
import reposense.parser.InvalidLocationException;
import reposense.parser.ParseException;
import reposense.parser.RepoConfigCsvParser;
import reposense.report.ReportGenerator;
import reposense.system.LogsManager;
import reposense.system.ReportServer;
import reposense.util.FileUtil;
import reposense.util.TimeUtil;

/**
 * The main RepoSense class.
 */
public class RepoSense {
    private static final Logger logger = LogsManager.getLogger(RepoSense.class);
    private static final int SERVER_PORT_NUMBER = 9000;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E MMM d HH:mm:ss yyyy z");
    private static final String VERSION_UNSPECIFIED = "unspecified";
    private static final String FINDING_PREVIOUS_AUTHORS_INVALID_VERSION_WARNING_MESSAGE =
            "--find-previous-authors/-F requires git version 2.23 and above. Feature will be disabled for this run";

    /**
     * The entry point of the program.
     * Additional flags are provided by the user in {@code args}.
     */
    public static void main(String[] args) {
        try {
            TimeUtil.startTimer();
            CliArguments cliArguments = ArgsParser.parse(args);
            List<RepoConfiguration> configs = null;
            ReportConfiguration reportConfig = new ReportConfiguration();

            if (cliArguments instanceof ViewCliArguments) {
                ReportServer.startServer(SERVER_PORT_NUMBER, ((
                        ViewCliArguments) cliArguments).getReportDirectoryPath().toAbsolutePath());
                return;
            } else if (cliArguments instanceof ConfigCliArguments) {
                configs = getRepoConfigurations((ConfigCliArguments) cliArguments);
                reportConfig = ((ConfigCliArguments) cliArguments).getReportConfiguration();
            } else if (cliArguments instanceof LocationsCliArguments) {
                configs = getRepoConfigurations((LocationsCliArguments) cliArguments);
            } else {
                throw new AssertionError("CliArguments's subclass type is unhandled.");
            }

            RepoConfiguration.setFormatsToRepoConfigs(configs, cliArguments.getFormats());
            RepoConfiguration.setDatesToRepoConfigs(configs, cliArguments.getSinceDate(), cliArguments.getUntilDate());
            RepoConfiguration.setZoneIdToRepoConfigs(configs, cliArguments.getZoneId());
            RepoConfiguration.setStandaloneConfigIgnoredToRepoConfigs(configs,
                    cliArguments.isStandaloneConfigIgnored());
            RepoConfiguration.setFileSizeLimitIgnoredToRepoConfigs(configs,
                    cliArguments.isFileSizeLimitIgnored());
            RepoConfiguration.setIsLastModifiedDateIncludedToRepoConfigs(configs,
                    cliArguments.isLastModifiedDateIncluded());
            RepoConfiguration.setIsShallowCloningPerformedToRepoConfigs(configs,
                    cliArguments.isShallowCloningPerformed());
            RepoConfiguration.setIsFindingPreviousAuthorsPerformedToRepoConfigs(configs,
                    cliArguments.isFindingPreviousAuthorsPerformed());

            if (RepoConfiguration.isAnyRepoFindingPreviousAuthors(configs)
                    && !GitVersion.isGitVersionSufficientForFindingPreviousAuthors()) {
                logger.warning(FINDING_PREVIOUS_AUTHORS_INVALID_VERSION_WARNING_MESSAGE);
                RepoConfiguration.setToFalseIsFindingPreviousAuthorsPerformedToRepoConfigs(configs);
            }

            List<String[]> globalGitConfig = GitConfig.getGlobalGitLfsConfig();
            if (globalGitConfig.size() != 0) {
                GitConfig.setGlobalGitLfsConfig(GitConfig.SKIP_SMUDGE_CONFIG_SETTINGS);
            }

            boolean isTestMode = cliArguments.isTestMode();
            if (isTestMode) {
                // Required by ConfigSystemTest to pass
                AuthorConfiguration.setHasAuthorConfigFile(false);
            }

            List<Path> reportFoldersAndFiles = ReportGenerator.generateReposReport(configs,
                    cliArguments.getOutputFilePath().toAbsolutePath().toString(),
                    cliArguments.getAssetsFilePath().toAbsolutePath().toString(), reportConfig,
                    formatter.format(ZonedDateTime.now(cliArguments.getZoneId())),
                    cliArguments.getSinceDate(), cliArguments.getUntilDate(),
                    cliArguments.isSinceDateProvided(), cliArguments.isUntilDateProvided(),
                    cliArguments.getNumCloningThreads(), cliArguments.getNumAnalysisThreads(),
                    TimeUtil::getElapsedTime, cliArguments.getZoneId(), cliArguments.isFreshClonePerformed());

            FileUtil.zipFoldersAndFiles(reportFoldersAndFiles, cliArguments.getOutputFilePath().toAbsolutePath(),
                    ".json");

            // Set back to user's initial global git lfs config
            GitConfig.setGlobalGitLfsConfig(globalGitConfig);

            logger.info(TimeUtil.getElapsedTimeMessage());

            if (cliArguments.isAutomaticallyLaunching()) {
                ReportServer.startServer(SERVER_PORT_NUMBER, cliArguments.getOutputFilePath().toAbsolutePath());
            }
        } catch (IOException | ParseException | InvalidCsvException | InvalidHeaderException e) {
            logger.log(Level.WARNING, e.getMessage(), e);
        } catch (HelpScreenException e) {
            // help message was printed by the ArgumentParser; it is safe to exit.
        }

        LogManager.getLogManager().reset();
    }

    /**
     * Constructs a list of {@link RepoConfiguration} if {@code cliArguments} is a {@link ConfigCliArguments}.
     *
     * @throws IOException if user-supplied csv file does not exist or is not readable.
     * @throws InvalidCsvException if user-supplied repo-config csv is malformed.
     * @throws InvalidHeaderException if user-supplied csv file has header that cannot be parsed.
     */
    public static List<RepoConfiguration> getRepoConfigurations(ConfigCliArguments cliArguments)
            throws IOException, InvalidCsvException, InvalidHeaderException {
        List<RepoConfiguration> repoConfigs = new RepoConfigCsvParser(cliArguments.getRepoConfigFilePath()).parse();
        List<AuthorConfiguration> authorConfigs;
        List<GroupConfiguration> groupConfigs;

        try {
            authorConfigs = new AuthorConfigCsvParser(cliArguments.getAuthorConfigFilePath()).parse();
            RepoConfiguration.merge(repoConfigs, authorConfigs);
            AuthorConfiguration.setHasAuthorConfigFile(true);
        } catch (FileNotFoundException fnfe) {
            // FileNotFoundException thrown as author-config.csv is not found.
            // Ignore exception as the file is optional.
        } catch (IOException | InvalidCsvException e) {
            // for all IO and invalid csv exceptions, log the error and continue
            logger.log(Level.WARNING, e.getMessage(), e);
        }

        try {
            groupConfigs = new GroupConfigCsvParser(cliArguments.getGroupConfigFilePath()).parse();
            RepoConfiguration.setGroupConfigsToRepos(repoConfigs, groupConfigs);
        } catch (FileNotFoundException fnfe) {
            // FileNotFoundException thrown as groups-config.csv is not found.
            // Ignore exception as the file is optional.
        } catch (IOException | InvalidCsvException e) {
            // for all other IO and invalid csv exceptions, log the error and continue
            logger.log(Level.WARNING, e.getMessage(), e);
        }

        return repoConfigs;
    }

    /**
     * Constructs a list of {@link RepoConfiguration} if {@code cliArguments} is a {@link LocationsCliArguments}.
     *
     * @throws ParseException if all repo locations are invalid.
     */
    public static List<RepoConfiguration> getRepoConfigurations(LocationsCliArguments cliArguments)
            throws ParseException {
        List<RepoConfiguration> configs = new ArrayList<>();
        for (String locationString : cliArguments.getLocations()) {
            try {
                configs.add(new RepoConfiguration(new RepoLocation(locationString)));
            } catch (InvalidLocationException ile) {
                logger.log(Level.WARNING, ile.getMessage(), ile);
            }
        }

        if (configs.isEmpty()) {
            throw new ParseException("All repository locations are invalid.");
        }

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
