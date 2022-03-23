package reposense.parser;

/**
 * Signals that there is a problem in the header of csv config file.
 */
public class InvalidHeaderException extends Exception {
    public InvalidHeaderException(String message) {
        super(message);
    }
}
