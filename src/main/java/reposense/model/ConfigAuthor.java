package reposense.model;

public class ConfigAuthor {
    private String gitHubId;
    private String authorDisplayName;

    public ConfigAuthor(String gitHubId, String authorDisplayName) {
        this.gitHubId = gitHubId;
        this.authorDisplayName = authorDisplayName;
    }

    public String getGitHubId() {
        return gitHubId;
    }

    public String getAuthorDisplayName() {
        return authorDisplayName;
    }
}
