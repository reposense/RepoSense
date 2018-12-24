package reposense.parser;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
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
import reposense.model.LocationsCliArguments;
import reposense.model.ViewCliArguments;

/**
 * Verifies and parses a string-formatted date to a {@code CliArguments} object.
 */
public class ArgsParser {
    public static final String DEFAULT_REPORT_NAME = "reposense-report";
    public static final List<String> DEFAULT_FORMATS = Arrays.asList(
            "adoc", "cs", "css", "fxml", "gradle", "html", "java", "js", "json", "jsp", "md", "py", "tag", "xml");
    private static final String PROGRAM_USAGE = "java -jar RepoSense.jar";
    private static final String PROGRAM_DESCRIPTION =
            "RepoSense is a contribution analysis tool for Git repositories.";
    private static final String MESSAGE_SINCE_DATE_LATER_THAN_UNTIL_DATE =
            "\"Since Date\" cannot be later than \"Until Date\"";

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
                .setDefault(Paths.get("").toAbsolutePath())
                .help("The directory containing the config files."
                        + "If not provided, the config files will be obtained from the current working directory.");

        mutexParser.addArgument("-repo", "-repos")
                .nargs("+")
                .dest("repos")
                .metavar("LOCATION")
                .help("The GitHub URL or disk locations to clone repository.");

        mutexParser.addArgument("-view")
                .metavar("PATH")
                .type(new ReportFolderArgumentType())
                .help("Starts a server to display the dashboard in the provided directory.");

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
                .setDefault(DEFAULT_FORMATS)
                .help("The alphanumeric file formats to process.\n"
                        + "If not provided, default file formats will be used.\n"
                        + "Please refer to userguide for more information.");

        parser.addArgument("--ignore-standalone-config", "-isac")
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

            Path configFolderPath = results.get("config");
            Path reportFolderPath = results.get("view");
            Path outputFolderPath = results.get("output");
            Optional<Date> sinceDate = results.get("since");
            Optional<Date> untilDate = results.get("until");
            List<String> formats = results.get("formats");
            List<String> locations = results.get("repos");
            boolean isStandaloneConfigIgnored = results.get("ignore_standalone_config");

            verifyDatesRangeIsCorrect(sinceDate, untilDate);

            if (locations != null) {
                return new LocationsCliArguments(locations, outputFolderPath,
                        sinceDate, untilDate, formats, isStandaloneConfigIgnored);
            }

            if (reportFolderPath != null) {
                return new ViewCliArguments(reportFolderPath);
            }

            return new ConfigCliArguments(configFolderPath, outputFolderPath, sinceDate, untilDate, formats);
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
