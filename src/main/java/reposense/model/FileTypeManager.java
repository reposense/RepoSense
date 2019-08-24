package reposense.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import reposense.system.LogsManager;

/**
 * {@code FileTypeManager} is responsible for holding a list of whitelisted formats and user-specified custom
 * groupings.
 */
public class FileTypeManager {
    private static final String DEFAULT_GROUP = "other";
    private static final FileType DEFAULT_GROUP_TYPE = new FileType(DEFAULT_GROUP, Collections.singletonList("**"));

    private List<FileType> formats;
    private List<FileType> groups;

    private final Logger logger = LogsManager.getLogger(this.getClass());

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
            String deducedFormat = tok[tok.length - 1];
            try {
                return new FileType(deducedFormat, Collections.singletonList("**" + deducedFormat));
            } catch (IllegalArgumentException iae) {
                logger.warning(String.format("Unable to determine the file format for the file \"%s\". "
                        + "This file will be treated as the file type \"other\".", fileName));
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
