package reposense.util;

import java.net.MalformedURLException;
import java.net.URL;

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

    /**
     * Returns true if the given String is a valid URL.
     */
    public static boolean isValidUrl(String possibleUrl) {
        boolean isValidUrl;
        try {
            new URL(possibleUrl);
            isValidUrl = true;
        } catch (MalformedURLException mue) {
            isValidUrl = false;
        }
        return isValidUrl;
    }
}
