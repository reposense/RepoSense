package reposense.parser;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import net.sourceforge.argparse4j.inf.Argument;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import reposense.util.TimeUtil;

/**
 * Verifies and parses a string-formatted since date to a {@link LocalDateTime} object.
 */
public class SinceDateArgumentType extends DateArgumentType {
    /*
     * When user specifies "d1", arbitrary first commit date will be returned.
     * Then, ReportGenerator will replace the arbitrary since date with the earliest commit date.
     */
    public static final LocalDateTime ARBITRARY_FIRST_COMMIT_DATE = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(Long.MIN_VALUE), ZoneId.of("Z"));
    public static final String FIRST_COMMIT_DATE_SHORTHAND = "d1";

    /**
     * Returns an arbitrary year {@link SinceDateArgumentType#ARBITRARY_FIRST_COMMIT_DATE} if user specifies
     * {@link SinceDateArgumentType#FIRST_COMMIT_DATE_SHORTHAND} in {@code value}, or attempts to return the
     * desired date otherwise.
     *
     * @throws ArgumentParserException if the given date cannot be parsed.
     */
    @Override
    public Optional<LocalDateTime> convert(ArgumentParser parser, Argument arg, String value)
            throws ArgumentParserException {
        if (FIRST_COMMIT_DATE_SHORTHAND.equals(value)) {
            return Optional.of(ARBITRARY_FIRST_COMMIT_DATE);
        }
        String sinceDate = TimeUtil.extractDate(value);
        return super.convert(parser, arg, sinceDate + " 00:00:00");
    }
}
