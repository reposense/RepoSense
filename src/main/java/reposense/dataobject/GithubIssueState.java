package reposense.dataobject;

/**
 * Represents a github issue's state
 */
public class GithubIssueState {

    private String state;

    /**
     * Constructs a {@code GithubIssueState}.
     *
     * @param state A valid description.
     */
    public GithubIssueState(String state) {
        this.state = state;
    }
}
