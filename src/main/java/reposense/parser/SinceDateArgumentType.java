package reposense.parser;

import java.util.Date;
import java.util.Optional;

import net.sourceforge.argparse4j.inf.Argument;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;

/**
 * Verifies and parses a string-formatted since date to a {@code Date} object.
 */
public class SinceDateArgumentType extends DateArgumentType {
    /*
     * When user specifies *, arbitrary first commit date will be returned.
     * Then, ReportGenerator will replace the arbitrary since date with the earliest commit date.
     */
    public static final Date ARBITRARY_FIRST_COMMIT_DATE = new Date(Long.MIN_VALUE);

    /**
     * Returns an arbitrary year {@link SinceDateArgumentType#ARBITRARY_FIRST_COMMIT_DATE} if user specifies * in
     * {@code value}, or attempts to return the desired date otherwise.
     */
    @Override
    public Optional<Date> convert(ArgumentParser parser, Argument arg, String value) throws ArgumentParserException {
        return ("*".equals(value)) ? Optional.of(ARBITRARY_FIRST_COMMIT_DATE) : super.convert(parser, arg, value);
    }
}
