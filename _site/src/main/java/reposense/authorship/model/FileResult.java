package reposense.authorship.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import reposense.model.Author;
import reposense.model.FileType;

/**
 * Stores the result from analyzing a {@code FileInfo}.
 */
public class FileResult {
    private final String path;
    private FileType fileType;
    private final ArrayList<LineInfo> lines;
    private final HashMap<Author, Integer> authorContributionMap;

    public FileResult(String path, FileType fileType, ArrayList<LineInfo> lines,
            HashMap<Author, Integer> authorContributionMap) {
        this.path = path;
        this.fileType = fileType;
        this.lines = lines;
        this.authorContributionMap = authorContributionMap;
    }

    public List<LineInfo> getLines() {
        return lines;
    }

    public String getPath() {
        return path;
    }

    public FileType getFileType() {
        return fileType;
    }

    public HashMap<Author, Integer> getAuthorContributionMap() {
        return authorContributionMap;
    }
}
