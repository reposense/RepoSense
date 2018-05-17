package reposense.dataobject;

/**
 * Represents a github issue's label
 */
public class GithubIssueLabels {

    private String label;

    /**
     * Constructs a {@code GithubIssueLabels}.
     *
     * @param label A valid description.
     */
    public GithubIssueLabels(String label) {
        this.label = label;
    }
}
