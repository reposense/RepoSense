package reposense.model;

public class AuthorNameToGitId {
    private String authorName;
    private String gitId;

    public AuthorNameToGitId(String authorName, String gitId) {
        this.authorName = authorName;
        this.gitId = gitId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getGitId() {
        return gitId;
    }
}
