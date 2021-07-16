package reposense.util;

/**
 * Contains system related functionalities.
 */
public class SystemUtil {

    /**
     * Returns true if the test environment is on Windows OS.
     */
    public static boolean isWindows() {
        return (System.getProperty("os.name").toLowerCase().indexOf("win") >= 0);
    }

    /**
     * Returns true if the current environment is a test environment (defined by build.gradle)
     */
    public static boolean isTestEnvironment() {
        String environment = System.getenv("REPOSENSE_ENVIRONMENT");
        return ((environment != null) && (environment.equals("TEST")));
    }
}
