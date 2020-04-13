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
    private final ArrayList<LineInfo> lines;
    private final HashMap<Author, Integer> authorContributionMap;

    public FileResult(String path, FileType fileType, ArrayList<LineInfo> lines,
            HashMap<Author, Integer> authorContributionMap, boolean isBinary) {
        this.path = path;
        this.fileType = fileType;
        this.lines = lines;
        this.authorContributionMap = authorContributionMap;
        if (isBinary) {
            this.isBinary = true;
        }
    }

    public static FileResult createNonBinaryFileResult(String path, FileType fileType, ArrayList<LineInfo> lines,
            HashMap<Author, Integer> authorContributionMap) {
        return new FileResult(path, fileType, lines, authorContributionMap, false);
    }

    public static FileResult createBinaryFileResult(String path, FileType fileType, HashMap<Author,
            Integer> authorContributionMap) {
        return new FileResult(path, fileType, new ArrayList<>(), authorContributionMap, true);
    }

    public boolean isBinary() {
        return isBinary != null;
    }

    public void setBinary(boolean binary) {
        isBinary = binary;
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
