package reposense.authorship.model;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import reposense.model.Author;
import reposense.model.Format;

/**
 * Stores the contribution summary of the authors in the repo.
 */
public class AuthorshipSummary {
    private final List<FileResult> fileResults;
    private final Map<Author, Integer> authorFinalContributionMap;
    private final Map<Author, LinkedHashMap<String, Integer>> authorFileFormatContributionMap;

    public AuthorshipSummary(List<FileResult> fileResults, List<Author> authors, List<Format> formats) {
        this.fileResults = fileResults;
        authorFinalContributionMap = new HashMap<>();
        authorFileFormatContributionMap = new HashMap<>();

        // initialise each author contribution to be 0
        authors.forEach((author) -> authorFinalContributionMap.put(author, 0));
        authors.forEach((author) -> {
            LinkedHashMap<String, Integer> defaultFileFormatContribution = new LinkedHashMap<>();
            for (Format format : formats) {
                defaultFileFormatContribution.put(format.toString(), 0);
            }
            authorFileFormatContributionMap.put(author, defaultFileFormatContribution);
        });
    }

    /**
     * Increments the contribution count of {@code author} and the corresponding file format specified by
     * {@code filePath} by one.
     */
    public void addAuthorContributionCount(Author author, String filePath) {
        authorFinalContributionMap.put(author, authorFinalContributionMap.get(author) + 1);

        Map<String, Integer> fileFormatContributionMap = authorFileFormatContributionMap.get(author);
        String fileFormat = Format.getFileFormat(filePath).toString();
        fileFormat = fileFormat.isEmpty() ? "others" : fileFormat;
        fileFormatContributionMap.put(fileFormat, fileFormatContributionMap.getOrDefault(fileFormat, 0) + 1);
    }

    public Map<Author, Integer> getAuthorFinalContributionMap() {
        return authorFinalContributionMap;
    }

    public Map<Author, LinkedHashMap<String, Integer>> getAuthorFileFormatContributionMap() {
        return authorFileFormatContributionMap;
    }

    public List<FileResult> getFileResults() {
        return fileResults;
    }
}
