package reposense.authorship.model;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import reposense.model.Author;
import reposense.model.FileType;

/**
 * Stores the contribution summary of the authors in the repo.
 */
public class AuthorshipSummary {
    private final List<FileResult> fileResults;
    private final Map<Author, Integer> authorFinalContributionMap;
    private final Map<Author, LinkedHashMap<FileType, Integer>> authorFileTypeContributionMap;

    public AuthorshipSummary(List<FileResult> fileResults, List<Author> authors, List<FileType> fileTypes) {
        this.fileResults = fileResults;
        authorFinalContributionMap = new HashMap<>();
        authorFileTypeContributionMap = new HashMap<>();

        for (Author author : authors) {
            // initialise each author contribution to be 0
            authorFinalContributionMap.put(author, 0);

            LinkedHashMap<FileType, Integer> defaultFileTypeContribution = new LinkedHashMap<>();

            fileTypes.forEach(fileType -> defaultFileTypeContribution.put(fileType, 0));
            authorFileTypeContributionMap.put(author, defaultFileTypeContribution);
        }
    }

    /**
     * Increments the contribution count of {@code author} and the corresponding file type specified by
     * {@code filePath} by one.
     */
    public void addAuthorContributionCount(Author author, FileType fileType) {
        authorFinalContributionMap.put(author, authorFinalContributionMap.get(author) + 1);

        // Add file type contribution count
        Map<FileType, Integer> fileTypeContributionMap = authorFileTypeContributionMap.get(author);
        fileTypeContributionMap.put(fileType, fileTypeContributionMap.getOrDefault(fileType, 0) + 1);
    }

    public Map<Author, Integer> getAuthorFinalContributionMap() {
        return authorFinalContributionMap;
    }

    public Map<Author, LinkedHashMap<FileType, Integer>> getAuthorFileTypeContributionMap() {
        return authorFileTypeContributionMap;
    }

    public List<FileResult> getFileResults() {
        return fileResults;
    }
}
