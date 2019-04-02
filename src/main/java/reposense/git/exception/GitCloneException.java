package reposense.git.exception;

/**
 * Represents the exception of git clone
 */
public class GitCloneException extends Exception {
    public GitCloneException(Exception e) {
        super(e.getMessage());
    }
}
