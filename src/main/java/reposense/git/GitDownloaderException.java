package reposense.git;


public class GitDownloaderException extends Exception {
    public GitDownloaderException(Exception e) {
        super(e.getMessage());
    }
}
