package reposense.git.exception;


/**
 * Signals that a expected commit cannot be found.
 */
public class CommitNotFoundException extends Exception {
    public CommitNotFoundException(String message) {
        super(message);
    }
}
