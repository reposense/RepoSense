package reposense.parser;

import java.util.Date;
import java.util.Optional;

import reposense.exception.ParseException;
import reposense.util.Constants;

/**
 * Verifies and parses a string-formatted date to a Date object.
 */
public class DateParser implements Parser<Optional<Date>, String> {
    private static final String PARSE_EXCEPTION_MESSAGE_INVALID_DATE_STRING_FORMAT = "Invalid Date: %s";

    /**
     * Parses the given string to a Date object
     *
     * @throws ParseException If the given string fails to parse to a Date object.
     */
    @Override
    public Optional<Date> parse(String input) throws ParseException {
        Optional<Date> date;

        try {
            date = Optional.of(Constants.CLI_ARGS_DATE_FORMAT.parse(input));
        } catch (Exception e) {
            throw new ParseException(String.format(PARSE_EXCEPTION_MESSAGE_INVALID_DATE_STRING_FORMAT, input));
        }

        return date;
    }
}
