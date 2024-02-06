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
        return super.convert(parser, arg, untilDate + " 23:59:59");
    }
}
