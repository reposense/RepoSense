package reposense.authorship.model;

import java.util.*;

import reposense.model.Author;
import reposense.model.FileType;

/**
 * Stores the result from analyzing a {@code FileInfo}.
 */
public class FileResult {
    private final String path;
    private FileType fileType;
    private boolean isBinary;
    private final ArrayList<LineInfo> lines;
    private final HashMap<Author, Integer> authorContributionMap;
    private final Set<Author> binaryFileAuthors;

    public FileResult(String path, FileType fileType, ArrayList<LineInfo> lines,
            HashMap<Author, Integer> authorContributionMap, boolean isBinary, Set<Author> binaryFileAuthors) {
        this.path = path;
        this.fileType = fileType;
        this.lines = lines;
        this.authorContributionMap = authorContributionMap;
        this.isBinary = isBinary;
        this.binaryFileAuthors = binaryFileAuthors;
    }

    public Set<Author> getBinaryFileAuthors() {
        return binaryFileAuthors;
    }

    public static FileResult createNonBinaryFileResult(String path, FileType fileType, ArrayList<LineInfo> lines,
                                                       HashMap<Author, Integer> authorContributionMap) {
        return new FileResult(path, fileType, lines, authorContributionMap, false, new HashSet<>());
    }

    public static FileResult createBinaryFileResult(String path, FileType fileType, Set<Author> binaryFileAuthors) {
        HashMap<Author, Integer> authorContributionMap = new HashMap<>();
        binaryFileAuthors.forEach(author -> authorContributionMap.put(author, 1));
        return new FileResult(path, fileType, new ArrayList<>(), authorContributionMap,true, binaryFileAuthors);
    }

    public boolean isBinary() {
        return isBinary;
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
