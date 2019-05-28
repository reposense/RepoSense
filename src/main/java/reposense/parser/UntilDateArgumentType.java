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
    /**
     * Returns the date of report generation if user specifies * in {@code value}, or attempts to return
     * the desired date otherwise.
     */
    @Override
    public Optional<Date> convert(ArgumentParser parser, Argument arg, String value) throws ArgumentParserException {
        if ("*".equals(value)) {
            return Optional.empty();
        }
        return super.convert(parser, arg, value);
    }
}
