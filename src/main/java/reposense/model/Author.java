package reposense.model;

import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;
import java.util.ArrayList;
import java.util.List;

public class Author {
    public static final String UNKNOWN_AUTHOR_GIT_ID = "-";

    private static final String MESSAGE_ILLEGAL_GIT_ID = "The provided Git ID, %s, contains illegal characters.";
    private static final String MESSAGE_ILLEGAL_DISPLAY_NAME =
            "The provided display name, %s, contains illegal characters.";
    private static final String MESSAGE_ILLEGAL_AUTHOR_ALIAS =
            "The provided author alias, %s,  contains illegal characters.";
    private static final String MESSAGE_UNCOMMON_GLOB_PATTERN =
            "The provided ignore glob, %s,  uses uncommon pattern.";

    private static final String COMMON_GLOB_REGEX = "^[-a-zA-Z0-9 _/\\\\*!{}\\[\\]!(),:.]*$";
    private static final String NAME_VALIDATION_REGEX = "^[-a-zA-Z0-9 _/\\\\*]+$";

    private String gitId;

    private transient String displayName;
    private transient List<String> authorAliases;
    private transient List<String> ignoreGlobList;
    private transient PathMatcher ignoreGlobMatcher;


    public Author(String gitId) {
        if (!isValidName(gitId)) {
            throw new IllegalArgumentException(String.format(MESSAGE_ILLEGAL_GIT_ID, gitId));
        }
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

        if (!isValidName(gitId)) {
            throw new IllegalArgumentException(String.format(MESSAGE_ILLEGAL_GIT_ID, gitId));
        }
        if (!isValidName(displayName)) {
            throw new IllegalArgumentException(String.format(MESSAGE_ILLEGAL_DISPLAY_NAME, displayName));
        }
        validateAuthorAliases(authorAliases);
        validateIgnoreGlobs(ignoreGlobList);

        this.gitId = gitId;
        this.displayName = displayName;
        this.authorAliases = authorAliases;
        this.ignoreGlobList = new ArrayList<>(ignoreGlobList);

        updateIgnoreGlobMatcher();
    }

    /**
     * Returns true if a given string is a valid name.
     */
    private static boolean isValidName(String name) {
        return name.matches(NAME_VALIDATION_REGEX);
    }

    /**
     * Returns true if all the strings in the {@code ignoreGlobList} only contains commonly used glob patterns.
     */
    private static boolean validateIgnoreGlobs(List<String> ignoreGlobList) {
        for (String glob: ignoreGlobList) {
            if (!glob.matches(COMMON_GLOB_REGEX)) {
                throw new IllegalArgumentException(String.format(MESSAGE_UNCOMMON_GLOB_PATTERN, glob));
            }
        }
        return true;
    }

    /**
     * Returns true if all the strings in the {@code authorAliases} are valid names.
     */
    private static boolean validateAuthorAliases(List<String> authorAliases) {
        for (String alias: authorAliases) {
            if (!isValidName(alias)) {
                throw new IllegalArgumentException(String.format(MESSAGE_ILLEGAL_AUTHOR_ALIAS, alias));
            }
        }
        return true;
    }

    public String getGitId() {
        return gitId;
    }

    public void setGitId(String gitId) {
        this.gitId = gitId;
    }

    public String getDisplayName() {
        return displayName;
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
        this.ignoreGlobList = ignoreGlobList;
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

