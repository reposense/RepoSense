package reposense.report;

import java.util.HashMap;

import reposense.authorship.model.FileResult;
import reposense.model.Author;

/**
 * Stores the result of individual contributions made to {@code filePath}.
 */
public class FileAuthorshipSummary {
    private final String filePath;
    private final HashMap<Author, Integer> authorshipSummaryMap;

    public FileAuthorshipSummary(FileResult fileResult) {
        this.filePath = fileResult.getPath();
        authorshipSummaryMap = new HashMap<>();
        authorshipSummaryMap.putAll(fileResult.getAuthorContributionMap());
    }
}
