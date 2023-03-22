package reposense.util;

import java.net.MalformedURLException;
import java.net.URL;

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
     * Returns true if the given String is a valid URL.
     */
    public static boolean isValidUrl(String possibleUrl) {
        try {
            new URL(possibleUrl);
        } catch (MalformedURLException mue) {
            return false;
        }
        return true;
    }

    /**
     * Returns true if the current environment is a test environment (defined by build.gradle).
     */
    public static boolean isTestEnvironment() {
        String environment = System.getenv("REPOSENSE_ENVIRONMENT");
        return ((environment != null) && (environment.equals("TEST")));
    }
}
