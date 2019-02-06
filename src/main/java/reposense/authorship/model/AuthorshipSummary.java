package reposense.authorship.model;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import reposense.model.Author;
import reposense.model.Format;

/**
 * Stores the contribution summary of the authors in the repo.
 */
public class AuthorshipSummary {
    private final List<FileResult> fileResults;
    private final HashMap<Author, Integer> authorFinalContributionMap;
    private final HashMap<Author, LinkedHashMap<String, Integer>> authorFileTypeContributionMap;

    public AuthorshipSummary(List<FileResult> fileResults, List<Author> authors, List<Format> formats) {
        this.fileResults = fileResults;
        authorFinalContributionMap = new HashMap<>();
        authorFileTypeContributionMap = new HashMap<>();

        // initialise each author contribution to be 0
        authors.forEach((author) -> authorFinalContributionMap.put(author, 0));
        authors.forEach((author) -> {
            LinkedHashMap<String, Integer> defaultFileTypeContribution = new LinkedHashMap<>();
            for (Format format : formats) {
                defaultFileTypeContribution.put(format.toString(), 0);
            }
            authorFileTypeContributionMap.put(author, defaultFileTypeContribution);
        });
    }

    /**
     * Increments the contribution count of {@code author} and of a specific fileType by one.
     */
    public void addAuthorContributionCount(Author author, String filePath) {
        authorFinalContributionMap.put(author, authorFinalContributionMap.get(author) + 1);

        LinkedHashMap<String, Integer> fileTypeContributionMap = authorFileTypeContributionMap.get(author);
        String fileType = filePath.substring(filePath.lastIndexOf('.') + 1);
        fileType = (fileType.isEmpty()) ? "others" : fileType;
        fileTypeContributionMap.put(fileType, fileTypeContributionMap.getOrDefault(fileType, 0) + 1);
    }

    public HashMap<Author, Integer> getAuthorFinalContributionMap() {
        return authorFinalContributionMap;
    }

    public HashMap<Author, LinkedHashMap<String, Integer>> getAuthorFileTypeContributionMap() {
        return authorFileTypeContributionMap;
    }

    public List<FileResult> getFileResults() {
        return fileResults;
    }
}
