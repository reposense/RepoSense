package reposense.parser;

import java.util.Date;
import java.util.Optional;

import net.sourceforge.argparse4j.inf.Argument;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;

/**
 * Verifies and parses a string-formatted until date to a {@code Date} object.
 */

public class UntilDateArgumentType extends DateArgumentType {
    @Override
    public Optional<Date> convert(ArgumentParser parser, Argument arg, String value) throws ArgumentParserException {
        String untilDate = extractDate(value);
        return super.convert(parser, arg, untilDate + " 23:59:59");
    }
}
