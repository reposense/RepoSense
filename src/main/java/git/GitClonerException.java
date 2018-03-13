package git;

/**
 * Created by matanghao1 on 13/3/18.
 */
public class GitClonerException extends Exception {
    public GitClonerException(Exception e){
        super(e.getMessage());
    }
}
