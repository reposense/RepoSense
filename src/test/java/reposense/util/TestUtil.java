package reposense.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class TestUtil {

    private static final String MESSAGE_COMPARING_FILES = "Comparing files %s & %s\n";
    private static final String MESSAGE_LINE_CONTENT_DIFFERENT = "Content different at line number %d:\n"
            + "<< %s\n"
            + ">> %s\n";

    private static final String MESSAGE_LINES_LENGTH_DIFFERENT = "The files' lines count do not match.";

    /**
     * Compares the files' contents.
     * Prints out error message if the lines count are different,
     * else prints out the first line of content difference (if any).
     */
    public static boolean compareFileContents(File expected, File actual) throws IOException {
        return compareFileContents(expected, actual, 1);
    }

    /**
     * Compares the files' contents.
     * Prints out error message if the lines count are different,
     * else prints out maximum {@code traceCounts} lines of content difference (if any).
     */
    public static boolean compareFileContents(File expected, File actual, int traceCounts) throws IOException {
        int count = 0;

        System.out.println(String.format(MESSAGE_COMPARING_FILES, expected, actual));

        String[] expectedContent = new String(Files.readAllBytes(expected.toPath())).replace("\r", "").split("\n");
        String[] actualContent = new String(Files.readAllBytes(actual.toPath())).replace("\r", "").split("\n");

        for (int i = 0; i < Math.min(expectedContent.length, actualContent.length); i++) {
            if (!expectedContent[i].equals(actualContent[i])) {
                System.out.println(
                        String.format(MESSAGE_LINE_CONTENT_DIFFERENT, i + 1, expectedContent[i], actualContent[i]));
                if (++count >= traceCounts) {
                    break;
                }
            }
        }
        if (expectedContent.length != actualContent.length) {
            System.out.println(MESSAGE_LINES_LENGTH_DIFFERENT);
            return false;
        } else if (count >= traceCounts) {
            return false;
        }
        return true;
    }
}
