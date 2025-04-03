package reposense.parser.types;

import java.time.LocalDateTime;
import java.util.Optional;

import net.sourceforge.argparse4j.inf.Argument;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import reposense.util.TimeUtil;

/**
 * Verifies and parses a string-formatted until date to a {@link LocalDateTime} object.
 */

public class UntilDateArgumentType extends DateArgumentType {
    @Override
    public Optional<LocalDateTime> convert(ArgumentParser parser, Argument arg, String value)
            throws ArgumentParserException {
        String untilDate = TimeUtil.extractDate(value);

        if (untilDate.matches(DATETIME_WITHOUT_HOURS_REGEX)) {
            untilDate += "T23:59:59";
        } else if (untilDate.matches(DATETIME_WITHOUT_SECONDS_REGEX)) {
            untilDate += ":59";
        }

        return super.convert(parser, arg, untilDate);
    }
}
