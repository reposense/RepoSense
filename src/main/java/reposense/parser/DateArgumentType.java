package reposense.parser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.argparse4j.inf.Argument;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.ArgumentType;

/**
 * Verifies and parses a string-formatted date to a {@code Date} object.
 */
public class DateArgumentType implements ArgumentType<Optional<Date>> {
    protected static final String DATE_FORMAT_REGEX =
            "^((0[1-9]|[12][0-9]|3[01])\\/(0[1-9]|1[012])\\/(19|2[0-9])[0-9]{2})";
    private static final String PARSE_EXCEPTION_MESSAGE_INVALID_DATE_STRING_FORMAT = "Invalid Date: %s";
    private static final DateFormat CLI_ARGS_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    static {
        /*
         * Setting setLenient to false prevents unexpected results.
         * Without it, even with "dd/MM/yyyy HH:mm:ss" format, 11/31/2017 00:00:00 will be parsed to 11/7/2019 00:00:00.
         * */
        CLI_ARGS_DATE_FORMAT.setLenient(false);
    }

    /**
     * Extracts the first substring that matches the {@code DATE_FORMAT_REGEX}.
     */
    protected String extractDate(String date) {
        Matcher matcher = Pattern.compile(DATE_FORMAT_REGEX).matcher(date);
        String extractedDate = date;
        if (matcher.find()) {
            extractedDate = matcher.group(1);
        }
        return extractedDate;
    }

    @Override
    public Optional<Date> convert(ArgumentParser parser, Argument arg, String value) throws ArgumentParserException {
        try {
            return Optional.of(CLI_ARGS_DATE_FORMAT.parse(value));
        } catch (java.text.ParseException pe) {
            throw new ArgumentParserException(
                    String.format(PARSE_EXCEPTION_MESSAGE_INVALID_DATE_STRING_FORMAT, value), parser);
        }
    }
}
