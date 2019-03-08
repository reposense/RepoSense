package reposense.git.exception;


public class BranchNotFoundException extends Exception {
    public BranchNotFoundException(Exception e) {
        super(e.getMessage());
    }
}
