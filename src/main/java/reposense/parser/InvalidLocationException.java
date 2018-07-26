package reposense.parser;

/**
 * Signals that location cannot be represented by {@code URL} or {@code Path}.
 */
public class InvalidLocationException extends ParseException {
    public InvalidLocationException(String message) {
        super(message);
    }
}
