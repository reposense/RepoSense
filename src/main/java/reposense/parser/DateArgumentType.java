package reposense.parser;

import java.util.Date;
import java.util.Optional;

import net.sourceforge.argparse4j.inf.Argument;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.ArgumentType;
import reposense.util.TimeUtil;

/**
 * Verifies and parses a string-formatted date to a {@code Date} object.
 */
public class DateArgumentType implements ArgumentType<Optional<Date>> {
    private static final String PARSE_EXCEPTION_MESSAGE_INVALID_DATE_STRING_FORMAT = "Invalid Date: %s";

    @Override
    public Optional<Date> convert(ArgumentParser parser, Argument arg, String value) throws ArgumentParserException {
        try {
            return Optional.of(TimeUtil.parseDate(value));
        } catch (ParseException pe) {
            throw new ArgumentParserException(
                    String.format(PARSE_EXCEPTION_MESSAGE_INVALID_DATE_STRING_FORMAT, value), parser);
        }
    }
}
