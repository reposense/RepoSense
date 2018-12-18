package reposense.git;

public class GitCheckerException extends Exception{
    public GitCheckerException(Exception e) {
        super(e.getMessage());
    }
}