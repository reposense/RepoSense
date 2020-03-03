package reposense.util;

/**
 * Contains sytem related functionalities.
 */
public class SystemUtil {

    /**
     * Returns true if the test environment is on Windows OS.
     */
    public static boolean isWindows() {
        return (System.getProperty("os.name").toLowerCase().indexOf("win") >= 0);
    }
}
