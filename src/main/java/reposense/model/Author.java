package reposense.model;


public class Author {
    public static final String UNKNOWN_AUTHOR_GIT_ID = "-";

    private String gitId;

    public Author(String gitId) {
        this.gitId = gitId;
    }

    public String getGitId() {
        return gitId;
    }

    public void setGitId(String gitId) {
        this.gitId = gitId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Author author = (Author) o;

        return gitId != null ? gitId.toLowerCase().equals(author.gitId.toLowerCase()) : author.gitId == null;
    }

    @Override
    public int hashCode() {
        return gitId != null ? gitId.toLowerCase().hashCode() : 0;
    }

    @Override
    public String toString() {
        return gitId;
    }
}

