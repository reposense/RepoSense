package reposense.authorship.model;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import reposense.model.Author;
import reposense.model.Group;

/**
 * Stores the contribution summary of the authors in the repo.
 */
public class AuthorshipSummary {
    private final List<FileResult> fileResults;
    private final Map<Author, Integer> authorFinalContributionMap;
    private final Map<Author, LinkedHashMap<String, Integer>> authorFileTypeContributionMap;

    public AuthorshipSummary(List<FileResult> fileResults, List<Author> authors, List<Group> fileTypes,
            boolean hasCustomGroupings) {
        this.fileResults = fileResults;
        authorFinalContributionMap = new HashMap<>();
        authorFileTypeContributionMap = new HashMap<>();

        authors.forEach(author -> {
            // initialise each author contribution to be 0
            authorFinalContributionMap.put(author, 0);

            LinkedHashMap<String, Integer> defaultFileTypeContribution = new LinkedHashMap<>();
            for (Group fileType : fileTypes) {
                defaultFileTypeContribution.put(fileType.toString(), 0);
            }
            if (hasCustomGroupings) {
                defaultFileTypeContribution.put(Group.DEFAULT_GROUP, 0);
            }
            authorFileTypeContributionMap.put(author, defaultFileTypeContribution);
        });
    }

    /**
     * Increments the contribution count of {@code author} and the corresponding file type specified by
     * {@code filePath} by one.
     */
    public void addAuthorContributionCount(Author author, String fileType) {
        authorFinalContributionMap.put(author, authorFinalContributionMap.get(author) + 1);

        // Add file type contribution count
        Map<String, Integer> fileTypeContributionMap = authorFileTypeContributionMap.get(author);
        fileTypeContributionMap.put(fileType, fileTypeContributionMap.getOrDefault(fileType, 0) + 1);
    }

    public Map<Author, Integer> getAuthorFinalContributionMap() {
        return authorFinalContributionMap;
    }

    public Map<Author, LinkedHashMap<String, Integer>> getAuthorFileTypeContributionMap() {
        return authorFileTypeContributionMap;
    }

    public List<FileResult> getFileResults() {
        return fileResults;
    }
}
