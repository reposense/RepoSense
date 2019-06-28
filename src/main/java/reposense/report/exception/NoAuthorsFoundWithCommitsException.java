package reposense.report.exception;

/**
 * Signals that no authors were found with commits from a specific repository.
 */
public class NoAuthorsFoundWithCommitsException extends Exception {
    public NoAuthorsFoundWithCommitsException() {
        super();
    }
}
