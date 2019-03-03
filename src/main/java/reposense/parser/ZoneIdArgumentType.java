package reposense.parser;

import java.time.DateTimeException;
import java.time.ZoneId;

import net.sourceforge.argparse4j.inf.Argument;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.ArgumentType;

/**
 * Verifies and parses a string-formatted zone id to a {@code ZoneId} object.
 */
public class ZoneIdArgumentType implements ArgumentType<ZoneId> {
    private static final String MESSAGE_TIMEZONE_INVALID =
            "The timezone provided is invalid, please use the format of UTC±[hh[mm]]";

    @Override
    public ZoneId convert(ArgumentParser parser, Argument arg, String value) throws ArgumentParserException {
        try {
            if (!value.contains("UTC")) {
                throw new DateTimeException(MESSAGE_TIMEZONE_INVALID);
            }
            return ZoneId.of(value);
        } catch (DateTimeException dte) {
            throw new ArgumentParserException(MESSAGE_TIMEZONE_INVALID, parser);
        }
    }
}
