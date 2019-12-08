package reposense.util;

/**
 * Contains time related functionalities.
 */
public class TimeUtil {
    private static final String MESSAGE_ELAPSED_TIME =
            "Elapsed processing time: %d hour(s) %d minute(s) %.2f second(s)";
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
    public static String getTimeElapsedMessage() {
        long endTime = System.nanoTime();
        double elapsedTime = (double) (endTime - startTime) / 1_000_000_000.0;
        int elapsedHours = (int) elapsedTime / 3600;
        int elapsedMinutes = (int) (elapsedTime % 3600) / 60;
        double elapsedSeconds = elapsedTime % 60;
        return String.format(MESSAGE_ELAPSED_TIME, elapsedHours, elapsedMinutes, elapsedSeconds);
    }
}
