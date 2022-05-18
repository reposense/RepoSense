package reposense.parser;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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
     * This date is equivalent to 1970-01-01 00:00:00 in UTC time.
     * Then, ReportGenerator will replace the arbitrary since date with the earliest commit date.
     */
    public static final String FIRST_COMMIT_DATE_SHORTHAND = "d1";
    private static final ZonedDateTime ARBITRARY_FIRST_COMMIT_DATE_UTC = ZonedDateTime.ofInstant(
            Instant.ofEpochMilli(0), ZoneId.of("Z"));
    private static final LocalDateTime ARBITRARY_FIRST_COMMIT_DATE_LOCAL = ARBITRARY_FIRST_COMMIT_DATE_UTC
            .toLocalDateTime();

    /**
     * Returns an arbitrary year {@link SinceDateArgumentType#ARBITRARY_FIRST_COMMIT_DATE_LOCAL} if user specifies
     * {@link SinceDateArgumentType#FIRST_COMMIT_DATE_SHORTHAND} in {@code value}, or attempts to return the
     * desired date otherwise.
     *
     * @throws ArgumentParserException if the given date cannot be parsed.
     */
    @Override
    public Optional<LocalDateTime> convert(ArgumentParser parser, Argument arg, String value)
            throws ArgumentParserException {
        if (FIRST_COMMIT_DATE_SHORTHAND.equals(value)) {
            return Optional.of(ARBITRARY_FIRST_COMMIT_DATE_LOCAL);
        }
        String sinceDate = TimeUtil.extractDate(value);
        return super.convert(parser, arg, sinceDate + " 00:00:00");
    }

    /**
     * Returns the {@link SinceDateArgumentType#ARBITRARY_FIRST_COMMIT_DATE_LOCAL}, which is the
     * {@link LocalDateTime} of {@link SinceDateArgumentType#ARBITRARY_FIRST_COMMIT_DATE_UTC}.
     */
    public static LocalDateTime getArbitraryFirstCommitDateLocal() {
        return ARBITRARY_FIRST_COMMIT_DATE_LOCAL;
    }

    /**
     * Returns the {@link SinceDateArgumentType#ARBITRARY_FIRST_COMMIT_DATE_UTC} adjusted for the time zone based on
     * {@code toZoneId} and converted to a {@link LocalDateTime} object.
     */
    public static LocalDateTime getArbitraryFirstCommitDateConverted(ZoneId toZoneId) {
        return ARBITRARY_FIRST_COMMIT_DATE_UTC.withZoneSameInstant(toZoneId).toLocalDateTime();
    }
}
