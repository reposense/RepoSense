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
    private Boolean isBinary = null; // Should only be true or null to prevent it from being serialized
    private Boolean isIgnored = null;
    private final ArrayList<LineInfo> lines;
    private final HashMap<Author, Integer> authorContributionMap;

    public FileResult(String path, FileType fileType, ArrayList<LineInfo> lines,
            HashMap<Author, Integer> authorContributionMap, boolean isBinary, boolean isIgnored) {
        this.path = path;
        this.fileType = fileType;
        this.lines = lines;
        this.authorContributionMap = authorContributionMap;
        if (isBinary) {
            this.isBinary = true;
        } else if (isIgnored) { // isBinary and isIgnored are mutually exclusive
            this.isIgnored = true;
        }
    }

    public static FileResult createTextFileResult(String path, FileType fileType, ArrayList<LineInfo> lines,
            HashMap<Author, Integer> authorContributionMap, boolean isIgnored) {
        return new FileResult(path, fileType, lines, authorContributionMap, false, isIgnored);
    }

    public static FileResult createBinaryFileResult(String path, FileType fileType, HashMap<Author,
            Integer> authorContributionMap) {
        return new FileResult(path, fileType, new ArrayList<>(), authorContributionMap, true, false);
    }

    public boolean isBinary() {
        return isBinary != null;
    }

    public boolean isIgnored() {
        return isIgnored != null;
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

    public void clearLines() {
        this.lines.clear();
    }

    public HashMap<Author, Integer> getAuthorContributionMap() {
        return authorContributionMap;
    }
}
