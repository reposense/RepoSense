package reposense.git.exception;

/**
 * Signals that there is an exception when cloning a repo.
 */
public class GitCloneException extends Exception {
    public GitCloneException(Exception e) {
        super(e.getMessage());
    }
}
