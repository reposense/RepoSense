package reposense.dataobject;

import java.util.HashMap;
import java.util.List;

/**
 * Stores the contribution summary of the authors in the repo.
 */
public class AuthorContributionSummary {

    private final List<FileResult> fileResults;
    private final HashMap<Author, Integer> authorFinalContributionMap;

    public AuthorContributionSummary(List<FileResult> fileResults, List<Author> authors) {
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
}
