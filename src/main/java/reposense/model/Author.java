package reposense.model;

public class Author {
    public static final String UNKNOWN_AUTHOR_GIT_ID = "-";
    public static final String UNSET_AUTHOR_GIT_ID = "";

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
    public boolean equals(Object obj) {
        // short circuit if same object
        if (obj == this) {
            return true;
        }

        // instanceof handles null
        if (!(obj instanceof Author)) {
            return false;
        }

        //state check
        Author other = (Author) obj;
        return this.gitId.equalsIgnoreCase(other.gitId);
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

