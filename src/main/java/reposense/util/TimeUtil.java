package reposense.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import reposense.parser.ParseException;
import reposense.parser.SinceDateArgumentType;

/**
 * Contains time related functionalities.
 */
public class TimeUtil {
    private static Long startTime;
    private static final String DATE_FORMAT_REGEX =
            "^((0[1-9]|[12][0-9]|3[01])\\/(0[1-9]|1[012])\\/(19|2[0-9])[0-9]{2})";

    // "uuuu" is used for year since "yyyy" does not work with ResolverStyle.STRICT
    private static final DateTimeFormatter CLI_ARGS_DATE_FORMAT = DateTimeFormatter.ofPattern("d/M/uuuu HH:mm:ss");
    private static final String MESSAGE_SINCE_DATE_LATER_THAN_UNTIL_DATE =
            "\"Since Date\" cannot be later than \"Until Date\".";
    private static final String MESSAGE_SINCE_DATE_LATER_THAN_TODAY_DATE =
            "\"Since Date\" must not be later than today's date.";

    /**
     * Sets the {@code startTime} to be the current time
     */
    public static void startTimer() {
        startTime = System.nanoTime();
    }

    /**
     * Returns the formatted elapsed time from {@code startTime} until current time
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
     * Returns a {@code LocalDateTime} that is set to midnight.
     */
    public static LocalDateTime getSinceDate(LocalDateTime sinceDate) {
        if (sinceDate.equals(SinceDateArgumentType.ARBITRARY_FIRST_COMMIT_DATE)) {
            return sinceDate;
        }

        return sinceDate.withHour(0).withMinute(0).withSecond(0);
    }

    /**
     * Returns a {@code LocalDateTime} that is set to 23:59:59.
     */
    public static LocalDateTime getUntilDate(LocalDateTime untilDate) {
        return untilDate.withHour(23).withMinute(59).withSecond(59);
    }

    /**
     * Returns a {@code LocalDateTime} that is one month before {@code cliUntilDate} (if present) or one month
     * before report generation date otherwise.
     */
    public static LocalDateTime getDateMinusAMonth(LocalDateTime cliUntilDate) {
        return getSinceDate(cliUntilDate.minusMonths(1));
    }

    /**
     * Returns a {@code LocalDateTime} that is {@code numOfDays} before {@code cliUntilDate} (if present) or one month
     * before report generation date otherwise.
     */
    public static LocalDateTime getDateMinusNDays(LocalDateTime cliUntilDate, int numOfDays) {
        return getSinceDate(cliUntilDate.minusDays(numOfDays));
    }

    /**
     * Returns a {@code LocalDateTime} that is {@code numOfDays} after {@code cliSinceDate} (if present).
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
     * Verifies that {@code sinceDate} is no later than the date of report generation.
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
     * Extracts the first substring that matches the {@code DATE_FORMAT_REGEX}.
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
     * Parses the given String as a Date based on the {@code CLI_ARGS_DATE_FORMAT}.
     * Uses {@code ResolverStyle.STRICT} to avoid unexpected dates like 31/02/2020.
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
