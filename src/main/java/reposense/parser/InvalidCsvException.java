package reposense.parser;

/**
 * Signals that there is a major error in a CSV file (e.g. wrong number of columns, zero valid records).
 */
public class InvalidCsvException extends Exception {
    public InvalidCsvException(String message) {
        super(message);
    }
}
