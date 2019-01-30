package reposense.parser;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.impl.action.HelpArgumentAction;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.MutuallyExclusiveGroup;
import net.sourceforge.argparse4j.inf.Namespace;
import reposense.model.CliArguments;
import reposense.model.ConfigCliArguments;
import reposense.model.Format;
import reposense.model.LocationsCliArguments;
import reposense.model.ViewCliArguments;

/**
 * Verifies and parses a string-formatted date to a {@code CliArguments} object.
 */
public class ArgsParser {
    public static final String DEFAULT_REPORT_NAME = "reposense-report";
    private static final String PROGRAM_USAGE = "java -jar RepoSense.jar";
    private static final String PROGRAM_DESCRIPTION =
            "RepoSense is a contribution analysis tool for Git repositories.";
    private static final String MESSAGE_SINCE_DATE_LATER_THAN_UNTIL_DATE =
            "\"Since Date\" cannot be later than \"Until Date\"";
    private static final Path EMPTY_PATH = Paths.get("");

    private static final String FULL_PREFIX = "--";
    private static final String ALIAS_PREFIX = "-";

    private static final String HELP_FLAG = "help";
    private static final String HELP_FLAG_ALIAS = "h";
    private static final String CONFIG_FLAG = "config";
    private static final String CONFIG_FLAG_ALIAS = "c";
    private static final String REPO_FLAG = "repos";
    private static final String REPO_FLAG_ALIAS = "r";
    private static final String VIEW_FLAG = "view";
    private static final String VIEW_FLAG_ALIAS = "v";
    private static final String OUTPUT_FLAG = "output";
    private static final String OUTPUT_FLAG_ALIAS = "o";
    private static final String SINCE_FLAG = "since";
    private static final String SINCE_FLAG_ALIAS = "s";
    private static final String UNTIL_FLAG = "until";
    private static final String UNTIL_FLAG_ALIAS = "u";
    private static final String FORMAT_FLAG = "formats";
    private static final String FORMAT_FLAG_ALIAS = "f";
    private static final String IGNORE_FLAG = "ignore-standalone-config";
    private static final String IGNORE_FLAG_ALIAS = "isac";


    private static ArgumentParser getArgumentParser() {
        ArgumentParser parser = ArgumentParsers
                .newFor(PROGRAM_USAGE)
                .addHelp(false)
                .build()
                .description(PROGRAM_DESCRIPTION);

        MutuallyExclusiveGroup mutexParser = parser
                .addMutuallyExclusiveGroup(PROGRAM_USAGE)
                .required(false);

        parser.addArgument(FULL_PREFIX + HELP_FLAG, ALIAS_PREFIX + HELP_FLAG_ALIAS)
                .help("Show help message.")
                .action(new HelpArgumentAction());

        mutexParser.addArgument(FULL_PREFIX + CONFIG_FLAG, ALIAS_PREFIX + CONFIG_FLAG_ALIAS)
                .type(new ConfigFolderArgumentType())
                .metavar("PATH")
                .setDefault(EMPTY_PATH.toAbsolutePath())
                .help("The directory containing the config files."
                        + "If not provided, the config files will be obtained from the current working directory.");

        mutexParser.addArgument(FULL_PREFIX + REPO_FLAG, ALIAS_PREFIX + REPO_FLAG_ALIAS)
                .nargs("+")
                .dest("repos")
                .metavar("LOCATION")
                .help("The GitHub URL or disk locations to clone repository.");

        parser.addArgument(FULL_PREFIX + VIEW_FLAG, ALIAS_PREFIX + VIEW_FLAG_ALIAS)
                .nargs("?")
                .metavar("PATH")
                .type(new ReportFolderArgumentType())
                .setConst(EMPTY_PATH)
                .help("Starts a server to display the dashboard in the provided directory."
                        + "If used as a flag (with no argument), "
                        + "generates a report and automatically displays the dashboard.");

        parser.addArgument(FULL_PREFIX + OUTPUT_FLAG, ALIAS_PREFIX + OUTPUT_FLAG_ALIAS)
                .metavar("PATH")
                .type(new OutputFolderArgumentType())
                .setDefault(Paths.get(ArgsParser.DEFAULT_REPORT_NAME))
                .help("The directory to output the report folder, reposense-report. "
                        + "If not provided, the report folder will be created in the current working directory.");

        parser.addArgument(FULL_PREFIX + SINCE_FLAG, ALIAS_PREFIX + SINCE_FLAG_ALIAS)
                .metavar("dd/MM/yyyy")
                .type(new DateArgumentType())
                .setDefault(Optional.empty())
                .help("The date to start filtering.");

        parser.addArgument(FULL_PREFIX + UNTIL_FLAG, ALIAS_PREFIX + UNTIL_FLAG_ALIAS)
                .metavar("dd/MM/yyyy")
                .type(new DateArgumentType())
                .setDefault(Optional.empty())
                .help("The date to stop filtering.");

        parser.addArgument(FULL_PREFIX + FORMAT_FLAG, ALIAS_PREFIX + FORMAT_FLAG_ALIAS)
                .nargs("*")
                .metavar("FORMAT")
                .type(new AlphanumericArgumentType())
                .setDefault(Format.DEFAULT_FORMAT_STRINGS)
                .help("The alphanumeric file formats to process.\n"
                        + "If not provided, default file formats will be used.\n"
                        + "Please refer to userguide for more information.");

        parser.addArgument(FULL_PREFIX + IGNORE_FLAG, ALIAS_PREFIX + IGNORE_FLAG_ALIAS)
                .action(Arguments.storeTrue())
                .help("A flag to ignore the standalone config file in the repo.");

        return parser;
    }

    /**
     * Parses the given string arguments to a {@code CliArguments} object.
     *
     * @throws ParseException if the given string arguments fails to parse to a {@code CliArguments} object.
     */
    public static CliArguments parse(String[] args) throws ParseException {
        try {
            ArgumentParser parser = getArgumentParser();
            Namespace results = parser.parseArgs(args);

            Path configFolderPath = results.get(CONFIG_FLAG);
            Path reportFolderPath = results.get(VIEW_FLAG);
            Path outputFolderPath = results.get(OUTPUT_FLAG);
            Optional<Date> sinceDate = results.get(SINCE_FLAG);
            Optional<Date> untilDate = results.get(UNTIL_FLAG);
            List<String> locations = results.get(REPO_FLAG);
            List<Format> formats = Format.convertStringsToFormats(results.get(FORMAT_FLAG));
            boolean isStandaloneConfigIgnored = results.get(IGNORE_FLAG.replace("-", "_"));

            verifyDatesRangeIsCorrect(sinceDate, untilDate);

            if (reportFolderPath != null && !reportFolderPath.equals(EMPTY_PATH)) {
                return new ViewCliArguments(reportFolderPath);
            }

            boolean isAutomaticallyLaunching = reportFolderPath != null;

            if (locations != null) {
                return new LocationsCliArguments(locations, outputFolderPath, sinceDate, untilDate, formats,
                        isAutomaticallyLaunching, isStandaloneConfigIgnored);
            }

            return new ConfigCliArguments(
                    configFolderPath, outputFolderPath, sinceDate, untilDate, formats, isAutomaticallyLaunching);
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
