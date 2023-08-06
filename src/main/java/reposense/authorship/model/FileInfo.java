package reposense.authorship.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import reposense.model.Author;
import reposense.model.FileType;
import reposense.util.SystemUtil;

/**
 * Stores the path to the file, the list of {@code LineInfo} for each line in the file and file size.
 */
public class FileInfo {
    private final String path;
    private final ArrayList<LineInfo> lines;

    private FileType fileType;
    private long fileSize;
    private boolean exceedsFileLimit = false;
    private boolean isFileAnalyzed = true;

    public FileInfo(String path) {
        if (SystemUtil.isWindows()) {
            // Only replace \ to / in Windows paths, so it does not interferes with a correct Unix path
            path = path.replace('\\', '/');
        }

        this.path = path;
        lines = new ArrayList<>();
    }

    /**
     * Returns true if none of the {@link Author} in {@code listedAuthors} contributed to this file.
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

    public int getNumOfLines() {
        return lines.size();
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

    public long getFileSize() {
        return this.fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public void setFileAnalyzed(boolean isFileAnalyzed) {
        this.isFileAnalyzed = isFileAnalyzed;
    }

    public boolean isFileAnalyzed() {
        return isFileAnalyzed;
    }

    public boolean exceedsFileLimit() {
        return exceedsFileLimit;
    }

    public void setExceedsSizeLimit(boolean exceedsFileLimit) {
        this.exceedsFileLimit = exceedsFileLimit;
    }

    /**
     * Sets the {@code author} of the {@link LineInfo} in {@code lineNumber} for this {@link FileInfo}.
     */
    public void setLineAuthor(int lineNumber, Author author) {
        lines.get(lineNumber).setAuthor(author);
    }

    /**
     * Sets the {@code lastModifiedDate} of the {@link LineInfo} in {@code lineNumber} for this {@link FileInfo}.
     */
    public void setLineLastModifiedDate(int lineNumber, LocalDateTime lastModifiedDate) {
        lines.get(lineNumber).setLastModifiedDate(lastModifiedDate);
    }

    /**
     * Returns true if the {@link LineInfo} in {@code lineNumber} index is being tracked.
     */
    public boolean isFileLineTracked(int lineNumber) {
        return getLines().get(lineNumber).isTracked();
    }

    /**
     * Sets whether {@code lineNumber} is fully credited to its {@code author}.
     */
    public void setIsFullCredit(int lineNumber, boolean isFullCredit) {
        lines.get(lineNumber).setIsFullCredit(isFullCredit);
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
