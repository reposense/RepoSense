package reposense.dataObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class FileInfo {

    private String path;
    private ArrayList<LineInfo> lines = new ArrayList<>();
    private HashMap<Author, Integer> authorContributionMap = new HashMap<>();

    public FileInfo(String path) {
        this.path = path;
    }

    public FileInfo(String path, ArrayList<LineInfo> lines) {
        this.path = path;
        this.lines = lines;
    }

    public HashMap<Author, Integer> getAuthorContributionMap() {
        return authorContributionMap;
    }

    public void setAuthorContributionMap(HashMap<Author, Integer> authorContributionMap) {
        this.authorContributionMap = authorContributionMap;
    }

    public LineInfo getLineByNumber(int num) {
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

    public void constructAuthorContributionMap() {
        for (LineInfo line : lines) {
            Author author = line.getAuthor();
            if (!authorContributionMap.containsKey(author)) {
                authorContributionMap.put(author, 1);
            } else {
                authorContributionMap.put(author, authorContributionMap.get(author) + 1);
            }
        }
    }

    public boolean isAllAuthorsIgnored(List<Author> listedAuthors) {
        for (LineInfo line : lines) {
            if (listedAuthors.contains(line.getAuthor())) {
                return false;
            }
        }
        return true;
    }

}
