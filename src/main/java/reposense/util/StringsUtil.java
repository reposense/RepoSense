package reposense.util;

import java.util.regex.Pattern;

/**
 * Contains strings related utilities.
 */
public class StringsUtil {
    public static final Pattern NEWLINE = Pattern.compile("\n");
    public static final Pattern TAB = Pattern.compile("\t");
    public static final Pattern SPACE = Pattern.compile(" ");

    public static final Pattern NUMERIC = Pattern.compile("^\\d+$");
    private static final Pattern SPECIAL_SYMBOLS = Pattern.compile("[@;:&/\\\\!<>{}%#\"\\-='()\\[\\].+*?^$|]");

    /**
     * Filters the {@code text}, returning only the lines that matches the given {@code regex}.
     */
    public static String filterText(String text, String regex) {
        StringBuilder sb = new StringBuilder();
        Pattern regexPattern = Pattern.compile(regex);

        for (String line : NEWLINE.split(text)) {
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

    /**
     * Calculates the Levenshtein Distance between two strings using Dynamic Programming.
     * Insertion, deletion, and substitution are all of cost 1.
     * This version improves the space complexity down to O(min(s, t))
     * <p></p>
     * The dp will stop if the {@code limit} is reached, this means that if the final distance is 7 and the limit is set
     * to 3, the algorithm ends early once it reaches 3. This is possible as we are using this method to find the string
     * with the lowest Levenshtein distance.
     * <p></p>
     * Returns {@code Integer.MAX_VALUE} if limit is reached, else returns the computed Levenshtein distance.
     */
    public static int getLevenshteinDistance(String s, String t, double limit) {
        // Early termination if either string is empty, lev dist is just the length of the other string.
        if (s.isEmpty()) {
            return t.length();
        }

        if (t.isEmpty()) {
            return s.length();
        }

        // The final lev dist is at least k where k = difference in length = number of insert/delete.
        if (Math.abs(s.length() - t.length()) >= limit) {
            return Integer.MAX_VALUE;
        }

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
            // Store the value of the previous row's column
            int prev = dp[0];
            dp[0] = i;

            // If for this row, all the values are at least k, then the final lev dist computed will also be at least k.
            // hasLower will check for values smaller than the limit, and terminate early if limit is reached.
            boolean hasLower = false;

            for (int j = 1; j <= t.length(); j++) {
                int temp = dp[j];

                if (s.charAt(i - 1) == t.charAt(j - 1)) {
                    dp[j] = prev;
                } else {
                    dp[j] = Math.min(prev, Math.min(dp[j - 1], dp[j])) + 1;
                }

                prev = temp;

                if (dp[j] < limit) {
                    hasLower = true;
                }
            }

            if (!hasLower) {
                return Integer.MAX_VALUE;
            }
        }

        return dp[t.length()];
    }
}
