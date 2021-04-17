package reposense.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import reposense.system.LogsManager;

/**
 * Contains strings related utilities.
 */
public class StringsUtil {

    private static final Pattern SPECIAL_SYMBOLS = Pattern.compile("[@;:&/\\\\!<>{}%#\"\\-='()\\[\\].+*?^$|]");
    private static final Logger logger = LogsManager.getLogger(StringsUtil.class);
    private static String encoding = "UTF-8";

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
        if (original.startsWith("\"") && original.endsWith("\"")) {
            return original.substring(1, original.length() - 1);
        }

        return original;
    }

    /**
     * Returns the decoded path string with %20 in original path string replaced by white space character
     */
    public static String decodeString(Path path) {
        try {
            return URLDecoder.decode(path.toString(), encoding);
        } catch (UnsupportedEncodingException e) {
            logger.log(Level.SEVERE, "The encoding " + encoding
                    + " is incompatible with the path " + path + ".");
            return "";
        }
    }

    public static void setEncoding(String encoding) {
        StringsUtil.encoding = encoding;
    }
}
