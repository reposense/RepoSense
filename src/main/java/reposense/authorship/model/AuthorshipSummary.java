package reposense.authorship.model;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import reposense.model.Author;
import reposense.model.Format;
import reposense.model.Group;

/**
 * Stores the contribution summary of the authors in the repo.
 */
public class AuthorshipSummary {
    private final List<FileResult> fileResults;
    private final Map<Author, Integer> authorFinalContributionMap;
    private final Map<Author, LinkedHashMap<String, Integer>> authorFileTypeContributionMap;

    public AuthorshipSummary(List<FileResult> fileResults, List<Author> authors, List<Format> formats,
        List<Group> groups) {
        this.fileResults = fileResults;
        authorFinalContributionMap = new HashMap<>();
        authorFileTypeContributionMap = new HashMap<>();

        // initialise each author contribution to be 0
        authors.forEach((author) -> authorFinalContributionMap.put(author, 0));

        if (!groups.isEmpty()) {
            // group contribution
            authors.forEach((author) -> {
                LinkedHashMap<String, Integer> defaultGroupContribution = new LinkedHashMap<>();
                for (Group group : groups) {
                    defaultGroupContribution.put(group.toString(), 0);
                }
                defaultGroupContribution.put(Group.DEFAULT_GROUP, 0);
                authorFileTypeContributionMap.put(author, defaultGroupContribution);
            });
        } else {
            // file format contribution
            authors.forEach((author) -> {
                LinkedHashMap<String, Integer> defaultFileFormatContribution = new LinkedHashMap<>();
                for (Format format : formats) {
                    defaultFileFormatContribution.put(format.toString(), 0);
                }
                authorFileTypeContributionMap.put(author, defaultFileFormatContribution);
            });
        }
    }

    /**
     * Increments the contribution count of {@code author} and the corresponding file format specified by
     * {@code filePath} by one.
     */
    public void addAuthorContributionCount(Author author, String format) {
        authorFinalContributionMap.put(author, authorFinalContributionMap.get(author) + 1);

        // Add fileType contribution count
        Map<String, Integer> formatContributionMap = authorFileTypeContributionMap.get(author);
        formatContributionMap.put(format, formatContributionMap.getOrDefault(format, 0) + 1);
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
