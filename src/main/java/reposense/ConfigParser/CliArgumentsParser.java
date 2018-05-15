package reposense.ConfigParser;

import reposense.exceptions.ParseException;

import java.util.Date;
import java.util.HashMap;

public class CliArgumentsParser extends Parser<Argument, String[]> {

    private static final String ARGUMENT_PREFIX_DASH = "-";

    private static final String CONFIG_FILE_ARG = "config";
    private static final String TARGET_FILE_ARG = "output";
    private static final String SINCE_DATE_ARG = "since";
    private static final String UNTIL_DATE_ARG = "until";

    private static final String PARSE_EXCEPTION_MESSAGE_INVALID_INPUTS = "Failed to parse inputs arguments";
    private static final String PARSE_EXCEPTION_MESSAGE_NO_CSV_FILE = "You need to specify a config CSV file!";

    private final HashMap<String, String> argumentMap;
    private final DateParser dateParser;
    private final Argument argument;

    public CliArgumentsParser() {
        argumentMap = new HashMap<String, String>();
        dateParser = new DateParser();
        argument = new Argument();
    }

    /**
     * Parses user-supplied arguments into an Argument object.
     *
     * @return Argument object, which contains the user supplied arguments.
     * @throws ParseException If the given inputs or the csv file fail to parse or mandatory fields are missing.
     */
    @Override
    public Argument parse(String[] args) throws ParseException{
        for (int i = 0; i < args.length; i = i + 2) {

            if (i + 1 > args.length) {
                throw new ParseException(PARSE_EXCEPTION_MESSAGE_INVALID_INPUTS);
            }

            if (!args[i].startsWith(ARGUMENT_PREFIX_DASH)) {
                throw new ParseException(PARSE_EXCEPTION_MESSAGE_INVALID_INPUTS);
            }

            String key = args[i].substring(1);
            argumentMap.put(key, args[i + 1]);
        }

        isAllMandatoryArgumentsPresent();

        setUserInputValuesToArgument();

        return argument;
    }

    /**
     * Checks if all mandatory fields are present.
     *
     * @throws ParseException If there are missing mandatory fields.
     */
    private void isAllMandatoryArgumentsPresent() throws ParseException {
        if(!argumentMap.containsKey(CONFIG_FILE_ARG)) {
            throw new ParseException(PARSE_EXCEPTION_MESSAGE_NO_CSV_FILE);
        }
    }

    /**
     * Set user-supplied inputs to the Argument object
     *
     * @throws ParseException If the supplied dates fail to parse.
     */
    private void setUserInputValuesToArgument() throws ParseException {
        if (argumentMap.containsKey(CONFIG_FILE_ARG)) {
            String configFile = argumentMap.get(CONFIG_FILE_ARG);
            argument.setConfigFile(configFile);
        }

        if (argumentMap.containsKey(TARGET_FILE_ARG)) {
            String targetFile = argumentMap.get(TARGET_FILE_ARG);
            argument.setTargetFile(targetFile);
        }

        if (argumentMap.containsKey(SINCE_DATE_ARG)) {
            String sinceDateInString = argumentMap.get(SINCE_DATE_ARG);
            Date sinceDate = dateParser.parse(sinceDateInString);
            argument.setUntilDate(sinceDate);
        }

        if (argumentMap.containsKey(UNTIL_DATE_ARG)) {
            String untilDateInString = argumentMap.get(UNTIL_DATE_ARG);
            Date untilDate = dateParser.parse(untilDateInString);
            argument.setUntilDate(untilDate);
        }
    }
}
