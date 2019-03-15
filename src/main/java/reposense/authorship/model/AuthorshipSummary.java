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
    private final Map<Author, LinkedHashMap<String, Integer>> authorFileFormatContributionMap;
    private final Map<Author, LinkedHashMap<String, Integer>> authorGroupContributionMap;

    public AuthorshipSummary(List<FileResult> fileResults, List<Author> authors, List<Format> formats,
        List<Group> groups) {
        this.fileResults = fileResults;
        authorFinalContributionMap = new HashMap<>();
        authorFileFormatContributionMap = new HashMap<>();
        authorGroupContributionMap = new HashMap<>();

        // initialise each author contribution to be 0
        authors.forEach((author) -> authorFinalContributionMap.put(author, 0));

        // file format contribution
        authors.forEach((author) -> {
            LinkedHashMap<String, Integer> defaultFileFormatContribution = new LinkedHashMap<>();
            for (Format format : formats) {
                defaultFileFormatContribution.put(format.toString(), 0);
            }
            authorFileFormatContributionMap.put(author, defaultFileFormatContribution);
        });

        // group contribution
        if (!groups.isEmpty()) {
            authors.forEach((author) -> {
                LinkedHashMap<String, Integer> defaultGroupContribution = new LinkedHashMap<>();
                for (Group group : groups) {
                    defaultGroupContribution.put(group.toString(), 0);
                }
                defaultGroupContribution.put(Group.DEFAULT_GROUP, 0);
                authorGroupContributionMap.put(author, defaultGroupContribution);
            });
        } else {
            authors.forEach((author) -> authorGroupContributionMap.put(author, new LinkedHashMap<>()));
        }
    }

    /**
     * Increments the contribution count of {@code author} and the corresponding file format specified by
     * {@code filePath} by one.
     */

    public void addAuthorContributionCount(Author author, String filePath, String group) {
        authorFinalContributionMap.put(author, authorFinalContributionMap.get(author) + 1);

        // Add file format contribution count
        Map<String, Integer> fileFormatContributionMap = authorFileFormatContributionMap.get(author);
        String fileFormat = filePath.substring(filePath.lastIndexOf('.') + 1);
        fileFormat = fileFormat.isEmpty() ? "others" : fileFormat;
        fileFormatContributionMap.put(fileFormat, fileFormatContributionMap.getOrDefault(fileFormat, 0) + 1);

        // Add group contribution count
        if (group != null) {
            Map<String, Integer> groupContributionMap = authorGroupContributionMap.get(author);
            groupContributionMap.put(group, groupContributionMap.getOrDefault(group, 0) + 1);
        }
    }

    public Map<Author, Integer> getAuthorFinalContributionMap() {
        return authorFinalContributionMap;
    }

    public Map<Author, LinkedHashMap<String, Integer>> getAuthorGroupContributionMap() {
        return authorGroupContributionMap;
    }

    public Map<Author, LinkedHashMap<String, Integer>> getAuthorFileFormatContributionMap() {
        return authorFileFormatContributionMap;
    }

    public List<FileResult> getFileResults() {
        return fileResults;
    }
}
