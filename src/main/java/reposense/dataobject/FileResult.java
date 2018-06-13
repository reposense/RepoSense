package reposense.dataobject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Stores the git blame analysis result of the file.
 */
public class FileResult {
    private String path;
    private ArrayList<LineInfo> lines;
    private HashMap<Author, Integer> authorContributionMap;

    public FileResult(String path, ArrayList<LineInfo> lines, HashMap<Author, Integer> authorContributionMap) {
        this.path = path;
        this.lines = lines;
        this.authorContributionMap = authorContributionMap;
    }

    public LineInfo getLine(int idx) {
        return lines.get(idx);
    }

    public List<LineInfo> getLines() {
        return lines;
    }

    public String getPath() {
        return path;
    }
}
