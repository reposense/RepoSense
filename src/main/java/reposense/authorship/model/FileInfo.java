package reposense.authorship.model;

import java.util.ArrayList;
import java.util.List;

import reposense.model.Author;

/**
 * Stores the path to the file and the list of {@code LineInfo} for each line in the file.
 */
public class FileInfo {
    private String path;
    private List<LineInfo> lines;

    public FileInfo(String path) {
        this(path, new ArrayList<>());
    }

    public FileInfo(String path, List<LineInfo> lines) {
        this.path = path;
        this.lines = lines;
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

    public List<LineInfo> getLines() {
        return lines;
    }

    public void setLines(List<LineInfo> lines) {
        this.lines = lines;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setLineAuthor(int lineNumber, Author author) {
        lines.get(lineNumber).setAuthor(author);
    }

    @Override
    public boolean equals(Object obj) {
        // short circuit if same object
        if (obj == this) {
            return true;
        }

        // instanceof handles null
        if (!(obj instanceof FileInfo)) {
            return false;
        }

        // state check
        FileInfo other = (FileInfo) obj;
        return this.path.equals(other.path)
                && this.getLines().equals(other.getLines());
    }
}
