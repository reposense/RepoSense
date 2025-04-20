package reposense.parser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.JsonSyntaxException;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.helper.HelpScreenException;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.impl.action.HelpArgumentAction;
import net.sourceforge.argparse4j.impl.action.VersionArgumentAction;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.FeatureControl;
import net.sourceforge.argparse4j.inf.MutuallyExclusiveGroup;
import net.sourceforge.argparse4j.inf.Namespace;
import reposense.RepoSense;
import reposense.model.AuthorBlurbMap;
import reposense.model.ChartBlurbMap;
import reposense.model.CliArguments;
import reposense.model.FileType;
import reposense.model.RepoBlurbMap;
import reposense.model.reportconfig.ReportConfiguration;
import reposense.parser.exceptions.InvalidMarkdownException;
import reposense.parser.exceptions.ParseException;
import reposense.parser.types.AlphanumericArgumentType;
import reposense.parser.types.AnalysisThreadsArgumentType;
import reposense.parser.types.CloningThreadsArgumentType;
import reposense.parser.types.ConfigFolderArgumentType;
import reposense.parser.types.OutputFolderArgumentType;
import reposense.parser.types.PeriodArgumentType;
import reposense.parser.types.ReportFolderArgumentType;
import reposense.parser.types.SinceDateArgumentType;
import reposense.parser.types.UntilDateArgumentType;
import reposense.parser.types.ZoneIdArgumentType;
import reposense.system.LogsManager;
import reposense.util.TimeUtil;

/**
 * Verifies and parses a string-formatted date to a {@link CliArguments} object.
 */
public class ArgsParser {
    public static final String DEFAULT_REPORT_NAME = "reposense-report";
    public static final int DEFAULT_NUM_CLONING_THREADS = 4;
    public static final int DEFAULT_NUM_ANALYSIS_THREADS = Runtime.getRuntime().availableProcessors();
    public static final boolean DEFAULT_SHOULD_FRESH_CLONE = false;
    public static final double DEFAULT_ORIGINALITY_THRESHOLD = 0.51;

    public static final String[] HELP_FLAGS = new String[] {"--help", "-h"};
    public static final String[] CONFIG_FLAGS = new String[] {"--config", "-c"};
    public static final String[] REPO_FLAGS = new String[] {"--repo", "--repos", "-r"};
    public static final String[] VIEW_FLAGS = new String[] {"--view", "-v"};
    public static final String[] OUTPUT_FLAGS = new String[] {"--output", "-o"};
    public static final String[] SINCE_FLAGS = new String[] {"--since", "-s"};
    public static final String[] UNTIL_FLAGS = new String[] {"--until", "-u"};
    public static final String[] PERIOD_FLAGS = new String[] {"--period", "-p"};
    public static final String[] SHALLOW_CLONING_FLAGS = new String[] {"--shallow-cloning", "-S"};
    public static final String[] FORMAT_FLAGS = new String[] {"--formats", "-f"};
    public static final String[] IGNORE_CONFIG_FLAGS = new String[] {"--ignore-standalone-config", "-i"};
    public static final String[] IGNORE_SIZELIMIT_FLAGS = new String[] {"--ignore-filesize-limit", "-I"};
    public static final String[] TIMEZONE_FLAGS = new String[] {"--timezone", "-t"};
    public static final String[] VERSION_FLAGS = new String[] {"--version", "-V"};
    public static final String[] LAST_MODIFIED_DATE_FLAGS = new String[] {"--last-modified-date", "-l"};
    public static final String[] FIND_PREVIOUS_AUTHORS_FLAGS = new String[] {"--find-previous-authors", "-F"};
    public static final String[] CLONING_THREADS_FLAG = new String[] {"--cloning-threads"};
    public static final String[] ANALYSIS_THREADS_FLAG = new String[] {"--analysis-threads"};
    public static final String[] FRESH_CLONING_FLAG = new String[] {"--fresh-cloning"};
    public static final String[] ANALYZE_AUTHORSHIP_FLAGS = new String[] {"--analyze-authorship", "-A"};
    public static final String[] ORIGINALITY_THRESHOLD_FLAGS = new String[] {"--originality-threshold", "-ot"};
    public static final String[] PORTFOLIO_FLAG = new String[] {"--portfolio", "-P"};
    public static final String[] REFRESH_ONLY_TEXT_FLAG = new String[] {"--text", "-T"};

    private static final Logger logger = LogsManager.getLogger(ArgsParser.class);

    private static final String PROGRAM_USAGE = "java -jar RepoSense.jar";
    private static final String PROGRAM_DESCRIPTION =
            "RepoSense is a contribution analysis tool for Git repositories.";
    private static final String MESSAGE_HEADER_MUTEX = "mutual exclusive arguments";
    private static final String MESSAGE_HAVE_SINCE_DATE_UNTIL_DATE_AND_PERIOD =
            "\"Since Date\", \"Until Date\", and \"Period\" cannot be applied together.";
    private static final String MESSAGE_USING_DEFAULT_CONFIG_PATH =
            "Config path not provided, using the config folder as default.";
    private static final String MESSAGE_INVALID_CONFIG_PATH = "%s is malformed.";
    private static final String MESSAGE_INVALID_CONFIG_YAML = "%s Ignoring the report config provided.";
    private static final String MESSAGE_INVALID_MARKDOWN_BLURBS = "%s Ignoring the blurb file provided.";
    private static final String MESSAGE_SINCE_D1_WITH_PERIOD = "You may be using --since d1 with the --period flag. "
            + "This may result in an incorrect date range being analysed.";
    private static final String MESSAGE_SINCE_DATE_LATER_THAN_UNTIL_DATE =
            "\"Since Date\" cannot be later than \"Until Date\".";
    private static final String MESSAGE_SINCE_DATE_LATER_THAN_TODAY_DATE =
            "\"Since Date\" must not be later than today's date.";
    private static final Path EMPTY_PATH = Paths.get("");
    private static final Path DEFAULT_CONFIG_PATH = Paths.get(System.getProperty("user.dir")
            + File.separator + "config" + File.separator);
    private static final Path DEFAULT_ASSETS_PATH = Paths.get(System.getProperty("user.dir")
            + File.separator + "assets" + File.separator);

    private static ArgumentParser getArgumentParser() {
        ArgumentParser parser = ArgumentParsers
                .newFor(PROGRAM_USAGE)
                .addHelp(false)
                .build()
                .description(PROGRAM_DESCRIPTION);

        MutuallyExclusiveGroup mutexParser = parser
                .addMutuallyExclusiveGroup(MESSAGE_HEADER_MUTEX)
                .required(false);

        MutuallyExclusiveGroup mutexParser2 = parser
                .addMutuallyExclusiveGroup(MESSAGE_HEADER_MUTEX)
                .required(false);

        // Boolean flags
        parser.addArgument(HELP_FLAGS)
                .help("Show help message.")
                .action(new HelpArgumentAction());

        parser.version("RepoSense " + RepoSense.getVersion());
        parser.addArgument(VERSION_FLAGS)
                .help("Show the version of RepoSense.")
                .action(new VersionArgumentAction());

        parser.addArgument(IGNORE_CONFIG_FLAGS)
                .dest(IGNORE_CONFIG_FLAGS[0])
                .action(Arguments.storeTrue())
                .help("A flag to ignore the standalone config file in the repo.");

        parser.addArgument(IGNORE_SIZELIMIT_FLAGS)
                .dest(IGNORE_SIZELIMIT_FLAGS[0])
                .action(Arguments.storeTrue())
                .help("A flag to ignore the filesize limit for analyzed files.");

        parser.addArgument(VIEW_FLAGS)
                .dest(VIEW_FLAGS[0])
                .nargs("?")
                .metavar("PATH")
                .type(new ReportFolderArgumentType())
                .setConst(EMPTY_PATH)
                .help("Starts a server to display the report in the provided directory. "
                        + "If used as a flag (with no argument), "
                        + "generates a report and automatically displays the report.");

        parser.addArgument(OUTPUT_FLAGS)
                .dest(OUTPUT_FLAGS[0])
                .metavar("PATH")
                .type(new OutputFolderArgumentType())
                .setDefault(Paths.get(ArgsParser.DEFAULT_REPORT_NAME))
                .help("The directory to output the report folder, reposense-report. "
                        + "If not provided, the report folder will be created in the current working directory.");

        parser.addArgument(SINCE_FLAGS)
                .dest(SINCE_FLAGS[0])
                .metavar("dd/MM/yyyy")
                .type(new SinceDateArgumentType())
                .setDefault(Optional.empty())
                .help("The date to start filtering.");

        parser.addArgument(UNTIL_FLAGS)
                .dest(UNTIL_FLAGS[0])
                .metavar("dd/MM/yyyy")
                .type(new UntilDateArgumentType())
                .setDefault(Optional.empty())
                .help("The date to stop filtering.");

        parser.addArgument(PERIOD_FLAGS)
                .dest(PERIOD_FLAGS[0])
                .metavar("PERIOD")
                .type(new PeriodArgumentType())
                .setDefault(Optional.empty())
                .help("The number of days of the filtering window.");

        parser.addArgument(FORMAT_FLAGS)
                .dest(FORMAT_FLAGS[0])
                .nargs("*")
                .metavar("FORMAT")
                .type(new AlphanumericArgumentType())
                .setDefault(Collections.emptyList())
                .help("The alphanumeric file formats to process.\n"
                        + "If not provided, default file formats will be used.\n"
                        + "Please refer to userguide for more information.");

        parser.addArgument(TIMEZONE_FLAGS)
                .dest(TIMEZONE_FLAGS[0])
                .metavar("ZONE_ID[±hh[mm]]")
                .type(new ZoneIdArgumentType())
                .setDefault(ZoneId.systemDefault())
                .help("The timezone to use for the generated report. "
                        + "One kind of valid timezones is relative to UTC. E.g. UTC, UTC+08, UTC-1030. \n"
                        + "If not provided, system default timezone will be used.");

        parser.addArgument(FIND_PREVIOUS_AUTHORS_FLAGS)
                .dest(FIND_PREVIOUS_AUTHORS_FLAGS[0])
                .action(Arguments.storeTrue())
                .help("A flag to make RepoSense utilize Git blame's ignore revisions functionality, RepoSense "
                        + "will attempt to blame the line changes caused by commits in the ignore commit list to the "
                        + "previous authors who altered those lines (if available)");

        parser.addArgument(ANALYZE_AUTHORSHIP_FLAGS)
                .dest(ANALYZE_AUTHORSHIP_FLAGS[0])
                .action(Arguments.storeTrue())
                .help("Performs further analysis to distinguish between partial and full credit attribution for "
                        + "lines of code assigned to the author. A darker background colour represents full credit, "
                        + "while a lighter background colour represents partial credit.");

        parser.addArgument(ORIGINALITY_THRESHOLD_FLAGS)
                .dest(ORIGINALITY_THRESHOLD_FLAGS[0])
                .metavar("(0.0 ~ 1.0)")
                .type(new OriginalityThresholdArgumentType())
                .setDefault(DEFAULT_ORIGINALITY_THRESHOLD)
                .help("Specifies the cut-off point for partial and full credit when further analysis of authorship "
                        + "is performed. Author will be given full credit if their contribution exceeds this "
                        + "threshold, else partial credit is given.");

        parser.addArgument(PORTFOLIO_FLAG)
                .dest(PORTFOLIO_FLAG[0])
                .action(Arguments.storeTrue())
                .help("Generates an optimized report for code portfolio pages");

        parser.addArgument(REFRESH_ONLY_TEXT_FLAG)
                .dest(REFRESH_ONLY_TEXT_FLAG[0])
                .action(Arguments.storeTrue())
                .help("Refreshes only the text content of the report, without analyzing the repositories again.");

        // Mutex flags - these will always be the last parameters in help message.
        mutexParser.addArgument(CONFIG_FLAGS)
                .dest(CONFIG_FLAGS[0])
                .type(new ConfigFolderArgumentType())
                .metavar("PATH")
                .setDefault(DEFAULT_CONFIG_PATH)
                .help("The directory containing the config files."
                        + "If not provided, the config files will be obtained from the config folder.");

        mutexParser.addArgument(REPO_FLAGS)
                .nargs("+")
                .dest(REPO_FLAGS[0])
                .metavar("LOCATION")
                .help("The remote Git host URLs or local directories to clone the repositories from.");

        mutexParser2.addArgument(LAST_MODIFIED_DATE_FLAGS)
                .dest(LAST_MODIFIED_DATE_FLAGS[0])
                .action(Arguments.storeTrue())
                .help("A flag to keep track of the last modified date of each line of code.");

        mutexParser2.addArgument(SHALLOW_CLONING_FLAGS)
                .dest(SHALLOW_CLONING_FLAGS[0])
                .action(Arguments.storeTrue())
                .help("A flag to make RepoSense employ Git's shallow cloning functionality, which can significantly "
                        + "reduce the time taken to clone large repositories. This flag should not be used for "
                        + "smaller repositories, where the .git file is smaller than 500 MB.");

        parser.addArgument(CLONING_THREADS_FLAG)
                .dest(CLONING_THREADS_FLAG[0])
                .type(new CloningThreadsArgumentType())
                .setDefault(DEFAULT_NUM_CLONING_THREADS)
                .help(FeatureControl.SUPPRESS);

        parser.addArgument(ANALYSIS_THREADS_FLAG)
                .dest(ANALYSIS_THREADS_FLAG[0])
                .type(new AnalysisThreadsArgumentType())
                .setDefault(DEFAULT_NUM_ANALYSIS_THREADS)
                .help(FeatureControl.SUPPRESS);

        // Testing flags
        parser.addArgument(FRESH_CLONING_FLAG)
                .dest(FRESH_CLONING_FLAG[0])
                .action(Arguments.storeTrue())
                .help("Enables fresh cloning.");

        return parser;
    }

    /**
     * Parses the given string {@code args} to a {@link CliArguments} object.
     *
     * @throws HelpScreenException if given args contain the --help flag. Help message will be printed out
     * by the {@link ArgumentParser} hence this is to signal to the caller that the program is safe to exit.
     * @throws ParseException if the given string arguments fails to parse to a {@link CliArguments} object.
     */
    public static CliArguments parse(String[] args) throws HelpScreenException, ParseException {
        ArgumentParser parser = getArgumentParser();
        Namespace results;

        try {
            results = parser.parseArgs(args);
        } catch (HelpScreenException hse) {
            throw hse;
        } catch (ArgumentParserException ape) {
            throw new ParseException(getArgumentParser().formatUsage() + ape.getMessage() + "\n");
        }

        Path configFolderPath = results.get(CONFIG_FLAGS[0]);
        Path reportFolderPath = results.get(VIEW_FLAGS[0]);
        Path outputFolderPath = results.get(OUTPUT_FLAGS[0]);
        ZoneId zoneId = results.get(TIMEZONE_FLAGS[0]);
        List<String> locations = results.get(REPO_FLAGS[0]);
        List<FileType> formats = FileType.convertFormatStringsToFileTypes(results.get(FORMAT_FLAGS[0]));
        boolean isStandaloneConfigIgnored = results.get(IGNORE_CONFIG_FLAGS[0]);
        boolean isFileSizeLimitIgnored = results.get(IGNORE_SIZELIMIT_FLAGS[0]);
        boolean shouldIncludeLastModifiedDate = results.get(LAST_MODIFIED_DATE_FLAGS[0]);
        boolean shouldPerformShallowCloning = results.get(SHALLOW_CLONING_FLAGS[0]);
        boolean shouldFindPreviousAuthors = results.get(FIND_PREVIOUS_AUTHORS_FLAGS[0]);
        boolean isAuthorshipAnalyzed = results.get(ANALYZE_AUTHORSHIP_FLAGS[0]);
        double originalityThreshold = results.get(ORIGINALITY_THRESHOLD_FLAGS[0]);
        boolean isPortfolio = results.get(PORTFOLIO_FLAG[0]);
        int numCloningThreads = results.get(CLONING_THREADS_FLAG[0]);
        int numAnalysisThreads = results.get(ANALYSIS_THREADS_FLAG[0]);
        boolean shouldPerformFreshCloning = results.get(FRESH_CLONING_FLAG[0]);
        boolean shouldRefreshOnlyText = results.get(REFRESH_ONLY_TEXT_FLAG[0]);

        CliArguments.Builder cliArgumentsBuilder = new CliArguments.Builder()
                .configFolderPath(configFolderPath)
                .reportDirectoryPath(reportFolderPath)
                .outputFilePath(outputFolderPath)
                .zoneId(zoneId)
                .locations(locations)
                .formats(formats)
                .isStandaloneConfigIgnored(isStandaloneConfigIgnored)
                .isFileSizeLimitIgnored(isFileSizeLimitIgnored)
                .isLastModifiedDateIncluded(shouldIncludeLastModifiedDate)
                .isShallowCloningPerformed(shouldPerformShallowCloning)
                .isFindingPreviousAuthorsPerformed(shouldFindPreviousAuthors)
                .numCloningThreads(numCloningThreads)
                .numAnalysisThreads(numAnalysisThreads)
                .isAuthorshipAnalyzed(isAuthorshipAnalyzed)
                .originalityThreshold(originalityThreshold)
                .isPortfolio(isPortfolio)
                .isFreshClonePerformed(shouldPerformFreshCloning)
                .isOnlyTextRefreshed(shouldRefreshOnlyText);

        LogsManager.setLogFolderLocation(outputFolderPath);

        if (locations == null && configFolderPath.equals(DEFAULT_CONFIG_PATH)) {
            logger.info(MESSAGE_USING_DEFAULT_CONFIG_PATH);
        }

        addReportConfigToBuilder(cliArgumentsBuilder, results);
        addRepoBlurbMapToBuilder(cliArgumentsBuilder, results);
        addAuthorBlurbMapToBuilder(cliArgumentsBuilder, results);
        addChartBlurbMapToBuilder(cliArgumentsBuilder, results);
        addAnalysisDatesToBuilder(cliArgumentsBuilder, results);

        boolean isViewModeOnly = reportFolderPath != null
                && !reportFolderPath.equals(EMPTY_PATH)
                && configFolderPath.equals(DEFAULT_CONFIG_PATH)
                && locations == null;
        cliArgumentsBuilder.isViewModeOnly(isViewModeOnly);

        boolean isAutomaticallyLaunching = reportFolderPath != null;
        if (isAutomaticallyLaunching && !reportFolderPath.equals(EMPTY_PATH) && !isViewModeOnly) {
            logger.info(String.format("Ignoring argument '%s' for --view.", reportFolderPath.toString()));
        }
        cliArgumentsBuilder.isAutomaticallyLaunching(isAutomaticallyLaunching);

        return cliArgumentsBuilder.build();
    }

    /**
     * Adds the reportConfig field to the given {@code builder}.
     *
     * @param builder Builder to be supplied with the reportConfig field.
     * @param results Parsed results of the user-supplied CLI arguments.
     */
    private static void addReportConfigToBuilder(CliArguments.Builder builder, Namespace results) {
        List<String> locations = results.get(REPO_FLAGS[0]);
        Path configFolderPath = results.get(CONFIG_FLAGS[0]);

        // Report config is ignored if --repos is provided
        if (locations == null) {
            Path reportConfigFilePath = configFolderPath.resolve(ReportConfigYamlParser.REPORT_CONFIG_FILENAME);

            try {
                ReportConfiguration reportConfig = new ReportConfigYamlParser().parse(reportConfigFilePath);
                builder.reportConfiguration(reportConfig);
            } catch (JsonSyntaxException jse) {
                logger.warning(String.format(MESSAGE_INVALID_CONFIG_PATH, reportConfigFilePath));
            } catch (IOException ioe) {
                // IOException thrown as report-config.yaml is not found or fields are invalid.
                logger.log(Level.WARNING, "Error parsing report-config.yaml: " + ioe.getMessage() + "\n"
                        + String.format(MESSAGE_INVALID_CONFIG_YAML, reportConfigFilePath));
            }
        }
    }

    /**
     * Adds the repoBlurbMap field to the given {@code builder}.
     *
     * @param builder Builder to be supplied with the reportConfig field.
     * @param results Parsed results of the user-supplied CLI arguments.
     */
    private static void addRepoBlurbMapToBuilder(CliArguments.Builder builder, Namespace results) {
        RepoBlurbMap repoBlurbMap = new RepoBlurbMap();
        Path configFolderPath = results.get(CONFIG_FLAGS[0]);

        // Blurbs are parsed regardless
        Path blurbConfigPath = configFolderPath.resolve(RepoBlurbMarkdownParser.DEFAULT_BLURB_FILENAME);

        try {
            repoBlurbMap = new RepoBlurbMarkdownParser(blurbConfigPath).parse();
        } catch (InvalidMarkdownException ex) {
            logger.warning(String.format(MESSAGE_INVALID_MARKDOWN_BLURBS, ex.getMessage()));
        } catch (IOException ioe) {
            // IOException thrown as blurbs.md is not found.
            // Ignore exception as the file is optional.
        }

        builder.repoBlurbMap(repoBlurbMap);
    }

    /**
     * Adds the authorBlurbMap field to the given {@code builder}.
     *
     * @param builder Builder to be supplied with the reportConfig field.
     * @param results Parsed results of the user-supplied CLI arguments.
     */
    private static void addAuthorBlurbMapToBuilder(CliArguments.Builder builder, Namespace results) {
        AuthorBlurbMap authorBlurbMap = new AuthorBlurbMap();
        Path configFolderPath = results.get(CONFIG_FLAGS[0]);

        // Blurbs are parsed regardless
        Path blurbConfigPath = configFolderPath.resolve(AuthorBlurbMarkdownParser.DEFAULT_BLURB_FILENAME);

        try {
            authorBlurbMap = new AuthorBlurbMarkdownParser(blurbConfigPath).parse();
        } catch (InvalidMarkdownException ex) {
            logger.warning(String.format(MESSAGE_INVALID_MARKDOWN_BLURBS, ex.getMessage()));
        } catch (IOException ioe) {
            // IOException thrown as blurbs.md is not found.
            // Ignore exception as the file is optional.
        }

        builder.authorBlurbMap(authorBlurbMap);
    }

    /**
     * Adds the chartBlurbMap field to the given {@code builder}.
     *
     * @param builder Builder to be supplied with the reportConfig field.
     * @param results Parsed results of the user-supplied CLI arguments.
     */
    private static void addChartBlurbMapToBuilder(CliArguments.Builder builder, Namespace results) {
        ChartBlurbMap chartBlurbMap = new ChartBlurbMap();
        Path configFolderPath = results.get(CONFIG_FLAGS[0]);

        // Blurbs are parsed regardless
        Path blurbConfigPath = configFolderPath.resolve(ChartBlurbMarkdownParser.DEFAULT_BLURB_FILENAME);

        try {
            chartBlurbMap = new ChartBlurbMarkdownParser(blurbConfigPath).parse();
        } catch (InvalidMarkdownException ex) {
            logger.warning(String.format(MESSAGE_INVALID_MARKDOWN_BLURBS, ex.getMessage()));
        } catch (IOException ioe) {
            // IOException thrown as blurbs.md is not found.
            // Ignore exception as the file is optional.
        }

        builder.chartBlurbMap(chartBlurbMap);
    }

    /**
     * Adds the sinceDate and untilDate fields for analysis to the given {@code builder}.
     *
     * @param builder Builder to be supplied with the sinceDate and untilDate fields
     * @param results Parsed results of the user-supplied CLI arguments.
     * @throws ParseException if all of sinceDate, untilDate and (analysis) period is provided
     * or if the sinceDate is invalid.
     */
    private static void addAnalysisDatesToBuilder(CliArguments.Builder builder, Namespace results)
            throws ParseException {
        ZoneId zoneId = results.get(TIMEZONE_FLAGS[0]);
        Optional<LocalDateTime> cliSinceDate = results.get(SINCE_FLAGS[0]);
        Optional<LocalDateTime> cliUntilDate = results.get(UNTIL_FLAGS[0]);
        Optional<Integer> cliPeriod = results.get(PERIOD_FLAGS[0]);

        boolean isSinceDateProvided = cliSinceDate.isPresent();
        boolean isUntilDateProvided = cliUntilDate.isPresent();
        boolean isPeriodProvided = cliPeriod.isPresent();
        if (isSinceDateProvided && isUntilDateProvided && isPeriodProvided) {
            throw new ParseException(MESSAGE_HAVE_SINCE_DATE_UNTIL_DATE_AND_PERIOD);
        }

        LocalDateTime sinceDate;
        LocalDateTime untilDate;
        boolean isUsingArbitraryDate = false;

        LocalDateTime currentDate = TimeUtil.getCurrentDate(zoneId);

        if (isSinceDateProvided) {
            sinceDate = TimeUtil.getValidDate(cliSinceDate.get());
            // For --since d1, need to adjust the arbitrary date based on timezone
            if (TimeUtil.isEqualToArbitraryFirstDateUtc(sinceDate)) {
                isUsingArbitraryDate = true;
                sinceDate = TimeUtil.getArbitraryFirstCommitDateConverted(zoneId);
            }
        } else if (isUntilDateProvided) {
            sinceDate = isPeriodProvided
                    ? TimeUtil.getDateMinusNDays(cliUntilDate.get(), cliPeriod.get())
                    : TimeUtil.getDateMinusAMonth(cliUntilDate.get());
        } else {
            sinceDate = isPeriodProvided
                    ? TimeUtil.getDateMinusNDays(currentDate, cliPeriod.get())
                    : TimeUtil.getDateMinusAMonth(currentDate);
        }

        if (isPeriodProvided && isUsingArbitraryDate) {
            logger.warning(MESSAGE_SINCE_D1_WITH_PERIOD);
        }

        if (isUntilDateProvided) {
            untilDate = TimeUtil.getValidDate(cliUntilDate.get());
        } else {
            untilDate = (isSinceDateProvided && isPeriodProvided)
                    ? TimeUtil.getDatePlusNDays(cliSinceDate.get(), cliPeriod.get())
                    : currentDate;
        }

        untilDate = untilDate.compareTo(currentDate) < 0
                ? untilDate
                : currentDate;

        if (sinceDate.compareTo(currentDate) > 0) {
            throw new ParseException(MESSAGE_SINCE_DATE_LATER_THAN_TODAY_DATE);
        }

        if (sinceDate.compareTo(untilDate) > 0) {
            throw new ParseException(MESSAGE_SINCE_DATE_LATER_THAN_UNTIL_DATE);
        }

        builder.sinceDate(sinceDate)
                .isSinceDateProvided(isSinceDateProvided)
                .untilDate(untilDate)
                .isUntilDateProvided(isUntilDateProvided);
    }
}
