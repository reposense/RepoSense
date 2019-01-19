package reposense.model;

import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Git Author.
 */
public class Author {

    public static final String UNKNOWN_AUTHOR_GIT_ID = "-";
    private static final String MESSAGE_UNCOMMON_GLOB_PATTERN = "The provided ignore glob, %s, uses uncommon pattern.";
    private static final String COMMON_GLOB_REGEX = "^[-a-zA-Z0-9 _/\\\\*!{}\\[\\]!(),:.]*$";

    private final String gitId;

    private transient String displayName;
    private transient List<String> authorAliases;
    private transient List<String> ignoreGlobList;
    private transient PathMatcher ignoreGlobMatcher;

    public Author(String gitId) {
        this.gitId = gitId;
        this.displayName = gitId;
        this.authorAliases = new ArrayList<>();
        this.ignoreGlobList = new ArrayList<>();

        updateIgnoreGlobMatcher();
    }

    public Author(StandaloneAuthor sa) {
        String gitId = sa.getGithubId();
        String displayName = !sa.getDisplayName().isEmpty() ? sa.getDisplayName() : sa.getGithubId();
        List<String> authorAliases = sa.getAuthorNames();
        List<String> ignoreGlobList = sa.getIgnoreGlobList();

        validateIgnoreGlobs(ignoreGlobList);

        this.gitId = gitId;
        this.displayName = displayName;
        this.authorAliases = authorAliases;
        this.ignoreGlobList = new ArrayList<>(ignoreGlobList);

        updateIgnoreGlobMatcher();
    }

    public Author(Author another) {
        this.gitId = another.gitId;
        this.displayName = another.gitId;
        this.authorAliases = another.authorAliases;
        this.ignoreGlobList = another.authorAliases;
        this.ignoreGlobMatcher = another.ignoreGlobMatcher;
    }

    /**
     * Checks that all the strings in the {@code ignoreGlobList} only contains commonly used glob patterns.
     * @throws IllegalArgumentException if any of the values do not meet the criteria.
     */
    private static void validateIgnoreGlobs(List<String> ignoreGlobList) throws IllegalArgumentException {
        for (String glob: ignoreGlobList) {
            if (!glob.matches(COMMON_GLOB_REGEX)) {
                throw new IllegalArgumentException(String.format(MESSAGE_UNCOMMON_GLOB_PATTERN, glob));
            }
        }
    }

    public String getGitId() {
        return gitId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
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
        validateIgnoreGlobs(ignoreGlobList);
        this.ignoreGlobList = new ArrayList<>(ignoreGlobList);
        updateIgnoreGlobMatcher();
    }

    public PathMatcher getIgnoreGlobMatcher() {
        return ignoreGlobMatcher;
    }

    public void appendIgnoreGlobList(List<String> ignoreGlobList) {
        validateIgnoreGlobs(ignoreGlobList);
        this.ignoreGlobList.addAll(ignoreGlobList);
        updateIgnoreGlobMatcher();
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
     * Updates the {@code PathMatcher} to the new ignore glob list set.
     * Called after a new ignore glob list is set.
     */
    private void updateIgnoreGlobMatcher() {
        String globString = "glob:{" + String.join(",", ignoreGlobList) + "}";
        ignoreGlobMatcher = FileSystems.getDefault().getPathMatcher(globString);
    }
}

