package reposense.parser.types;

import java.time.LocalDateTime;
import java.util.Optional;

import net.sourceforge.argparse4j.inf.Argument;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.ArgumentType;
import reposense.util.TimeUtil;

/**
 * Verifies and parses a string-formatted date to a {@link LocalDateTime} object.
 */
public class DateArgumentType implements ArgumentType<Optional<LocalDateTime>> {
    private static final String PARSE_EXCEPTION_MESSAGE_INVALID_DATE_STRING_FORMAT = "Invalid Date: %s";
    protected static final String DATETIME_WITHOUT_HOURS_REGEX = "^\\d{1,2}/\\d{1,2}/\\d{4}$"; // Example: 31/12/2019
    protected static final String DATETIME_WITHOUT_SECONDS_REGEX = "^\\d{1,2}/\\d{1,2}/\\d{4}T\\d{2}:\\d{2}$"; // Example: 31/12/2019T14:30

    @Override
    public Optional<LocalDateTime> convert(ArgumentParser parser, Argument arg, String value)
            throws ArgumentParserException {
        try {
            return Optional.of(TimeUtil.parseDate(value));
        } catch (java.text.ParseException pe) {
            throw new ArgumentParserException(
                    String.format(PARSE_EXCEPTION_MESSAGE_INVALID_DATE_STRING_FORMAT, value), parser);
        }
    }
}
