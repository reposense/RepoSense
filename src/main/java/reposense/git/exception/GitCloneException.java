package reposense.git.exception;


public class GitCloneException extends Exception {
    public GitCloneException(Exception e) {
        super(e.getMessage());
    }
}
