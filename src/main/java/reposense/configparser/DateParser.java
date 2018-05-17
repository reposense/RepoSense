package reposense.configparser;

import java.util.Date;

import reposense.exceptions.ParseException;
import reposense.util.Constants;

/**
 * Verifies and parses a string-formatted date to a Date object.
 */
public class DateParser extends Parser<Date, String> {
    private static final String PARSE_EXCEPTION_MESSAGE_INVALID_DATE = "Invalid Date";
    private static final String PARSE_EXCEPTION_MESSAGE_INVALID_DATE_STRING_FORMAT =
            PARSE_EXCEPTION_MESSAGE_INVALID_DATE + " %s";

    /**
     * Parses the given string to a Date object
     *
     * @throws ParseException If the given string fails to parse to a Date object.
     */
    @Override
    public Date parse(String input) throws ParseException {
        Date date = null;

        try {
            date = Constants.CLI_ARGS_DATE_FORMAT.parse(input);
        } catch (Exception e) {
            throw new ParseException(String.format(PARSE_EXCEPTION_MESSAGE_INVALID_DATE_STRING_FORMAT, input));
        }

        return date;
    }
}
