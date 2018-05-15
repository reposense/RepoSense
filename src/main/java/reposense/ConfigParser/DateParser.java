package reposense.ConfigParser;

import reposense.exceptions.ParseException;
import reposense.util.Constants;

import java.util.Date;

public class DateParser extends Parser<Date, String> {
    private static final String PARSE_EXCEPTION_MESSAGE_INVALID_DATE = "Invalid Date";

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
