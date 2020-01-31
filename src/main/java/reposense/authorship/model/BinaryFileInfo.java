package reposense.authorship.model;

import java.util.ArrayList;
import java.util.List;

import reposense.model.Author;
import reposense.model.FileType;
import reposense.util.SystemUtil;

/**
 * Stores the path to the file.
 */
public class BinaryFileInfo {
    private final String path;

    private FileType fileType;

    public BinaryFileInfo(String path) {
        if (SystemUtil.isWindows()) {
            // Only replace \ to / in Windows paths, so it does not interferes with a correct Unix path
            path = path.replace('\\', '/');
        }

        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public FileType getFileType() {
        return fileType;
    }

    public void setFileType(FileType fileType) {
        this.fileType = fileType;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof BinaryFileInfo)) {
            return false;
        }

        BinaryFileInfo otherBinaryFileInfo = (BinaryFileInfo) other;
        return path.equals(otherBinaryFileInfo.path);
    }
}
