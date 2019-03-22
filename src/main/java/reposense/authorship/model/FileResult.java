package reposense.authorship.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import reposense.model.Author;

/**
 * Stores the result from analyzing a {@code FileInfo}.
 */
public class FileResult {
    private final String path;
    private String format;
    private final ArrayList<LineInfo> lines;
    private final HashMap<Author, Integer> authorContributionMap;

    public FileResult(String path, String group, ArrayList<LineInfo> lines,
        HashMap<Author, Integer> authorContributionMap) {
        this.path = path;
        if (!"none".equals(group)) {
            this.format = group;
        } else {
            String fileExt = path.substring(path.lastIndexOf('.') + 1);
            this.format = (fileExt.length() == 0) ? "others" : fileExt;
        }
        this.lines = lines;
        this.authorContributionMap = authorContributionMap;
    }

    public List<LineInfo> getLines() {
        return lines;
    }

    public String getPath() {
        return path;
    }

    public String getFormat() {
        return format;
    }

    public HashMap<Author, Integer> getAuthorContributionMap() {
        return authorContributionMap;
    }
}
