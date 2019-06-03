package reposense.parser;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Optional;

import net.sourceforge.argparse4j.inf.Argument;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;

/**
 * Verifies and parses a string-formatted since date to a {@code Date} object.
 */
public class SinceDateArgumentType extends DateArgumentType {
    /*
     * When user specifies *, arbitrary since date will be returned.
     * Then, ReportGenerator will replace the arbitrary since date with the earliest commit date.
     */
    public static final Date ARBITRARY_SINCE_DATE = new GregorianCalendar(1, Calendar.JANUARY,
            1).getTime();

    /**
     * Returns an arbitrary year 2000 if user specifies * in {@code value}, or attempts to return
     * the desired date otherwise.
     */
    @Override
    public Optional<Date> convert(ArgumentParser parser, Argument arg, String value) throws ArgumentParserException {
        if ("*".equals(value)) {
            return Optional.of(ARBITRARY_SINCE_DATE);
        }
        return super.convert(parser, arg, value);
    }
}
