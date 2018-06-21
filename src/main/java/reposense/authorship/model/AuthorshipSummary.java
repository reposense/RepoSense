package reposense.authorship.model;

import java.util.HashMap;
import java.util.List;

import reposense.model.Author;

/**
 * Stores the contribution summary of the authors in the repo.
 */
public class AuthorshipSummary {
    private final List<FileResult> fileResults;
    private final HashMap<Author, Integer> authorFinalContributionMap;

    public AuthorshipSummary(List<FileResult> fileResults, List<Author> authors) {
        this.fileResults = fileResults;
        authorFinalContributionMap = new HashMap<>();

        // initialise each author contribution to be 0
        authors.forEach((author) -> authorFinalContributionMap.put(author, 0));
    }

    /**
     * Increments the contribution count of {@code author} by one.
     */
    public void addAuthorContributionCount(Author author) {
        authorFinalContributionMap.put(author, authorFinalContributionMap.get(author) + 1);
    }

    public HashMap<Author, Integer> getAuthorFinalContributionMap() {
        return authorFinalContributionMap;
    }

    public List<FileResult> getFileResults() {
        return fileResults;
    }
}
