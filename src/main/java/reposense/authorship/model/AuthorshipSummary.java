package reposense.authorship.model;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import reposense.model.Author;

/**
 * Stores the contribution summary of the authors in the repo.
 */
public class AuthorshipSummary {
    private final List<FileResult> fileResults;
    private final HashMap<Author, Integer> authorFinalContributionMap;
    private final HashMap<Author, LinkedHashMap<String, Integer>> authorGroupContributionMap;

    public AuthorshipSummary(List<FileResult> fileResults, List<Author> authors) {
        this.fileResults = fileResults;
        authorFinalContributionMap = new HashMap<>();
        authorGroupContributionMap = new HashMap<>();

        // initialise each author contribution to be 0
        authors.forEach((author) -> authorFinalContributionMap.put(author, 0));
        authors.forEach((author) -> authorGroupContributionMap.put(author, new LinkedHashMap<>()));
    }

    /**
     * Increments the contribution count of {@code author} by one.
     */
    public void addAuthorContributionCount(Author author, String group) {
        authorFinalContributionMap.put(author, authorFinalContributionMap.get(author) + 1);

        LinkedHashMap<String, Integer> groupContributionMap = authorGroupContributionMap.get(author);
        groupContributionMap.put(group, groupContributionMap.getOrDefault(group, 0) + 1);
    }

    public HashMap<Author, Integer> getAuthorFinalContributionMap() {
        return authorFinalContributionMap;
    }

    public HashMap<Author, LinkedHashMap<String, Integer>> getAuthorGroupContributionMap() {
        return authorGroupContributionMap;
    }

    public List<FileResult> getFileResults() {
        return fileResults;
    }
}
