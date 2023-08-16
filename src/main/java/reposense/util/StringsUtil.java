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
        for (String line : split) {
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
        return Pattern.compile("^\\d+$").matcher(string).matches();
    }

    /**
     * Calculates the Levenshtein Distance between two strings using Dynamic Programming.
     */
    public static int getLevenshteinDistance(String s, String t) {
        // dp[i][j] stores the distance between s.substring(0, i) and t.substring(0, j) -> distance(s[:i], t[:j])
        int[][] dp = new int[s.length() + 1][t.length() + 1];

        // Distance between a string and an empty string is the length of the string
        for (int i = 0; i <= s.length(); i++) {
            dp[i][0] = i;
        }

        for (int i = 0; i <= t.length(); i++) {
            dp[0][i] = i;
        }

        for (int i = 1; i <= s.length(); i++) {
            for (int j = 1; j <= t.length(); j++) {
                // If s[i-1] and t[j-1] are equal, distance(s[:i], t[:j]) equals to distance(s[:i-1], t[:j-1])
                if (s.charAt(i - 1) == t.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    // distance(s[:i], t[:j]) is the minimum of:
                    // 1) distance(s[:i-1], t[:j]) + 1 -> add s[i]
                    // 2) distance(s[:i], t[:j-1]) + 1 -> add t[j]
                    // 3) distance(s[:i-1], t[:j-1]) + 1 -> substitute s[i] with t[j]
                    dp[i][j] = Math.min(dp[i - 1][j], Math.min(dp[i][j - 1], dp[i - 1][j - 1])) + 1;
                }
            }
        }

        return dp[s.length()][t.length()];
    }
}
