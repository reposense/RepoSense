package reposense.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * {@code FileTypeManager} is responsible for holding a list of whitelisted formats and user-specified custom
 * groupings.
 */
public class FileTypeManager {
    private static final String DEFAULT_GROUP = "other";
    private static final FileType DEFAULT_GROUP_TYPE = new FileType(DEFAULT_GROUP, Collections.singletonList("**"));

    private List<FileType> formats;
    private List<FileType> groups;

    public FileTypeManager(List<FileType> formats) {
        this.formats = formats;
        groups = new ArrayList<>();
    }

    /**
     * Returns the assigned file type of the file {@code fileName}.
     */
    public FileType getFileType(String fileName) {
        if (!hasCustomGroups()) {
            return getFileFormat(fileName);
        }

        FileType result = DEFAULT_GROUP_TYPE;
        for (FileType group : groups) {
            if (group.isFileGlobMatching(fileName)) {
                result = group;
            }
        }
        return result;
    }

    /**
     * Returns the file format of the given {@code fileName}.
     * Returns the file type "other" if the format of the file is not of the standard type.
     */
    private FileType getFileFormat(String fileName) {
        if (hasSpecifiedFormats()) {
            for (FileType format : formats) {
                if (format.isFileGlobMatching(fileName)) {
                    return format;
                }
            }
            throw new AssertionError(
                    "This exception should not happen as we have performed the whitelisted formats check.");
        } else {
            String[] tok = fileName.split("[./\\\\]+");
            String label = tok[tok.length - 1];

            try {
                FileType.validateFileFormat(label);
                return new FileType(label, Collections.singletonList("**" + label));
            } catch (IllegalArgumentException iae) {
                return DEFAULT_GROUP_TYPE;
            }
        }
    }

    public List<FileType> getAllFileTypes() {
        return hasCustomGroups() ? groups : formats;
    }

    /**
     * Returns true if the {@code fileName}'s file type is inside the list of specified formats to be analyzed.
     */
    public boolean isInsideWhitelistedFormats(String fileName) {
        return !hasSpecifiedFormats() || getFormats().stream().anyMatch(format -> format.isFileGlobMatching(fileName));
    }

    public List<FileType> getFormats() {
        return formats;
    }

    public void setFormats(List<FileType> formats) {
        this.formats = formats;
    }

    public boolean hasSpecifiedFormats() {
        return !formats.isEmpty();
    }

    public List<FileType> getGroups() {
        return groups;
    }

    /**
     * Adds new groups from {@code groupList}. Skips groups that have already been added previously.
     */
    public void addGroups(List<FileType> groupList) {
        groupList.stream().filter(group -> !this.containsGroup(group)).forEach(groups::add);
    }

    public boolean containsGroup(FileType group) {
        return groups.contains(group);
    }

    public void setGroups(List<FileType> groups) {
        this.groups = groups;
    }

    private boolean hasCustomGroups() {
        return !groups.isEmpty();
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (this == other) {
            return true;
        }

        // instanceof handles null
        if (!(other instanceof FileTypeManager)) {
            return false;
        }

        FileTypeManager otherFileType = (FileTypeManager) other;
        return this.groups.equals(otherFileType.groups) && this.formats.equals(otherFileType.formats);
    }
}
