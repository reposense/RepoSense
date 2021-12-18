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
    private final ArrayList<TextBlockInfo> blocks;
    private final HashMap<Author, Integer> authorContributionMap;

    public FileResult(String path, FileType fileType, ArrayList<LineInfo> lines, ArrayList<TextBlockInfo> blocks,
            HashMap<Author, Integer> authorContributionMap, boolean isBinary) {
        this.path = path;
        this.fileType = fileType;
        this.lines = lines;
        this.blocks = blocks;
        this.authorContributionMap = authorContributionMap;
        if (isBinary) {
            this.isBinary = true;
        }
    }

    public static FileResult createTextFileResult(String path, FileType fileType, ArrayList<LineInfo> lines,
                                                  ArrayList<TextBlockInfo> textBlocks, HashMap<Author, Integer> authorContributionMap) {
        return new FileResult(path, fileType, lines, textBlocks, authorContributionMap, false);
    }

    public static FileResult createBinaryFileResult(String path, FileType fileType, HashMap<Author,
            Integer> authorContributionMap) {
        return new FileResult(path, fileType, new ArrayList<>(), null, authorContributionMap, true);
    }

    public boolean isBinary() {
        return isBinary != null;
    }

    public List<LineInfo> getLines() {
        return lines;
    }

    public List<TextBlockInfo> getBlocks() {
        return blocks;
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
