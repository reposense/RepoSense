package reposense.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * {@code FileTypeManager} is responsible for holding a list of whitelisted formats and user-specified custom
 * groupings.
 */
public class FileTypeManager {
    private static final String DEFAULT_GROUP = "other";

    private List<FileType> formats;
    private List<FileType> groups;


    public FileTypeManager() {
        formats = new ArrayList<>();
        groups = new ArrayList<>();
    }

    public String getFileType(String fileName) {
        if (hasCustomGroups()) {
            String fileTypeLabel = DEFAULT_GROUP;
            for (FileType group : groups) {
                if (group.isFileGlobMatching(fileName)) {
                    fileTypeLabel = group.getLabel();
                }
            }
            return fileTypeLabel;
        } else {
            return getFileFormat(fileName);
        }
    }

    private String getFileFormat(String fileName) {
        if (hasSpecifiedFormats()) {
            for (FileType format : formats) {
                if (format.isFileGlobMatching(fileName)) {
                    return format.getLabel();
                }
            }
            throw new RuntimeException("This should not have happened as the file should have been in the whitelisted"
                    + " list of formats.");
        } else {
            String[] tok = fileName.split("[./\\\\]+");
            return tok[tok.length -1];
        }
    }

    public List<String> getFileTypeLabels() {
        return hasCustomGroups()
                ? groups.stream().map(FileType::getLabel).collect(Collectors.toList())
                : formats.stream().map(FileType::getLabel).collect(Collectors.toList());
    }

    /**
     * Returns true if the {@code fileName}'s file type is inside {@code formatsWhiteList}.
     */
    public static boolean isInsideFormatsWhiteList(RepoConfiguration config, String fileName) {
        return config.getFormats().stream().anyMatch(fileType -> fileType.isFileGlobMatching(fileName));
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
