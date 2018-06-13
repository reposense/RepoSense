package reposense.dataobject;

import java.util.ArrayList;
import java.util.List;


public class FileInfo {

    private String path;
    private ArrayList<LineInfo> lines = new ArrayList<>();

    public FileInfo(String path) {
        this.path = path;
    }

    public boolean isAllAuthorsIgnored(List<Author> listedAuthors) {
        return lines.stream().noneMatch(lineResult -> listedAuthors.contains(lineResult.getAuthor()));
    }

    public LineInfo getLine(int num) {
        return lines.get(num - 1);
    }

    public ArrayList<LineInfo> getLines() {
        return lines;
    }

    public void setLines(ArrayList<LineInfo> lines) {
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
}
