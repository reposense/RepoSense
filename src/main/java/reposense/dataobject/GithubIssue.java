package reposense.dataobject;

import java.util.List;

/**
 * Represents a github issue
 */
public class GithubIssue {

    private final GithubIssueTitle title;
    private final List<GithubIssueLabels> labels;
    private final GithubIssueIndex index;
    private final GithubIssueState state;

    public GithubIssue(GithubIssueTitle title, List<GithubIssueLabels> labels,
                       GithubIssueIndex index, GithubIssueState state) {
        this.title = title;
        this.labels = labels;
        this.index = index;
        this.state = state;
    }

    public GithubIssueTitle getTitle() {
        return title;
    }

    public List<GithubIssueLabels> getLabels() {
        return labels;
    }

    public GithubIssueIndex getIndex() {
        return index;
    }

    public GithubIssueState getState() { return state; }
}
