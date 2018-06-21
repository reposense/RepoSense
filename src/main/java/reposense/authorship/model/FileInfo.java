package reposense.authorship.model;

import java.util.ArrayList;
import java.util.List;

import reposense.model.Author;

/**
 * Stores the path to the file and the list of {@code LineInfo} for each line in the file.
 */
public class FileInfo {
    private final String path;
    private final ArrayList<LineInfo> lines;

    public FileInfo(String path) {
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

    public void setLineAuthor(int lineNumber, Author author) {
        lines.get(lineNumber).setAuthor(author);
    }
}
