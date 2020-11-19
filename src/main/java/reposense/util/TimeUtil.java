package reposense.util;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;

/**
 * Contains time related functionalities.
 */
public class TimeUtil {
    private static Long startTime;

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
}
