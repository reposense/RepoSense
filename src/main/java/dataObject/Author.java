package dataObject;

/**
 * Created by matanghao1 on 29/5/17.
 */
public class Author {
    String gitID;

    public Author(String gitID) {
        this.gitID = gitID;
    }

    public String getGitID() {
        return gitID;
    }

    public void setGitID(String gitID) {
        this.gitID = gitID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Author author = (Author) o;

        return gitID != null ? gitID.equals(author.gitID) : author.gitID == null;
    }

    @Override
    public int hashCode() {
        return gitID != null ? gitID.hashCode() : 0;
    }

    @Override
    public String toString() {
        return gitID;
    }
}

