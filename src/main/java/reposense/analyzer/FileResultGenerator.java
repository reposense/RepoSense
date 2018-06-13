package reposense.analyzer;

import java.util.HashMap;

import reposense.dataobject.Author;
import reposense.dataobject.FileInfo;
import reposense.dataobject.FileResult;
import reposense.dataobject.LineInfo;

/**
 * Consolidates {@code FileInfo} data to generate a {@code FileResult}.
 */
public class FileResultGenerator {

    /**
     * Generates and returns a {@code FileResult} with the authorship results from {@code fileInfo} consolidated.
     */
    public static FileResult generateFileResult(FileInfo fileInfo) {
        HashMap<Author, Integer> authorContributionMap = new HashMap<>();
        for (LineInfo line: fileInfo.getLines()) {
            Author author = line.getAuthor();
            authorContributionMap.put(author, authorContributionMap.getOrDefault(author, 0) + 1);
        }
        return new FileResult(fileInfo.getPath(), fileInfo.getLines(), authorContributionMap);
    }
}
