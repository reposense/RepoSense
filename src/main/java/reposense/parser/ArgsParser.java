package reposense.parser;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Optional;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.impl.action.HelpArgumentAction;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import reposense.model.CliArguments;

/**
 * Verifies and parses a string-formatted date to a {@code CliArguments} object.
 */
public class ArgsParser {
    public static final String REPOSENSE_CONFIG_FOLDER = "_reposense";
    public static final String REPOSENSE_REPORT_FOLDER = "docs";

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

        parser.addArgument("-h", "-help")
                .help("Show help message.")
                .action(new HelpArgumentAction());

        parser.addArgument("genericInput")
                .metavar("PATH")
                .type(new GenericInputArgumentType())
                .help(GenericInput.VALID_TYPES_MESSAGE);

        parser.addArgument("-output")
                .type(Arguments.fileType().verifyExists().verifyIsDirectory().verifyCanWrite())
                .metavar("PATH")
                .setDefault(new File("."))
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

        return parser;
    }

    /**
     * Parses the given string arguments to a {@code CliArguments} object.
     *
     * @throws ParseException if the given string arguments fails to parse to a {@code CliArguments} object.
     */
    public static CliArguments parse(String[] args) throws ParseException {
        try {
            if (args.length == 0) {
                return new CliArguments(GenericInputArgumentType.getDefault());
            }

            ArgumentParser parser = getArgumentParser();
            Namespace results = parser.parseArgs(args);

            Optional<GenericInput> genericInput = results.get("genericInput");
            Optional<Date> sinceDate = results.get("since");
            Optional<Date> untilDate = results.get("until");
            Path outputPath = Paths.get(results.get("output").toString(), REPOSENSE_REPORT_FOLDER);
            verifyDatesRangeIsCorrect(sinceDate, untilDate);

            return new CliArguments(genericInput, sinceDate, untilDate, outputPath);
        } catch (ArgumentParserException ape) {
            System.out.println(ape.getMessage());
            if (ape.getMessage() == "Help Screen") {
                // ArgumentParser -h/-help will print help output to the console
                // Use empty string to indicate that we are not going to double print the help message again
                throw new ParseException("");
            }

            throw new ParseException(ape.getParser().formatHelp() + "\n");
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
