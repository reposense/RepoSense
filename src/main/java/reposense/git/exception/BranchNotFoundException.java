package reposense.git.exception;


/**
 * Signals that an expected branch cannot be found.
 */
public class BranchNotFoundException extends Exception {
    public BranchNotFoundException(Exception e) {
        super(e.getMessage());
    }
}
