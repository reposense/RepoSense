package reposense.authorship;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

import reposense.authorship.model.AuthorshipSummary;
import reposense.authorship.model.FileResult;
import reposense.authorship.model.LineInfo;
import reposense.model.Author;

/**
 * Aggregates the file analysis results to get the contribution and issue summary for all authors.
 */
public class FileResultAggregator {

    /**
     * Returns the {@code AuthorshipSummary} generated from aggregating the {@code fileResults}.
     */
    public static HashMap<String, AuthorshipSummary> aggregateFileResult(List<FileResult> fileResults, List<Author> authors) {
        HashMap<String, AuthorshipSummary> authorContributionSummaries = new HashMap<>();
        for (FileResult fileResult : fileResults) {
            String[] elements = fileResult.getPath().split("\\.");
            String docType = elements[elements.length - 1];
            AuthorshipSummary authorContributionSummary =
                    authorContributionSummaries.get(docType) == null?
                            new AuthorshipSummary(new ArrayList<>(), authors)
                            : authorContributionSummaries.get(docType);
            authorContributionSummary.addFileResults(fileResult);
            for (LineInfo lineInfo : fileResult.getLines()) {
                Author author = lineInfo.getAuthor();
                if (!authors.contains(author)) {
                    continue;
                }
                authorContributionSummary.addAuthorContributionCount(author);
            }
            authorContributionSummaries.put(docType, authorContributionSummary);
        }
        return authorContributionSummaries;
    }
}
