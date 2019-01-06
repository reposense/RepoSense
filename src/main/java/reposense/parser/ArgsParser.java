package reposense.parser;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import net.sourceforge.argparse4j.ArgumentParsers;
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

    private static ArgumentParser getArgumentParser() {
        ArgumentParser parser = ArgumentParsers
                .newFor(PROGRAM_USAGE)
                .addHelp(false)
                .build()
                .description(PROGRAM_DESCRIPTION);

        MutuallyExclusiveGroup mutexParser = parser
                .addMutuallyExclusiveGroup(PROGRAM_USAGE)
                .required(false);

        parser.addArgument("-h", "--help")
                .help("Show help message.")
                .action(new HelpArgumentAction());

        mutexParser.addArgument("-config")
                .type(new ConfigFolderArgumentType())
                .metavar("PATH")
                .setDefault(EMPTY_PATH.toAbsolutePath())
                .help("The directory containing the config files."
                        + "If not provided, the config files will be obtained from the current working directory.");

        mutexParser.addArgument("-repo", "-repos")
                .nargs("+")
                .dest("repos")
                .metavar("LOCATION")
                .help("The GitHub URL or disk locations to clone repository.");

        parser.addArgument("-view")
                .nargs("?")
                .metavar("PATH")
                .type(new ReportFolderArgumentType())
                .setConst(EMPTY_PATH)
                .help("Starts a server to display the dashboard in the provided directory."
                        + "If used as a flag (with no argument), "
                        + "generates a report and automatically displays the dashboard.");

        parser.addArgument("-output")
                .metavar("PATH")
                .type(new OutputFolderArgumentType())
                .setDefault(Paths.get(ArgsParser.DEFAULT_REPORT_NAME))
                .help("The directory to output the report folder, reposense-report. "
                        + "If not provided, the report folder will be created in the current working directory.");

        parser.addArgument("-since")
                .metavar("dd/MM/yyyy")
                .type(new DateArgumentType())
                .setDefault(Optional.empty())
                .help("The date to start filtering.");

        parser.addArgument("-until")
                .metavar("dd/MM/yyyy")
                .type(new DateArgumentType())
                .setDefault(Optional.empty())
                .help("The date to stop filtering.");

        parser.addArgument("-formats")
                .nargs("*")
                .metavar("FORMAT")
                .type(new AlphanumericArgumentType())
                .setDefault(Format.DEFAULT_FORMAT_STRINGS)
                .help("The alphanumeric file formats to process.\n"
                        + "If not provided, default file formats will be used.\n"
                        + "Please refer to userguide for more information.");

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

            Path configFolderPath = results.get("config");
            Path reportFolderPath = results.get("view");
            Path outputFolderPath = results.get("output");
            Optional<Date> sinceDate = results.get("since");
            Optional<Date> untilDate = results.get("until");
            List<String> locations = results.get("repos");
            List<Format> formats = Format.convertStringsToFormats(results.get("formats"));

            verifyDatesRangeIsCorrect(sinceDate, untilDate);

            if (reportFolderPath != null && !reportFolderPath.equals(EMPTY_PATH)) {
                return new ViewCliArguments(reportFolderPath);
            }

            boolean isAutomaticallyLaunching = reportFolderPath != null;

            if (locations != null) {
                return new LocationsCliArguments(
                        locations, outputFolderPath, sinceDate, untilDate, formats, isAutomaticallyLaunching);
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
