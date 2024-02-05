package reposense.util;

import java.util.Arrays;
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
     * Insertion, deletion, and substitution are all of cost 1.
     * This version improves the space complexity down to O(min(s, t)).
     * The dp will stop if the limit is reached, this means that if the final distance is 7 and the limit is set to 3,
     * the algorithm ends early once it reaches 3.
     */
    public static int getLevenshteinDistance(String s, String t, double limit) {
        if (s.length() < t.length()) {
            // Swap s and t to ensure s is always the longer string
            String temp = s;
            s = t;
            t = temp;
        }

        int[] dp = new int[t.length() + 1];
        for (int i = 0; i <= t.length(); i++) {
            dp[i] = i;
        }

        for (int i = 1; i <= s.length(); i++) {
            int prev = dp[0]; // Store the value of the previous row's column

            dp[0] = i;

            for (int j = 1; j <= t.length(); j++) {
                int temp = dp[j];

                if (s.charAt(i - 1) == t.charAt(j - 1)) {
                    dp[j] = prev;
                } else {
                    dp[j] = Math.min(prev, Math.min(dp[j - 1], dp[j])) + 1;
                }

                prev = temp;
            }

            // Since levenshtein distance is non-decreasing, if every value in the row is more than or equal to the
            // limit, then the final result will also be more than or equal to the limit.
            if (Arrays.stream(dp).noneMatch(v -> v < limit)) {
                // Return limit to ensure that the calculated originality score will always be more than the lowest
                // originality score.
                return (int) Math.ceil(limit);
            }
        }

        return dp[t.length()];
    }
}
