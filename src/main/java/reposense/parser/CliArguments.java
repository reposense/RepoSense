package reposense.parser;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Optional;

import reposense.exception.ParseException;

/**
 * Verifies and parses user-supplied CLI arguments.
 */
public class CliArguments {

    private static final String ARGUMENT_PREFIX_DASH = "-";

    private static final String CONFIG_FILE_ARG = "config";
    private static final String TARGET_FILE_ARG = "output";
    private static final String SINCE_DATE_ARG = "since";
    private static final String UNTIL_DATE_ARG = "until";

    private static final String PARSE_EXCEPTION_MESSAGE_INVALID_INPUTS = "Failed to parse inputs arguments";
    private static final String PARSE_EXCEPTION_MESSAGE_NO_CSV_FILE = "Failed due to missing CSV file";

    private static final String DEFAULT_FILE_ARG = ".";

    private File configFile;
    private File targetFile;
    private Optional<Date> sinceDate;
    private Optional<Date> untilDate;

    /**
     * Parses user-supplied arguments.
     *
     * @throws ParseException If the given inputs or the csv file fail to parse or mandatory fields are missing.
     */
    public CliArguments(String[] args) throws ParseException {
        sinceDate = Optional.empty();
        untilDate = Optional.empty();
        targetFile = new File(DEFAULT_FILE_ARG);

        final HashMap<String, String> argumentMap = new HashMap<String, String>();

        for (int i = 0; i < args.length; i = i + 2) {

            if (i + 1 > args.length) {
                throw new ParseException(PARSE_EXCEPTION_MESSAGE_INVALID_INPUTS);
            }

            if (!args[i].startsWith(ARGUMENT_PREFIX_DASH)) {
                throw new ParseException(PARSE_EXCEPTION_MESSAGE_INVALID_INPUTS);
            }

            final String key = args[i].substring(1);
            argumentMap.put(key, args[i + 1]);
        }

        checkAllMandatoryArgumentsPresent(argumentMap);
        setUserInputValuesToArgument(argumentMap);
    }



    /**
     * Checks if all mandatory fields are present.
     *
     * @throws ParseException If there are missing mandatory fields.
     */
    private void checkAllMandatoryArgumentsPresent(final HashMap<String, String> argumentMap) throws ParseException {
        if (!argumentMap.containsKey(CONFIG_FILE_ARG)) {
            throw new ParseException(PARSE_EXCEPTION_MESSAGE_NO_CSV_FILE);
        }
    }

    /**
     * Set user-supplied inputs to instance variables
     *
     * @throws ParseException If the supplied dates fail to parse.
     */
    private void setUserInputValuesToArgument(final HashMap<String, String> argumentMap)
            throws ParseException {
        final DateParser dateParser = new DateParser();

        if (argumentMap.containsKey(CONFIG_FILE_ARG)) {
            final String configFilePath = argumentMap.get(CONFIG_FILE_ARG);
            configFile = new File(configFilePath);
        }

        if (argumentMap.containsKey(TARGET_FILE_ARG)) {
            final String targetFilePath = argumentMap.get(TARGET_FILE_ARG);
            targetFile = new File(targetFilePath);
        }

        if (argumentMap.containsKey(SINCE_DATE_ARG)) {
            final String sinceDateInString = argumentMap.get(SINCE_DATE_ARG);
            sinceDate = dateParser.parse(sinceDateInString);
        }

        if (argumentMap.containsKey(UNTIL_DATE_ARG)) {
            final String untilDateInString = argumentMap.get(UNTIL_DATE_ARG);
            untilDate = dateParser.parse(untilDateInString);
        }
    }

    public File getConfigFile() {
        return configFile;
    }

    public Optional<Date> getSinceDate() {
        return sinceDate;
    }

    public File getTargetFile() {
        return targetFile;
    }

    public Optional<Date> getUntilDate() {
        return untilDate;
    }

}
