package reposense.ConfigParser;

import reposense.exceptions.ParseException;
import reposense.util.Constants;

import java.util.Date;

public class DateParser extends Parser<Date, String> {
    private static final String PARSE_EXCEPTION_MESSAGE_INVALID_DATE = "Invalid Date";

    /**
     * Parses the given string to a Date object
     *
     * @param input the string to be parsed
     * @return Date object
     * @throws ParseException If the given string fails to parse to a Date object.
     */
    @Override
    public Date parse(String input) throws ParseException {
        Date date = null;

        try {
            date =  Constants.CLI_ARGS_DATE_FORMAT.parse(input);
        } catch (Exception e) {
            throw new ParseException(PARSE_EXCEPTION_MESSAGE_INVALID_DATE + " " + input);
        }

        return date;
    }
}
