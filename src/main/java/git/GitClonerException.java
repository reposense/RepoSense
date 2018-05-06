package git;


public class GitClonerException extends Exception {
    public GitClonerException(Exception e) {
        super(e.getMessage());
    }
}
