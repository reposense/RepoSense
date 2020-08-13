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
    private final Map<Author, LinkedHashMap<FileType, Integer>> authorFileTypeContributionMap;

    public AuthorshipSummary(List<FileResult> fileResults, List<Author> authors, List<FileType> fileTypes) {
        this.fileResults = fileResults;
        authorFileTypeContributionMap = new HashMap<>();

        for (Author author : authors) {
            LinkedHashMap<FileType, Integer> defaultFileTypeContribution = new LinkedHashMap<>();

            fileTypes.forEach(fileType -> defaultFileTypeContribution.put(fileType, 0));
            authorFileTypeContributionMap.put(author, defaultFileTypeContribution);
        }
    }

    /**
     * Increments the corresponding {@code fileType} contribution count of {@code author} by one.
     */
    public void addAuthorContributionCount(Author author, FileType fileType) {
        Map<FileType, Integer> fileTypeContributionMap = authorFileTypeContributionMap.get(author);
        fileTypeContributionMap.put(fileType, fileTypeContributionMap.getOrDefault(fileType, 0) + 1);
    }

    public Map<Author, LinkedHashMap<FileType, Integer>> getAuthorFileTypeContributionMap() {
        return authorFileTypeContributionMap;
    }

    public List<FileResult> getFileResults() {
        return fileResults;
    }
}
