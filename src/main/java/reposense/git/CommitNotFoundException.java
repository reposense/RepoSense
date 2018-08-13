package reposense.git;


/**
 * Signals that a expected commit cannot be found.
 */
public class CommitNotFoundException extends Exception {
    public CommitNotFoundException(String message) {
        super(message);
    }
}
