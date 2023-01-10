package reposense.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import reposense.parser.ParseException;
import reposense.parser.SinceDateArgumentType;
import reposense.system.LogsManager;

/**
 * Contains time related functionalities.
 */
public class TimeUtil {
    private static Long startTime;
    private static final String DATE_FORMAT_REGEX =
            "^((0?[1-9]|[12][0-9]|3[01])\\/(0?[1-9]|1[012])\\/(19|2[0-9])[0-9]{2})";

    // "uuuu" is used for year since "yyyy" does not work with ResolverStyle.STRICT
    private static final DateTimeFormatter CLI_ARGS_DATE_FORMAT = DateTimeFormatter.ofPattern("d/M/uuuu HH:mm:ss");
    private static final String MESSAGE_SINCE_DATE_LATER_THAN_UNTIL_DATE =
            "\"Since Date\" cannot be later than \"Until Date\".";
    private static final String MESSAGE_SINCE_DATE_LATER_THAN_TODAY_DATE =
            "\"Since Date\" must not be later than today's date.";

    private static final String EARLIEST_VALID_DATE = "1970-01-01T00:00:00";
    private static final String LATEST_VALID_DATE = "2099-12-31T23:59:59";
    private static final String MESSAGE_SINCE_DATE_EARLIER_THAN_EARLIEST_VALID_DATE =
            "Date of %s must not be earlier than "
            + String.format("%s, resetting it to earliest valid date", EARLIEST_VALID_DATE);
    private static final String MESSAGE_UNTIL_DATE_LATER_THAN_LATEST_VALID_DATE =
            "Date of %s must not be later than "
            + String.format("%s, resetting it to latest valid date", LATEST_VALID_DATE);

    private static final Logger logger = LogsManager.getLogger(TimeUtil.class);

    /**
     * Sets the {@code startTime} to be the current time.
     */
    public static void startTimer() {
        startTime = System.nanoTime();
    }

    /**
     * Returns the formatted elapsed time from {@code startTime} until current time.
     */
    public static String getElapsedTime() {
        long endTime = System.nanoTime();
        double elapsedTime = (double) (endTime - startTime) / 1_000_000_000.0;
        int elapsedHours = (int) elapsedTime / 3600;
        int elapsedMinutes = (int) (elapsedTime % 3600) / 60;
        double elapsedSeconds = elapsedTime % 60;
        String formattedElapsedTime = "";

        if (elapsedHours > 0) {
            formattedElapsedTime += String.format(" %d hour(s)", elapsedHours);
        }

        if (elapsedMinutes > 0) {
            formattedElapsedTime += String.format(" %d minute(s)", elapsedMinutes);
        }

        formattedElapsedTime += String.format(" %.2f second(s)", elapsedSeconds);
        return formattedElapsedTime;
    }

    /**
     * Returns the formatted elapsed time from {@code startTime} until current time,
     * with an additional explanatory string.
     */
    public static String getElapsedTimeMessage() {
        return "Elapsed processing time:" + getElapsedTime();
    }

    /**
     * Returns a valid {@link LocalDateTime} that is set to midnight for the given {@code sinceDate}.
     */
    public static LocalDateTime getSinceDate(LocalDateTime sinceDate) {
        return getValidDate(sinceDate).withHour(0).withMinute(0).withSecond(0);
    }

    /**
     * Returns a valid {@link LocalDateTime} that is set to 23:59:59 for the given {@code untilDate}.
     */
    public static LocalDateTime getUntilDate(LocalDateTime untilDate) {
        return getValidDate(untilDate).withHour(23).withMinute(59).withSecond(59);
    }

    /**
     * Returns a valid {@link LocalDateTime} that is within {@value EARLIEST_VALID_DATE} and {@value LATEST_VALID_DATE}.
     * Resets {@code date} passed the closest valid date if it exceeds the date range.
     */
    public static LocalDateTime getValidDate(LocalDateTime date) {
        if (date.isBefore(LocalDateTime.parse(EARLIEST_VALID_DATE))) {
            logger.warning(String.format(MESSAGE_SINCE_DATE_EARLIER_THAN_EARLIEST_VALID_DATE, date));
            return LocalDateTime.parse(EARLIEST_VALID_DATE);
        }

        if (date.isAfter(LocalDateTime.parse(LATEST_VALID_DATE))) {
            logger.warning(String.format(MESSAGE_UNTIL_DATE_LATER_THAN_LATEST_VALID_DATE, date));
            return LocalDateTime.parse(LATEST_VALID_DATE);
        }
        return date;
    }

    /**
     * Returns a {@link LocalDateTime} that is one month before {@code cliUntilDate} (if present) or one month
     * before report generation date otherwise.
     */
    public static LocalDateTime getDateMinusAMonth(LocalDateTime cliUntilDate) {
        return getSinceDate(cliUntilDate.minusMonths(1));
    }

    /**
     * Returns a {@link LocalDateTime} that is {@code numOfDays} before {@code cliUntilDate} (if present) or one month
     * before report generation date otherwise.
     */
    public static LocalDateTime getDateMinusNDays(LocalDateTime cliUntilDate, int numOfDays) {
        return getSinceDate(cliUntilDate.minusDays(numOfDays));
    }

    /**
     * Returns a {@link LocalDateTime} that is {@code numOfDays} after {@code cliSinceDate} (if present).
     */
    public static LocalDateTime getDatePlusNDays(LocalDateTime cliSinceDate, int numOfDays) {
        return getUntilDate(cliSinceDate.plusDays(numOfDays));
    }

    /**
     * Returns current date with time set to 23:59:59. The time zone is adjusted to the given {@code zoneId}.
     */
    public static LocalDateTime getCurrentDate(ZoneId zoneId) {
        return LocalDateTime.now(zoneId).withHour(23).withMinute(59).withSecond(59).withNano(0);
    }

    /**
     * Returns the {@link LocalDateTime} of {@code ARBITRARY_FIRST_COMMIT_DATE} in the UTC time zone.
     */
    public static LocalDateTime getArbitraryFirstCommitDateLocal() {
        return SinceDateArgumentType.getArbitraryFirstCommitDateLocal();
    }

    /**
     * Returns the {@link LocalDateTime} of {@code ARBITRARY_FIRST_COMMIT_DATE} adjusted for the time zone based on
     * {@code toZoneId}.
     */
    public static LocalDateTime getArbitraryFirstCommitDateConverted(ZoneId toZoneId) {
        return SinceDateArgumentType.getArbitraryFirstCommitDateConverted(toZoneId);
    }

    /**
     * Checks whether the given {@code dateTime} is the {@code ARBITRARY_FIRST_COMMIT_DATE} in UTC time.
     */
    public static boolean isEqualToArbitraryFirstDateUtc(LocalDateTime dateTime) {
        return dateTime.equals(getArbitraryFirstCommitDateLocal());
    }

    /**
     * Checks whether the given {@code dateTime} is the {@code ARBITRARY_FIRST_COMMIT_DATE} in the time zone given by
     * {@code zoneId}.
     */
    public static boolean isEqualToArbitraryFirstDateConverted(LocalDateTime dateTime, ZoneId zoneId) {
        return dateTime.equals(getArbitraryFirstCommitDateConverted(zoneId));
    }

    /**
     * Verifies that {@code sinceDate} is earlier than {@code untilDate}.
     *
     * @throws ParseException if {@code sinceDate} supplied is later than {@code untilDate}.
     */
    public static void verifyDatesRangeIsCorrect(LocalDateTime sinceDate, LocalDateTime untilDate)
            throws ParseException {
        if (sinceDate.compareTo(untilDate) > 0) {
            throw new ParseException(MESSAGE_SINCE_DATE_LATER_THAN_UNTIL_DATE);
        }
    }

    /**
     * Verifies that {@code sinceDate} is no later than the date of report generation, given by {@code currentDate}.
     *
     * @throws ParseException if {@code sinceDate} supplied is later than date of report generation.
     */
    public static void verifySinceDateIsValid(LocalDateTime sinceDate, LocalDateTime currentDate)
            throws ParseException {
        if (sinceDate.compareTo(currentDate) > 0) {
            throw new ParseException(MESSAGE_SINCE_DATE_LATER_THAN_TODAY_DATE);
        }
    }

    /**
     * Extracts the first substring of {@code date} string that matches the {@code DATE_FORMAT_REGEX}.
     */
    public static String extractDate(String date) {
        Matcher matcher = Pattern.compile(DATE_FORMAT_REGEX).matcher(date);
        String extractedDate = date;
        if (matcher.find()) {
            extractedDate = matcher.group(1);
        }
        return extractedDate;
    }

    /**
     * Parses the given {@code date} string as a {@link LocalDateTime} based on the {@code CLI_ARGS_DATE_FORMAT}.
     * Uses {@link ResolverStyle#STRICT} to avoid unexpected dates like 31/02/2020.
     *
     * @throws java.text.ParseException if date cannot be parsed by the required format.
     */
    public static LocalDateTime parseDate(String date) throws java.text.ParseException {
        try {
            return LocalDateTime.parse(date, CLI_ARGS_DATE_FORMAT.withResolverStyle(ResolverStyle.STRICT));
        } catch (DateTimeParseException e) {
            throw new java.text.ParseException(String.format(
                    "Exception message: %s\n", e.getMessage()), e.getErrorIndex());
        }
    }
}
