package reposense.model;

import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;
import java.util.ArrayList;
import java.util.List;

public class Author {
    public static final String UNKNOWN_AUTHOR_GIT_ID = "-";

    private String gitId;

    private transient List<String> authorAliases;
    private transient List<String> ignoreGlobList;
    private transient PathMatcher ignoreGlobMatcher;

    public Author(String gitId) {
        this.gitId = gitId;

        authorAliases = new ArrayList<>();
        ignoreGlobList = new ArrayList<>();

        updateIgnoreGlobMatcher();
    }

    public String getGitId() {
        return gitId;
    }

    public void setGitId(String gitId) {
        this.gitId = gitId;
    }

    public List<String> getAuthorAliases() {
        return authorAliases;
    }

    public void setAuthorAliases(List<String> authorAliases) {
        this.authorAliases = authorAliases;
    }

    public List<String> getIgnoreGlobList() {
        return ignoreGlobList;
    }

    public void setIgnoreGlobList(List<String> ignoreGlobList) {
        this.ignoreGlobList = ignoreGlobList;
        updateIgnoreGlobMatcher();
    }

    public PathMatcher getIgnoreGlobMatcher() {
        return ignoreGlobMatcher;
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (this == other) {
            return true;
        }

        // instanceof handles null
        if (!(other instanceof Author)) {
            return false;
        }

        Author otherAuthor = (Author) other;
        return this.gitId.equalsIgnoreCase(otherAuthor.gitId);
    }

    @Override
    public int hashCode() {
        return gitId != null ? gitId.toLowerCase().hashCode() : 0;
    }

    @Override
    public String toString() {
        return gitId;
    }

    /**
     * Called after a new ignore glob list is set.
     * Updates the {@code PathMatcher} to the new ignore glob list set.
     */
    private void updateIgnoreGlobMatcher() {
        String globString = "glob:{" + String.join(",", ignoreGlobList) + "}";
        ignoreGlobMatcher = FileSystems.getDefault().getPathMatcher(globString);
    }
}

