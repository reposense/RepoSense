package reposense.parser;

/**
 * Signals that location cannot be represented by {@code URL} or {@code Path}.
 */
public class InvalidLocationException extends Exception {
    public InvalidLocationException(String message) {
        super(message);
    }
}
