package reposense.model;

import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class FileType {
    private List<Format> formats;
    private List<Group> groups;

    public static final String DEFAULT_GROUP = "other";

    private static final Pattern FILE_TYPE_VALIDATION_REGEX = Pattern.compile("[A-Za-z0-9]+");
    private static final String MESSAGE_ILLEGAL_FILE_TYPE = "The provided file type, %s, contains illegal characters.";

    public FileType() {
        formats = new ArrayList<>();
        groups = new ArrayList<>();
    }

    public FileType(List<Format> formats) {
        this.formats = new ArrayList<>(formats);
        groups = new ArrayList<>();
    }

    public String getFileType(String fileName) {
        if (hasCustomGroups()) {
            for (Group group : groups) {
                PathMatcher groupGlobMatcher = group.getGroupGlobMatcher();
                if (groupGlobMatcher.matches(Paths.get(fileName))) {
                    return group.toString();
                }
            }
            return DEFAULT_GROUP;
        } else {
            for (Format format : formats) {
                if (fileName.endsWith(format.toString())) {
                    return format.toString();
                }
            }
            return fileName;
        }
    }

    public List<String> getFileTypes() {
        return hasCustomGroups()
                ? groups.stream().map(Objects::toString).collect(Collectors.toList())
                : formats.stream().map(Objects::toString).collect(Collectors.toList());
    }

    /**
     * Returns true if the {@code fileName}'s file type is inside {@code formatsWhiteList}.
     */
    public static boolean isInsideFormatsWhiteList(RepoConfiguration config, String fileName) {
        return config.getFormats().stream().anyMatch(format -> fileName.endsWith(format.toString()));
    }

    public void setFormats(List<Format> formats) {
        this.formats = formats;
    }

    public List<Format> getFormats() {
        return formats;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public boolean hasSpecifiedFormats() {
        return !formats.isEmpty();
    }

    public boolean hasCustomGroups() {
        return !groups.isEmpty();
    }

    /**
     * Checks that the string {@code value} is a valid file type.
     * @throws IllegalArgumentException if {@code value} does not meet the criteria.
     */
    public void validateFileType(String value) {
        Matcher matcher = FILE_TYPE_VALIDATION_REGEX.matcher(value);
        if (!matcher.matches()) {
            throw new IllegalArgumentException(String.format(MESSAGE_ILLEGAL_FILE_TYPE, value));
        }
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (this == other) {
            return true;
        }

        // instanceof handles null
        if (!(other instanceof FileType)) {
            return false;
        }

        FileType otherFileType = (FileType) other;
        return this.groups.equals(otherFileType.groups) && this.formats.equals(otherFileType.formats);
    }
}
