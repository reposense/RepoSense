package reposense.model;

import java.util.List;

public class StandaloneAuthor {
    private String githubId;
    private List<String> authorName;
    private List<String> displayName;

    public StandaloneAuthor(String githubId, List<String> authorName, List<String> displayName) {
        this.githubId = githubId;
        this.authorName = authorName;
        this.displayName = displayName;
    }

    public String getGithubId() {
        return githubId;
    }

    public List<String> getAuthorName() {
        return authorName;
    }

    public List<String> getDisplayName() {
        return displayName;
    }
}
