package reposense.parser;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.helper.HelpScreenException;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.impl.action.HelpArgumentAction;
import net.sourceforge.argparse4j.impl.action.VersionArgumentAction;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.MutuallyExclusiveGroup;
import net.sourceforge.argparse4j.inf.Namespace;
import reposense.RepoSense;
import reposense.model.CliArguments;
import reposense.model.ConfigCliArguments;
import reposense.model.Format;
import reposense.model.LocationsCliArguments;
import reposense.model.ViewCliArguments;
import reposense.system.LogsManager;

/**
 * Verifies and parses a string-formatted date to a {@code CliArguments} object.
 */
public class ArgsParser {
    public static final String DEFAULT_REPORT_NAME = "reposense-report";

    public static final String[] HELP_FLAGS = new String[]{"--help", "-h"};
    public static final String[] CONFIG_FLAGS = new String[]{"--config", "-c"};
    public static final String[] REPO_FLAGS = new String[]{"--repo", "--repos", "-r"};
    public static final String[] VIEW_FLAGS = new String[]{"--view", "-v"};
    public static final String[] OUTPUT_FLAGS = new String[]{"--output", "-o"};
    public static final String[] SINCE_FLAGS = new String[]{"--since", "-s"};
    public static final String[] UNTIL_FLAGS = new String[]{"--until", "-u"};
    public static final String[] FORMAT_FLAGS = new String[]{"--formats", "-f"};
    public static final String[] IGNORE_FLAGS = new String[]{"--ignore-standalone-config", "-i"};
    public static final String[] TIMEZONE_FLAGS = new String[]{"--timezone", "-t"};
    public static final String[] VERSION_FLAGS = new String[]{"--version", "-V"};

    private static final Logger logger = LogsManager.getLogger(ArgsParser.class);

    private static final String PROGRAM_USAGE = "java -jar RepoSense.jar";
    private static final String PROGRAM_DESCRIPTION =
            "RepoSense is a contribution analysis tool for Git repositories.";
    private static final String MESSAGE_HEADER_MUTEX = "mutual exclusive arguments";
    private static final String MESSAGE_SINCE_DATE_LATER_THAN_UNTIL_DATE =
            "\"Since Date\" cannot be later than \"Until Date\"";
    private static final String MESSAGE_USING_DEFAULT_CONFIG_PATH =
            "Config path not provided, using current working directory as default.";
    private static final Path EMPTY_PATH = Paths.get("");

    private static ArgumentParser getArgumentParser() {
        ArgumentParser parser = ArgumentParsers
                .newFor(PROGRAM_USAGE)
                .addHelp(false)
                .build()
                .description(PROGRAM_DESCRIPTION);

        MutuallyExclusiveGroup mutexParser = parser
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

        parser.addArgument(IGNORE_FLAGS)
                .dest(IGNORE_FLAGS[0])
                .action(Arguments.storeTrue())
                .help("A flag to ignore the standalone config file in the repo.");

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
                .type(new DateArgumentType())
                .setDefault(Optional.empty())
                .help("The date to start filtering.");

        parser.addArgument(UNTIL_FLAGS)
                .dest(UNTIL_FLAGS[0])
                .metavar("dd/MM/yyyy")
                .type(new DateArgumentType())
                .setDefault(Optional.empty())
                .help("The date to stop filtering.");

        parser.addArgument(FORMAT_FLAGS)
                .dest(FORMAT_FLAGS[0])
                .nargs("*")
                .metavar("FORMAT")
                .type(new AlphanumericArgumentType())
                .setDefault(Format.DEFAULT_FORMAT_STRINGS)
                .help("The alphanumeric file formats to process.\n"
                        + "If not provided, default file formats will be used.\n"
                        + "Please refer to userguide for more information.");

        // Mutex flags - these will always be the last parameters in help message.
        mutexParser.addArgument(CONFIG_FLAGS)
                .dest(CONFIG_FLAGS[0])
                .type(new ConfigFolderArgumentType())
                .metavar("PATH")
                .setDefault(EMPTY_PATH)
                .help("The directory containing the config files."
                        + "If not provided, the config files will be obtained from the current working directory.");
        mutexParser.addArgument(REPO_FLAGS)
                .nargs("+")
                .dest(REPO_FLAGS[0])
                .metavar("LOCATION")
                .help("The GitHub URL or disk locations to clone repository.");

        parser.addArgument(TIMEZONE_FLAGS)
                .dest(TIMEZONE_FLAGS[0])
                .metavar("ZONE_ID[±hh[mm]]")
                .type(new ZoneIdArgumentType())
                .setDefault(ZoneId.systemDefault())
                .help("The timezone to use for the generated report. "
                        + "One kind of valid timezones is relative to UTC. E.g. UTC, UTC+08, UTC-1030. \n"
                        + "If not provided, system default timezone will be used.");

        return parser;
    }

    /**
     * Parses the given string arguments to a {@code CliArguments} object.
     *
     * @throws HelpScreenException if given args contain the --help flag. Help message will be printed out
     * by the {@code ArgumentParser} hence this is to signal to the caller that the program is safe to exit.
     * @throws ParseException if the given string arguments fails to parse to a {@code CliArguments} object.
     */
    public static CliArguments parse(String[] args) throws HelpScreenException, ParseException {
        try {
            ArgumentParser parser = getArgumentParser();
            Namespace results = parser.parseArgs(args);

            Path configFolderPath = results.get(CONFIG_FLAGS[0]);
            Path reportFolderPath = results.get(VIEW_FLAGS[0]);
            Path outputFolderPath = results.get(OUTPUT_FLAGS[0]);
            Optional<Date> sinceDate = results.get(SINCE_FLAGS[0]);
            Optional<Date> untilDate = results.get(UNTIL_FLAGS[0]);
            List<String> locations = results.get(REPO_FLAGS[0]);
            List<Format> formats = Format.convertStringsToFormats(results.get(FORMAT_FLAGS[0]));
            boolean isStandaloneConfigIgnored = results.get(IGNORE_FLAGS[0]);
            ZoneId zoneId = results.get(TIMEZONE_FLAGS[0]);

            verifyDatesRangeIsCorrect(sinceDate, untilDate);

            if (reportFolderPath != null && !reportFolderPath.equals(EMPTY_PATH) && configFolderPath.equals(EMPTY_PATH)
                && locations == null) {
                return new ViewCliArguments(reportFolderPath);
            }

            boolean isAutomaticallyLaunching = reportFolderPath != null;

            if (isAutomaticallyLaunching && !reportFolderPath.equals(EMPTY_PATH)) {
                logger.info(String.format("Ignoring argument '%s' for --view.", reportFolderPath.toString()));
            }

            if (locations != null) {
                return new LocationsCliArguments(locations, outputFolderPath, sinceDate, untilDate, formats,
                        isAutomaticallyLaunching, isStandaloneConfigIgnored, zoneId);
            }

            if (configFolderPath.equals(EMPTY_PATH)) {
                logger.info(MESSAGE_USING_DEFAULT_CONFIG_PATH);
            }
            return new ConfigCliArguments(configFolderPath, outputFolderPath, sinceDate, untilDate, formats,
                    isAutomaticallyLaunching, zoneId);
        } catch (HelpScreenException hse) {
            throw hse;
        } catch (ArgumentParserException ape) {
            throw new ParseException(getArgumentParser().formatUsage() + ape.getMessage() + "\n");
        }
    }

    /**
     * Verifies that {@code sinceDate} is earlier than {@code untilDate}.
     *
     * @throws ParseException if {@code sinceDate} supplied is later than {@code untilDate}.
     */
    private static void verifyDatesRangeIsCorrect(Optional<Date> sinceDate, Optional<Date> untilDate)
            throws ParseException {
        if (sinceDate.isPresent() && untilDate.isPresent() && sinceDate.get().getTime() > untilDate.get().getTime()) {
            throw new ParseException(MESSAGE_SINCE_DATE_LATER_THAN_UNTIL_DATE);
        }
    }
}
