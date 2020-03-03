package reposense.util;

/**
 * Contains time related functionalities.
 */
public class TimeUtil {
    private static String elapsedTimeMessage = "Elapsed processing time:";
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
    public static String getElapsedTimeMessage() {
        long endTime = System.nanoTime();
        double elapsedTime = (double) (endTime - startTime) / 1_000_000_000.0;
        int elapsedHours = (int) elapsedTime / 3600;
        int elapsedMinutes = (int) (elapsedTime % 3600) / 60;
        double elapsedSeconds = elapsedTime % 60;

        if (elapsedHours > 0) {
            elapsedTimeMessage += String.format(" %d hour(s)", elapsedHours);
        }

        if (elapsedMinutes > 0) {
            elapsedTimeMessage += String.format(" %d minute(s)", elapsedMinutes);
        }

        elapsedTimeMessage += String.format(" %.2f seconds(s)", elapsedSeconds);

        return elapsedTimeMessage;
    }
}
