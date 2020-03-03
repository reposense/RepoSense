package reposense.git.exception;

/**
 * Signals that there is an exception when checking out a branch.
 */
public class GitBranchException extends Exception {
    public GitBranchException(Exception e) {
        super(e.getMessage());
    }
}
