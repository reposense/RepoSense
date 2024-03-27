package reposense.util;

import java.util.regex.Pattern;

/**
 * Contains strings related utilities.
 */
public class StringsUtil {
    public static final Pattern NEWLINE = Pattern.compile("\n");
    public static final Pattern TAB = Pattern.compile("\t");
    public static final Pattern NUMERIC = Pattern.compile("^\\d+$");
    private static final Pattern SPECIAL_SYMBOLS = Pattern.compile("[@;:&/\\\\!<>{}%#\"\\-='()\\[\\].+*?^$|]");

    /**
     * Filters the {@code text}, returning only the lines that matches the given {@code regex}.
     */
    public static String filterText(String text, String regex) {
        StringBuilder sb = new StringBuilder();
        Pattern regexPattern = Pattern.compile(regex);

        for (String line: NEWLINE.split(text)) {
            if (regexPattern.matcher(line).matches()) {
                sb.append(line).append("\n");
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

    /**
     * Adds quotes to the string.
     */
    public static String addQuotes(String original) {
        return "\"" + original + "\"";
    }

    /**
     * Adds the appropriate quotation marks for a file path depending on the OS.
     */
    public static String addQuotesForFilePath(String filePath) {
        if (SystemUtil.isWindows()) {
            return "\"" + filePath + "\"";
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < filePath.length(); i++) {
                char c = filePath.charAt(i);
                if (c == '\'') {
                    sb.append("'\"'\"'");
                } else {
                    sb.append(c);
                }
            }
            return '\'' + sb.toString() + '\'';
        }
    }

    /**
     * Removes quotes at the start and end of {@code original}, if exists.
     */
    public static String removeQuote(String original) {
        return (original.startsWith("\"") && original.endsWith("\""))
                ? original.substring(1, original.length() - 1)
                : original;
    }

    /**
     * Removes trailing backslashes from a {@code string}, if it exists.
     */
    public static String removeTrailingBackslash(String string) {
        if (string.isEmpty()) {
            return string;
        }
        int lastCharIndex = string.length() - 1;
        String editedString = string;
        boolean isLastCharBackslash = string.charAt(lastCharIndex) == '\\';
        while (isLastCharBackslash) {
            editedString = editedString.substring(0, lastCharIndex--);
            boolean isStringEmpty = editedString.length() == 0;
            isLastCharBackslash = !isStringEmpty && editedString.charAt(lastCharIndex) == '\\';
        }
        return editedString;
    }

    /**
     * Returns true iff {@code string} is purely numeric.
     */
    public static boolean isNumeric(String string) {
        return NUMERIC.matcher(string).matches();
    }
}
