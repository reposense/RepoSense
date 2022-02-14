package reposense.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
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
    private static final DateFormat CLI_ARGS_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    private static final String MESSAGE_SINCE_DATE_LATER_THAN_UNTIL_DATE =
            "\"Since Date\" cannot be later than \"Until Date\".";
    private static final String MESSAGE_SINCE_DATE_LATER_THAN_TODAY_DATE =
            "\"Since Date\" must not be later than today's date.";

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
     * Get the raw offset in milliseconds for the {@code zoneId} timezone compared to UTC.
     */
    public static int getZoneRawOffset(ZoneId zoneId) {
        Instant now = Instant.now();
        ZoneOffset zoneOffset = zoneId.getRules().getOffset(now);
        return zoneOffset.getTotalSeconds() * 1000;
    }

    /**
     * Get the {@code current} date that is in the system's timezone into the {@code zoneId} timezone.
     */
    public static Date getZonedDateFromSystemDate(Date current, ZoneId zoneId) {
        int zoneRawOffset = getZoneRawOffset(zoneId);
        int systemRawOffset = getZoneRawOffset(ZoneId.systemDefault());
        return new Date(current.getTime() + zoneRawOffset - systemRawOffset);
    }

    /**
     * Returns a {@code Date} that is set to midnight for the given {@code sinceDate}, adjusted to
     * the timezone given by {@code zoneId}.
     */
    public static Date getZonedSinceDate(Date sinceDate, ZoneId zoneId) {
        if (sinceDate.equals(SinceDateArgumentType.ARBITRARY_FIRST_COMMIT_DATE)) {
            return sinceDate;
        }

        int zoneRawOffset = getZoneRawOffset(zoneId);
        int systemRawOffset = getZoneRawOffset(ZoneId.systemDefault());

        Calendar cal = new Calendar
                .Builder()
                .setInstant(sinceDate.getTime())
                .build();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.MILLISECOND, systemRawOffset - zoneRawOffset);
        return cal.getTime();
    }

    /**
     * Returns a {@code Date} that is set to 23:59:59 for the given {@code untilDate}, adjusted to
     * the timezone given by {@code zoneId}.
     */
    public static Date getZonedUntilDate(Date untilDate, ZoneId zoneId) {
        int zoneRawOffset = getZoneRawOffset(zoneId);
        int systemRawOffset = getZoneRawOffset(ZoneId.systemDefault());

        Calendar cal = new Calendar
                .Builder()
                .setInstant(untilDate.getTime())
                .build();
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.MILLISECOND, systemRawOffset - zoneRawOffset);
        return cal.getTime();
    }

    /**
     * Returns a {@code Date} that is one month before {@code cliUntilDate} (if present) or one month before report
     * generation date otherwise. The time zone is adjusted to the given {@code zoneId}.
     */
    public static Date getDateMinusAMonth(Optional<Date> cliUntilDate, ZoneId zoneId) {
        Calendar cal = Calendar.getInstance();
        cliUntilDate.ifPresent(cal::setTime);
        cal.setTime(getZonedSinceDate(cal.getTime(), zoneId));
        cal.add(Calendar.MONTH, -1);
        return cal.getTime();
    }

    /**
     * Returns a {@code Date} that is {@code numOfDays} before {@code cliUntilDate} (if present) or one month before
     * report generation date otherwise. The time zone is adjusted to the given {@code zoneId}.
     */
    public static Date getDateMinusNDays(Optional<Date> cliUntilDate, ZoneId zoneId, int numOfDays) {
        Calendar cal = Calendar.getInstance();
        cliUntilDate.ifPresent(cal::setTime);
        cal.setTime(getZonedSinceDate(cal.getTime(), zoneId));
        cal.add(Calendar.DATE, -numOfDays + 1);
        return cal.getTime();
    }

    /**
     * Returns a {@code Date} that is {@code numOfDays} after {@code cliSinceDate} (if present). The time zone is
     * adjusted to the given {@code zoneId}.
     */
    public static Date getDatePlusNDays(Optional<Date> cliSinceDate, ZoneId zoneId, int numOfDays) {
        Calendar cal = Calendar.getInstance();
        cliSinceDate.ifPresent(cal::setTime);
        cal.setTime(getZonedUntilDate(cal.getTime(), zoneId));
        cal.add(Calendar.DATE, numOfDays - 1);
        return cal.getTime();
    }

    /**
     * Returns current date with time set to 23:59:59. The time zone is adjusted to the given {@code zoneId}.
     */
    public static Date getCurrentDate(ZoneId zoneId) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getZonedUntilDate(cal.getTime(), zoneId));
        return cal.getTime();
    }

    /**
     * Verifies that {@code sinceDate} is earlier than {@code untilDate}.
     *
     * @throws ParseException if {@code sinceDate} supplied is later than {@code untilDate}.
     */
    public static void verifyDatesRangeIsCorrect(Date sinceDate, Date untilDate)
            throws ParseException {
        if (sinceDate.getTime() > untilDate.getTime()) {
            throw new ParseException(MESSAGE_SINCE_DATE_LATER_THAN_UNTIL_DATE);
        }
    }

    /**
     * Verifies that {@code sinceDate} is no later than the date of report generation.
     *
     * @throws ParseException if {@code sinceDate} supplied is later than date of report generation.
     */
    public static void verifySinceDateIsValid(Date sinceDate) throws ParseException {
        Date dateToday = new Date();
        if (sinceDate.getTime() > dateToday.getTime()) {
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
     * Parses the given {@code date} string as a Date based on the {@code CLI_ARGS_DATE_FORMAT}.
     * <p>
     * Setting setLenient to false prevents unexpected results.
     * Without it, even with "dd/MM/yyyy HH:mm:ss" format, 11/31/2017 00:00:00 will be parsed to 11/7/2019 00:00:00.
     *
     * @throws java.text.ParseException if date cannot be parsed by the required format.
     */
    public static Date parseDate(String date) throws java.text.ParseException {
        CLI_ARGS_DATE_FORMAT.setLenient(false);
        return CLI_ARGS_DATE_FORMAT.parse(date);
    }
}
