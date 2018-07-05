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
import reposense.util.Constants;

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

    private static ArgumentParser getArgumentParser() {
        ArgumentParser parser = ArgumentParsers
            .newFor(PROGRAM_USAGE)
            .addHelp(false)
            .build()
            .description(PROGRAM_DESCRIPTION);

        parser.addArgument("-h", "--help")
            .help("Show help message.")
            .action(new HelpArgumentAction());

        parser.addArgument("-config")
            .required(true)
            .type(Arguments.fileType().verifyExists().verifyIsFile().verifyCanRead())
            .metavar("PATH")
            .help("The path to the CSV config file to read.");

        parser.addArgument("-output")
                .metavar("PATH")
                .type(Arguments.fileType().verifyExists().verifyIsDirectory().verifyCanWrite())
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

        parser.addArgument("-doctype")
            .metavar("DOCTYPE")
            .type(String.class)
            .setDefault("java/adoc")
            .help("The document type to retrieve from Git commits");

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

            File configFile = results.get("config");
            File outputFile = results.get("output");
            Optional<Date> sinceDate = results.get("since");
            Optional<Date> untilDate = results.get("until");
            String docTypeString = results.get("doctype");
            Constants.setDocTypes(docTypeString.split("/"));

            Path configFilePath = configFile.toPath();
            Path outputFilePath = Paths.get(outputFile.toString(), DEFAULT_REPORT_NAME);

            verifyDatesRangeIsCorrect(sinceDate, untilDate);
            return new CliArguments(configFilePath, outputFilePath, sinceDate, untilDate);
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
