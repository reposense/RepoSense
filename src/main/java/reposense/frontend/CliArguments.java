package reposense.frontend;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Optional;

import reposense.exception.ParseException;
import reposense.parser.DateParser;

/**
 * Represents command line arguments user supplied when running the program.
 */
public class CliArguments {

    private static final String ARGUMENT_PREFIX_DASH = "-";

    private static final String CONFIG_FILE_ARG = "config";
    private static final String TARGET_FILE_ARG = "output";
    private static final String SINCE_DATE_ARG = "since";
    private static final String UNTIL_DATE_ARG = "until";

    private static final String MESSAGE_INVALID_INPUTS = "Failed to parse inputs arguments";
    private static final String MESSAGE_NO_CSV_FILE = "Failed due to missing CSV file";

    private static final String DEFAULT_FILE_ARG = ".";

    private File configFile;
    private File targetFile;
    private Optional<Date> sinceDate;
    private Optional<Date> untilDate;

    /**
     * Parses user-supplied arguments.
     *
     * @throws IllegalArgumentException if the given args inputs are either
     *  - malformed
     *  - fail to parse
     *  - missing mandatory fields
     */
    public CliArguments(String[] args) throws IllegalArgumentException {
        sinceDate = Optional.empty();
        untilDate = Optional.empty();
        targetFile = new File(DEFAULT_FILE_ARG);

        final HashMap<String, String> argumentMap = generateArgumentMap(args);
        checkAllMandatoryArgumentsPresent(argumentMap);
        setUserInputValuesToArgument(argumentMap);
    }

    /**
     * Alternative constructor for testing
     *
     * @param configFile
     * @param sinceDate
     * @param untilDate
     */
    public CliArguments(File configFile, Date sinceDate, Date untilDate) {
        this.configFile = configFile;
        this.sinceDate = Optional.of(sinceDate);
        this.untilDate = Optional.of(untilDate);
    }

    /**
     * Verifies if {@code args} begins with a dash character and follows by an input and generates an ArgumentMap
     *
     * @throws IllegalArgumentException If the given args are malformed,
     * which means it does not have a dash character or does not follow by an input.
     */
    private HashMap<String, String> generateArgumentMap(String[] args) {
        final HashMap<String, String> argumentMap = new HashMap<String, String>();

        for (int i = 0; i < args.length; i = i + 2) {

            if (i + 1 > args.length) {
                throw new IllegalArgumentException(MESSAGE_INVALID_INPUTS);
            }

            if (!args[i].startsWith(ARGUMENT_PREFIX_DASH)) {
                throw new IllegalArgumentException(MESSAGE_INVALID_INPUTS);
            }

            final String key = args[i].substring(1);
            argumentMap.put(key, args[i + 1]);
        }

        return argumentMap;
    }


    /**
     * Checks if all mandatory fields are present.
     *
     * @throws IllegalArgumentException If there are missing mandatory fields.
     */
    private void checkAllMandatoryArgumentsPresent(final HashMap<String, String> argumentMap)
            throws IllegalArgumentException {
        if (!argumentMap.containsKey(CONFIG_FILE_ARG)) {
            throw new IllegalArgumentException(MESSAGE_NO_CSV_FILE);
        }
    }

    /**
     * Set user-supplied inputs to instance variables
     *
     * @throws IllegalArgumentException If the supplied dates fail to parse.
     */
    private void setUserInputValuesToArgument(final HashMap<String, String> argumentMap)
            throws IllegalArgumentException {
        final DateParser dateParser = new DateParser();

        if (argumentMap.containsKey(CONFIG_FILE_ARG)) {
            final String configFilePath = argumentMap.get(CONFIG_FILE_ARG);
            configFile = new File(configFilePath);
        }

        if (argumentMap.containsKey(TARGET_FILE_ARG)) {
            final String targetFilePath = argumentMap.get(TARGET_FILE_ARG);
            targetFile = new File(targetFilePath);
        }

        try {
            if (argumentMap.containsKey(SINCE_DATE_ARG)) {
                final String sinceDateInString = argumentMap.get(SINCE_DATE_ARG);
                sinceDate = dateParser.parse(sinceDateInString);
            }

            if (argumentMap.containsKey(UNTIL_DATE_ARG)) {
                final String untilDateInString = argumentMap.get(UNTIL_DATE_ARG);
                untilDate = dateParser.parse(untilDateInString);
            }
        } catch (ParseException pe) {
            throw new IllegalArgumentException(pe.getMessage());
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
