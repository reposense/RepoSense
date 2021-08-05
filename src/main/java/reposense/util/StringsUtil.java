package reposense.util;

import java.util.regex.Pattern;

/**
 * Contains strings related utilities.
 */
public class StringsUtil {

    private static final Pattern SPECIAL_SYMBOLS = Pattern.compile("[@;:&/\\\\!<>{}%#\"\\-='()\\[\\].+*?^$|]");

    /**
     * Filters the {@code text}, returning only the lines that matches the given {@code regex}.
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

    /**
     * Converts all special symbol characters inside {@code regexString} to the {@code replacementCharacter}.
     */
    public static String replaceSpecialSymbols(String regexString, String replacementCharacter) {
        return SPECIAL_SYMBOLS.matcher(regexString).replaceAll(replacementCharacter);
    }

    public static String addQuote(String original) {
        return "\"" + original + "\"";
    }

    /**
     * Removes quotes at the start and end of {@code original}, if exists.
     */
    public static String removeQuote(String original) {
        return original.startsWith("\"") && original.endsWith("\"")
                ? original.substring(1, original.length() - 1) : original;
    }
}
