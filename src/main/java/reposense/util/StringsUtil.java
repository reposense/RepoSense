package reposense.util;

/**
 * Contains strings related utilities.
 */
public class StringsUtil {

    /**
     * Filter the {@code text}, returning only the lines that matches the given {@code regex}.
     */
    public static String filterText(String text, String regex) {
        String[] split = text.split("\n");
        StringBuilder sb = new StringBuilder();
        for (String line: split) {
            if (line.matches(regex)) {
                sb.append(line + "\n");
            }
        }

        return sb.toString();
    }
}
