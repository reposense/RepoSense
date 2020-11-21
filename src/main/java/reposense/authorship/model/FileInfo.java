package reposense.authorship.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import reposense.model.Author;
import reposense.model.FileType;
import reposense.util.SystemUtil;

/**
 * Stores the path to the file and the list of {@code LineInfo} for each line in the file.
 */
public class FileInfo {
    private final String path;
    private final ArrayList<LineInfo> lines;

    private FileType fileType;

    public FileInfo(String path) {
        if (SystemUtil.isWindows()) {
            // Only replace \ to / in Windows paths, so it does not interferes with a correct Unix path
            path = path.replace('\\', '/');
        }

        this.path = path;
        lines = new ArrayList<>();
    }

    /**
     * Returns true if none of the {@code Author} in {@code listedAuthors} contributed to this file.
     */
    public boolean isAllAuthorsIgnored(List<Author> listedAuthors) {
        return lines.stream().noneMatch(line -> listedAuthors.contains(line.getAuthor()));
    }

    public LineInfo getLine(int num) {
        return lines.get(num - 1);
    }

    public void addLine(LineInfo line) {
        lines.add(line);
    }

    public ArrayList<LineInfo> getLines() {
        return lines;
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

    /**
     * Sets the {@code Author} of the {@code LineInfo} in {@code lineNumber} for this {@code FileInfo}.
     */
    public void setLineAuthor(int lineNumber, Author author) {
        lines.get(lineNumber).setAuthor(author);
    }

    /**
     * Sets the {@code lastModifiedDate} of the {@code LineInfo} in {@code lineNumber} for this {@code FileInfo}.
     */
    public void setLineLastModifiedDate(int lineNumber, Date lastModifiedDate) {
        lines.get(lineNumber).setLastModifiedDate(lastModifiedDate);
    }

    /**
     * Returns true if the {@code LineInfo} in {@code lineNumber} index is being tracked.
     */
    public boolean isFileLineTracked(int lineNumber) {
        return getLines().get(lineNumber).isTracked();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof FileInfo)) {
            return false;
        }

        FileInfo otherFileInfo = (FileInfo) other;
        return path.equals(otherFileInfo.path)
                && lines.equals(otherFileInfo.lines);
    }
}
