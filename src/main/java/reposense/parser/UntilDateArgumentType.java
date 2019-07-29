package reposense.parser;

import java.util.Date;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.argparse4j.inf.Argument;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;

/**
 * Verifies and parses a string-formatted until date to a {@code Date} object.
 */

public class UntilDateArgumentType extends DateArgumentType {
    @Override
    public Optional<Date> convert(ArgumentParser parser, Argument arg, String value) throws ArgumentParserException {
        Matcher matcher = Pattern.compile(DATE_FORMAT_REGEX).matcher(value);
        String untilDate = matcher.find() ? matcher.group(1) : value;
        return super.convert(parser, arg, untilDate + " 23:59:59");
    }
}
