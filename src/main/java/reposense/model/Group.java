package reposense.model;

import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a group in {@code RepoConfiguration}.
 */
public class Group {
    public static final String DEFAULT_GROUP = "other";
    private static final String GROUP_VALIDATION_REGEX = "[A-Za-z0-9]+";
    private static final String MESSAGE_ILLEGAL_GROUPS = "The provided group, %s, contains illegal characters.";

    private transient List<String> filePaths;
    private transient PathMatcher groupGlobMatcher;
    private transient String groupName;

    public Group(String groupName, List<String> filePaths) {
        validateGroup(groupName);
        this.groupName = groupName;
        this.filePaths = filePaths;
        setGroupGlobMatcher();
    }

    @Override
    public String toString() {
        return groupName;
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (this == other) {
            return true;
        }

        // instanceof handles null
        if (!(other instanceof Group)) {
            return false;
        }

        Group otherGroup = (Group) other;
        return this.groupName.equals(otherGroup.groupName);
    }

    @Override
    public int hashCode() {
        return groupName.hashCode();
    }

    /**
     * Checks that {@code value} is a valid group.
     * @throws IllegalArgumentException if {@code value} does not meet the criteria.
     */
    public static void validateGroup(String value) throws IllegalArgumentException {
        if (!value.matches(GROUP_VALIDATION_REGEX)) {
            throw new IllegalArgumentException(String.format(MESSAGE_ILLEGAL_GROUPS, value));
        }
    }

    public PathMatcher getGroupGlobMatcher() {
        return groupGlobMatcher;
    }

    public void setGroupGlobMatcher() {
        String globString = "glob:{" + String.join(",", filePaths) + "}";
        groupGlobMatcher = FileSystems.getDefault().getPathMatcher(globString);
    }
}
