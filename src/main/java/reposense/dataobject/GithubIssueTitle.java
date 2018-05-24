package reposense.dataobject;

/**
 * Represents a github issue title
 */
public class GithubIssueTitle {

    private static String title;

    /**
     * Constructs a {@code GithubIssueTitle}.
     *
     * @param title A valid description.
     */
    public GithubIssueTitle(String title) {
        this.title = title;
    }
}
