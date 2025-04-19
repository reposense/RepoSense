package reposense.parser.exceptions;

/**
 * Signals potential conflicts between CLI and CSV dates as well as ill-configured CSV dates.
 */
public class InvalidDatesException extends Exception {
    public InvalidDatesException(String message) {
        super(message);
    }
}
