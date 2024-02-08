package reposense.parser.exceptions;

/**
 * Signals that there is an exception when parsing a string.
 */
public class ParseException extends Exception {
    public ParseException(String message) {
        super(message);
    }
}
