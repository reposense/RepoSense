package reposense.parsers;

import java.util.Date;
import java.util.HashMap;
import java.util.Optional;

import reposense.exception.ParseException;

/**
 * Verifies and parses user-supplied CLI arguments into an InputParameter Object.
 */
public class CliArgumentsParser extends Parser<InputParameter, String[]> {

    private static final String ARGUMENT_PREFIX_DASH = "-";

    private static final String CONFIG_FILE_ARG = "config";
    private static final String TARGET_FILE_ARG = "output";
    private static final String SINCE_DATE_ARG = "since";
    private static final String UNTIL_DATE_ARG = "until";

    private static final String PARSE_EXCEPTION_MESSAGE_INVALID_INPUTS = "Failed to parse inputs arguments";
    private static final String PARSE_EXCEPTION_MESSAGE_NO_CSV_FILE = "Failed due to missing CSV file";

    /**
     * Parses user-supplied arguments into an InputParameter object.
     *
     * @throws ParseException If the given inputs or the csv file fail to parse or mandatory fields are missing.
     */
    @Override
    public InputParameter parse(String[] args) throws ParseException {
        final HashMap<String, String> argumentMap = new HashMap<String, String>();
        final InputParameter parameter = new InputParameter();

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
        setUserInputValuesToArgument(argumentMap, parameter);

        return parameter;
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
     * Set user-supplied inputs to the InputParameter object
     *
     * @throws ParseException If the supplied dates fail to parse.
     */
    private void setUserInputValuesToArgument(final HashMap<String, String> argumentMap, final InputParameter parameter)
            throws ParseException {
        final DateParser dateParser = new DateParser();

        if (argumentMap.containsKey(CONFIG_FILE_ARG)) {
            final String configFile = argumentMap.get(CONFIG_FILE_ARG);
            parameter.setConfigFile(configFile);
        }

        if (argumentMap.containsKey(TARGET_FILE_ARG)) {
            final String targetFile = argumentMap.get(TARGET_FILE_ARG);
            parameter.setTargetFile(targetFile);
        }

        if (argumentMap.containsKey(SINCE_DATE_ARG)) {
            final String sinceDateInString = argumentMap.get(SINCE_DATE_ARG);
            final Optional<Date> sinceDate = dateParser.parse(sinceDateInString);
            parameter.setSinceDate(sinceDate.orElse(null));
        }

        if (argumentMap.containsKey(UNTIL_DATE_ARG)) {
            final String untilDateInString = argumentMap.get(UNTIL_DATE_ARG);
            final Optional<Date> untilDate = dateParser.parse(untilDateInString);
            parameter.setUntilDate(untilDate.orElse(null));
        }
    }
}
