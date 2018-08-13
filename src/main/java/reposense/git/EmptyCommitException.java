package reposense.git;


/**
 * Signals that a expected commit cannot be found.
 */
public class EmptyCommitException extends Exception {
    public EmptyCommitException(String message) {
        super(message);
    }
}
