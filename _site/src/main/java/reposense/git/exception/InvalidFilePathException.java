package reposense.git.exception;


/**
 * Signals that the repository contains file that cannot be cloned into a Windows system.
 */
public class InvalidFilePathException extends Exception {
    public InvalidFilePathException(String message) {
        super(message);
    }
}
