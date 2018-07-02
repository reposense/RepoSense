package reposense.authorship.model;

import java.util.HashMap;
import java.util.List;

import reposense.model.Author;

/**
 * Stores the result from analyzing a {@code FileInfo}.
 */
public class FileResult {
    private final String path;
    private final List<LineInfo> lines;
    private final HashMap<Author, Integer> authorContributionMap;

    public FileResult(String path, List<LineInfo> lines, HashMap<Author, Integer> authorContributionMap) {
        this.path = path;
        this.lines = lines;
        this.authorContributionMap = authorContributionMap;
    }

    public List<LineInfo> getLines() {
        return lines;
    }

    public String getPath() {
        return path;
    }

    public HashMap<Author, Integer> getAuthorContributionMap() {
        return authorContributionMap;
    }

    @Override
    public boolean equals(Object obj) {
        // short circuit if same object
        if (obj == this) {
            return true;
        }

        // instanceof handles null
        if (!(obj instanceof FileResult)) {
            return false;
        }

        // state check
        FileResult other = (FileResult) obj;
        return this.path.equals(other.path)
                && this.getLines().equals(other.getLines());
    }
}
